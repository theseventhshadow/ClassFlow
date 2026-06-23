# Seguridad

## Vista General

ClassFlow implementa seguridad en multiples capas: autenticacion mediante JWT, autorizacion basada en roles, cifrado de contrasenas, proteccion de rutas en el frontend y gestion de secretos. La siguiente tabla resume donde se aplica cada control:

| Control | Capa | Componente |
|---|---|---|
| Emision de JWT | Backend | `ms-auth` (Spring Security) |
| Validacion de JWT | Backend / Infra | `ms-auth` + Traefik ForwardAuth (K8s) |
| Cifrado de contrasenas | Backend | `ms-auth` (BCrypt) |
| Autorizacion por rol | Backend / Frontend | `SecurityConfig` + `ProtectedRoute` |
| CORS | Backend | `api-gateway` (CorsWebFilter) |
| TLS | Infraestructura | Traefik Ingress (Kubernetes) |
| Gestion de secretos | Infraestructura | `.env` (local), Secrets/ConfigMaps (K8s) |

---

## 1. Autenticacion con JWT

### Flujo de autenticacion

```
Cliente                       ms-auth                         Cliente
  |                             |                               |
  |-- POST /api/auth/login ---->|                               |
  |    { email, password }      |                               |
  |                             |-- Valida credenciales         |
  |                             |-- Genera JWT (HS256)          |
  |<-- { token, user } ---------|                               |
  |                             |                               |
  |-- GET /api/bff/dashboard ---|-------------------------------|
  |    Authorization: Bearer JWT|                               |
  |                             |                               |
  |     (Traefik ForwardAuth) --|--> GET /api/auth/validate     |
  |                             |<-- 200 OK / 401              |
  |                             |                               |
```

### JwtTokenProvider (ms-auth)

Clase responsable de la creacion y validacion de tokens JWT.

| Propiedad | Descripcion |
|---|---|
| Algoritmo | HS256 (HMAC-SHA256) |
| Clave | `jwt.secret` (minimo 32 caracteres, inyectada por variable de entorno) |
| Subject | Email del usuario |
| Emision | `issuedAt` = fecha actual |
| Expiracion | `expiration` = fecha actual + `jwt.expiration` (default: 86400000 ms = 24 horas) |

Metodos principales:

```java
// Genera un token a partir de la autenticacion de Spring Security
String generateToken(Authentication authentication);

// Extrae el email del subject del token
String getEmailFromToken(String token);

// Valida que el token sea valido (firma, expiracion, estructura)
boolean validateToken(String token);
```

### Configuracion de Spring Security (ms-auth)

```java
http
    .csrf(AbstractHttpConfigurer::disable)
    .sessionManagement(session ->
        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
    .authorizeHttpRequests(auth -> auth
        .requestMatchers("/api/auth/**").permitAll()
        .requestMatchers("/swagger-ui/**", "/swagger-ui.html",
                         "/v3/api-docs/**").permitAll()
        .anyRequest().authenticated()
    )
    .addFilterBefore(jwtAuthenticationFilter,
                     UsernamePasswordAuthenticationFilter.class);
```

| Aspecto | Configuracion |
|---|---|
| CSRF | Deshabilitado (API stateless) |
| Sesiones | Sin estado (STATELESS) |
| Endpoints publicos | `/api/auth/**`, `/swagger-ui/**`, `/v3/api-docs/**` |
| Filtro JWT | `JwtAuthenticationFilter` se ejecuta antes del filtro de autenticacion por usuario/contrasena |

### JwtAuthenticationFilter

Filtro que intercepta cada peticion entrante:

1. Extrae el token del header `Authorization: Bearer <token>`.
2. Valida el token mediante `JwtTokenProvider.validateToken()`.
3. Si es valido, carga el `UserDetails` desde `CustomUserDetailsService` y establece el `SecurityContext` de Spring Security.
4. Si es invalido o no existe, continua la cadena de filtros sin autenticar.

---

## 2. Autorizacion

### Roles del sistema

| Rol | Descripcion |
|---|---|
| `ADMINISTRATOR` | Acceso total al sistema: CRUD de usuarios y cursos, estadisticas globales |
| `TEACHER` | Gestion de cursos asignados, registro de asistencia, anotaciones y calificaciones |
| `STUDENT` | Consulta de notas, asistencias, evaluaciones proximas y anuncios |
| `GUARDIAN` | Supervision de estudiantes a cargo: calificaciones, asistencias y anotaciones |

### En el backend (BFF)

El BFF es consciente del rol del usuario. Al construir el dashboard:

- Si el rol es `STUDENT`, filtra las consultas de notas, asistencias y anotaciones por el ID del estudiante.
- Para los demas roles, retorna todos los registros disponibles.

### En el frontend (ProtectedRoute)

El componente `ProtectedRoute` en `src/components/common/ProtectedRoute.tsx` controla el acceso a las rutas:

1. Verifica que exista un token JWT en `localStorage`.
2. Valida el token contra `GET /api/auth/validate`.
3. Si el token es valido, verifica que el rol del usuario este incluido en `allowedRoles`.
4. Si no esta autorizado, redirige a `/access-denied`.
5. Mientras valida, muestra un indicador de carga (`Loading`).

```tsx
<ProtectedRoute allowedRoles={['ADMINISTRATOR']}>
    <AdminDashboard />
</ProtectedRoute>
```

---

## 3. Seguridad en el transporte

### Entorno local (Docker Compose)

Las peticiones viajan en texto plano dentro de la red interna `classflow-back-net`. El frontend se comunica con el API Gateway a traves del puerto 8080.

### Entorno Kubernetes

Existen dos variantes de ingreso, segun la version de Traefik:

| Archivo | Version de Traefik | Mecanismo de autenticacion |
|---|---|---|
| `ingress-traefik.yml` | v2 / v3 | Ingress estandar (sin auth integrado) |
| `ingress-traefik-v2-gateway.yml` | v2 | `IngressRoute` (enruta `/api` al gateway) |
| `ingress-traefik-v3-gateway-auth.yml` | v3 | `IngressRoute` + Middleware `forwardAuth` contra `ms-auth` |

#### ForwardAuth (Traefik v3)

El middleware `jwt-auth` intercepta las peticiones antes de que lleguen al API Gateway:

```
Request -> Traefik -> ForwardAuth -> ms-auth /api/auth/validate
                                     | 200 OK  -> continua al gateway
                                     | 401     -> rechaza la peticion
```

Configuracion del middleware:

```yaml
apiVersion: traefik.io/v1alpha1
kind: Middleware
metadata:
  name: jwt-auth
  namespace: classflow
spec:
  forwardAuth:
    address: http://ms-auth.classflow.svc.cluster.local:8081/api/auth/validate
    authResponseHeaders:
      - X-User-Id
      - X-User-Role
    trustForwardHeader: true
```

Las rutas publicas (login, register) se configuran con prioridad 20 para evitar que el middleware las intercepte:

```yaml
routes:
  - match: Host(`classflow.local`) && (PathPrefix(`/api/auth/login`)
           || PathPrefix(`/api/auth/register`))
    kind: Rule
    priority: 20
    services:
      - name: api-gateway
        port: 8080

  - match: Host(`classflow.local`) && PathPrefix(`/api`)
    kind: Rule
    priority: 10
    middlewares:
      - name: jwt-auth
    services:
      - name: api-gateway
        port: 8080
```

### CORS (API Gateway)

Configuracion global con `CorsWebFilter` (reactivo):

| Parametro | Valor |
|---|---|
| Origenes permitidos | Todos (`*`) |
| Metodos permitidos | Todos (`*`) |
| Headers permitidos | Todos (`*`) |
| Headers expuestos | `Authorization` |

---

## 4. Cifrado de contrasenas

`ms-auth` utiliza `BCryptPasswordEncoder` de Spring Security para el cifrado de contrasenas.

| Propiedad | Valor |
|---|---|
| Algoritmo | BCrypt |
| Fortaleza | Default (10 rounds) |
| Almacenamiento | Hash + salt incluido en el mismo string |

Cada vez que un usuario se registra o cambia su contrasena, el valor se cifra con BCrypt antes de persistirse. Nunca se almacena una contrasena en texto plano.

---

## 5. Gestion de secretos

### Principio

Ninguna credencial se incluye en el codigo fuente ni en el repositorio. Todas las variables sensibles se inyectan por configuracion externa.

### Entorno local

Las variables se definen en un archivo `.env` (basado en `.env.example`) que esta excluido del control de versiones via `.gitignore`.

```bash
# .env
JWT_SECRET=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970
JWT_EXPIRATION=86400000
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=tu_email@gmail.com
MAIL_PASSWORD=tu_contraseña_de_aplicacion
```

Docker Compose carga estas variables automaticamente desde el archivo `.env`:

```yaml
services:
  ms-auth:
    environment:
      - JWT_SECRET=${JWT_SECRET}
      - JWT_EXPIRATION=${JWT_EXPIRATION}
```

### Entorno Kubernetes

Las credenciales se inyectan mediante objetos `Secret` y `ConfigMap`:

| Secreto | Contenido |
|---|---|
| `classflow-db-secrets` | Usuarios, contrasenas y nombres de las 5 bases de datos (en base64) |
| `classflow-mail-secret` | Credenciales SMTP para el servicio de notificaciones |

Ejemplo de referencia en un Deployment:

```yaml
env:
  - name: JWT_SECRET
    valueFrom:
      secretKeyRef:
        name: classflow-jwt-secret
        key: jwt-secret
  - name: DB_PASSWORD
    valueFrom:
      secretKeyRef:
        name: classflow-db-secrets
        key: auth-db-password
```

**Nota:** Los valores en `classflow-db-secrets` estan codificados en base64 pero no cifrados. Son credenciales de demostracion que deben regenerarse para entornos productivos.

---

## 6. Seguridad en el frontend

| Mecanismo | Descripcion |
|---|---|
| Almacenamiento del token | `localStorage` (clave: `user_token`) |
| Envio del token | Interceptor de Axios agrega `Authorization: Bearer <token>` en cada peticion |
| Exclusion de endpoints publicos | `/auth/login`, `/auth/register`, `/auth/forgot-password` no incluyen token |
| Proteccion de rutas | `ProtectedRoute` valida token y rol antes de renderizar cada pagina |
| Cierre de sesion | `logout()` elimina token y datos del `localStorage` y limpia el estado global |

---

## 7. Resumen de vulnerabilidades mitigadas

| Vulnerabilidad | Mitigacion |
|---|---|
| Inyeccion SQL | JPA + Hibernate (consultas parametrizadas) |
| Cross-Site Request Forgery (CSRF) | CSRF deshabilitado (API stateless con JWT) |
| Cross-Origin Resource Sharing | CORS configurado en API Gateway |
| Exposicion de contrasenas | Cifrado con BCrypt |
| Token hijacking | Token JWT firmado (HS256), validacion en cada peticion |
| Acceso no autorizado a rutas | `ProtectedRoute` con verificacion de rol |
| Exposicion de secretos en repositorio | Variables de entorno, `.gitignore`, Secrets de Kubernetes |
| Man-in-the-Middle (MITM) | TLS en el Ingress de Kubernetes |
