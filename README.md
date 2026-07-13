# ClassFlow

ClassFlow es un sistema de gestión educativa con arquitectura de microservicios, diseñado para centralizar y automatizar procesos académicos como administración de usuarios, cursos, evaluaciones, asistencia, mensajería y notificaciones.

---

## Tabla de Contenidos

- [Stack Tecnológico](#stack-tecnologico)
- [Arquitectura](#arquitectura)
- [Estructura del Repositorio](#estructura-del-repositorio)
- [Ejecución Local](#ejecucion-local)
- [Despliegue en Kubernetes](#despliegue-en-kubernetes)
- [Documentación](#documentacion)

---

| Capa | Tecnología |
|---|---|
| Frontend | React 18, TypeScript 5.3, Vite, Nginx |
| Backend | Spring Boot 3.5.14, JDK 25 |
| Bases de datos | PostgreSQL 15, Flyway (migraciones) |
| Gateway | Spring Cloud Gateway (reactivo) |
| BFF | Spring WebFlux (reactivo) |
| Autenticación | JWT, BCrypt |
| Orquestación local | Docker Compose |
| Orquestación prod | Kubernetes, Traefik (Ingress) |

---

## Arquitectura

ClassFlow sigue una arquitectura de microservicios con separación estricta en capas (presentación, negocio, datos). El sistema se compone de **siete servicios** que se comunican vía REST:

```
Frontend (React SPA) -> API Gateway (8080) -> BFF (8086) -> Microservicios de dominio
```

### Servicios

| Servicio | Puerto | Base de datos | Propósito |
|---|---|---|---|
| `api-gateway` | 8080 | - | Punto de entrada único, enrutamiento y CORS |
| `bff` | 8086 | - | Agregación de datos para el frontend |
| `ms-auth` | 8081 | `auth-db` | Autenticación JWT y CRUD de usuarios |
| `ms-academic` | 8082 | `academic-db` | Cursos, asignaturas, evaluaciones y calificaciones |
| `ms-assistance` | 8083 | `assistance-db` | Asistencia diaria y anotaciones |
| `ms-message` | 8084 | `message-db` | Mensajería y anuncios por curso |
| `ms-notification` | 8085 | `notification-db` | Notificaciones por email y alertas |

### Flujo de datos

El frontend nunca consume los microservicios de forma directa. Su única interfaz con el backend es el **BFF**, expuesto a través del **API Gateway**. Cada microservicio de dominio gestiona su propia base de datos PostgreSQL (patrón Database per Service), con migraciones versionadas mediante Flyway.

### Seguridad

- `ms-auth` emite tokens JWT firmados. Las contraseñas se almacenan cifradas con BCrypt.
- El BFF valida el JWT antes de servir cualquier dato.
- En Kubernetes, Traefik termina TLS y el API Gateway centraliza la política de CORS.
- Las credenciales sensibles se inyectan vía variables de entorno (`.env` en local, Secrets/ConfigMaps en Kubernetes).

---

## Estructura del Repositorio

```
classflow/
├── backend/
│   ├── docker-compose.yml          # Orquestación local (14 contenedores)
│   ├── .env                        # Variables de entorno (JWT, API keys, etc.)
│   ├── README.md
│   │
│   ├── api-gateway/                # Spring Cloud Gateway (puerto 8080)
│   │   ├── Dockerfile, pom.xml
│   │   └── src/main/java/com/ohiggins/classflow/gateway/
│   │       ├── GatewayServiceApplication.java
│   │       ├── config/             # CorsConfig, SwaggerConfig
│   │       ├── security/           # SecurityConfig (filtro JWT)
│   │       └── exception/          # GlobalExceptionHandler, ErrorResponse
│   │
│   ├── bff/                        # Backend-for-Frontend (puerto 8086)
│   │   ├── Dockerfile, pom.xml
│   │   └── src/main/java/com/ohiggins/classflow/bff/
│   │       ├── BffServiceApplication.java
│   │       ├── controller/         # DashboardController
│   │       ├── service/            # DashboardService
│   │       ├── dto/                # DashboardResponse
│   │       ├── config/             # WebClientConfig, SwaggerConfig
│   │       ├── security/           # SecurityConfig (filtro JWT)
│   │       └── exception/          # GlobalExceptionHandler, ErrorResponse
│   │
│   ├── ms-auth/                    # Autenticación y usuarios (puerto 8081)
│   │   ├── Dockerfile, pom.xml
│   │   └── src/main/java/com/ohiggins/classflow/auth/
│   │       ├── controller/         # AuthController (login, register, validate...)
│   │       ├── service/            # AuthService, UserService, PasswordResetService
│   │       ├── repository/         # UserRepository
│   │       ├── entity/             # User, Role
│   │       ├── dto/                # LoginRequestDTO, LoginResponseDTO, etc.
│   │       ├── security/           # JwtTokenProvider, SecurityConfig
│   │       ├── config/             # SwaggerConfig
│   │       ├── exception/          # GlobalExceptionHandler, ErrorResponse
│   │       └── resources/db/migration/  # 6 migraciones Flyway
│   │
│   ├── ms-academic/                # Cursos, evaluaciones, notas (puerto 8082)
│   ├── ms-assistance/              # Asistencia y anotaciones (puerto 8083)
│   ├── ms-message/                 # Mensajes y anuncios (puerto 8084)
│   ├── ms-notification/            # Notificaciones (puerto 8085)
│   │   └── (misma estructura en capas: controller/service/repository/entity/
│   │        dto/security/config/exception)
│   │
│   └── k8s/                        # Manifiestos de Kubernetes
│       ├── namespace.yml, secret.yml, configmap.yml
│       ├── api-gateway.yml, bff.yml, frontend.yml
│       ├── ms-*.yml                # Un deployment por microservicio
│       ├── ingress-traefik*.yml
│       └── databases/              # Manifiestos de las 5 bases de datos
│
├── frontend/                       # React 18 + TypeScript + Vite
│   ├── package.json, tsconfig.json, vite.config.ts
│   ├── Dockerfile, nginx.conf
│   └── src/
│       ├── pages/                  # Login, ForgotPassword, ResetPassword,
│       │                           # AdminDashboard, TeacherAccountPage,
│       │                           # StudentDashboardPage, GuardianDashboardPage
│       ├── router/index.tsx        # Rutas protegidas por rol
│       ├── services/               # api, auth, user, dashboard, course,
│       │                           # grade, annotation, media
│       ├── context/                # AuthContext, ThemeContext
│       ├── hooks/                  # useDashboardData, useFetch, useForm,
│       │                           # useAsync, useLogout, useRawDashboard,
│       │                           # useTeacherCourseDetail
│       ├── components/             # Button, Input, Loading, Error,
│       │                           # ProtectedRoute, MediaImage, Layout
│       ├── config/ constants/ types/ utils/ styles/
│       └── main.tsx
│
└── docs/                           # Documentación del proyecto
    ├── arquitectura.md, microservicios.md, frontend.md
    ├── api.md, seguridad.md, infraestructura.md
    ├── pruebas.md, convenciones.md
    └── diagramas/                  # Fuentes C1, C2, C3
```

---

## Ejecución Local

### Prerrequisitos

- Docker y Docker Compose
- JDK 25 (para desarrollo sin Docker)
- Node.js 20+ (para desarrollo del frontend sin Docker)

### Con Docker Compose

```bash
cd backend
docker compose up -d
```

Esto levanta 14 contenedores: 5 bases de datos PostgreSQL, 5 microservicios, 1 BFF, 1 API Gateway y 1 frontend Nginx.

- Frontend: http://localhost:3000
- API Gateway: http://localhost:8080
- Glitchtip (error tracking): http://localhost:8000

> **Nota:** Glitchtip es un servicio de error tracking self-hospedado compatible con el SDK de Sentry. La primera vez que accedas a `http://localhost:8000` debes crear una cuenta, organización y proyecto, luego copiar el DSN generado a `SENTRY_DSN` en `backend/.env`.

### Sin Docker (desarrollo)

Cada microservicio se ejecuta individualmente con Maven. Usa el perfil `default` para conectar a H2 en memoria:

```bash
cd backend/ms-auth
./mvnw spring-boot:run
```

Para el frontend:

```bash
cd frontend
npm install
npm run dev
```

---

## Despliegue en Kubernetes

Los manifiestos se encuentran en `backend/k8s/`. El clúster aloja 12 pods en el namespace `classflow`:

```bash
kubectl apply -f backend/k8s/namespace.yml
kubectl apply -f backend/k8s/
```

Las bases de datos usan `StatefulSet` con almacenamiento persistente (`PersistentVolumeClaim`). El Ingress Controller (Traefik) expone el frontend al exterior y gestiona el certificado TLS.

---

## Documentación

Para información detallada, consultar los documentos en la carpeta `docs/`:

| Documento | Descripción |
|---|---|
| `docs/arquitectura.md` | Diagramas C1, C2, C3 y descripción completa de la arquitectura |
| `docs/microservicios.md` | Detalle de cada microservicio, endpoints y dependencias |
| `docs/frontend.md` | Estructura de componentes, rutas y portales por rol |
| `docs/seguridad.md` | Autenticación JWT, autorización y gestión de secretos |
| `docs/infraestructura.md` | Despliegue local con Docker Compose y Kubernetes |
| `docs/pruebas.md` | Cobertura de tests y estrategia de pruebas |
| `docs/api.md` | Documentación de APIs con Swagger/OpenAPI |
| `docs/convenciones.md` | Convenciones de código, naming y migraciones Flyway |
| `docs/glitchtip.md` | Error tracking con Glitchtip / Sentry |
| `docs/BackendREADME.md` | Documentación general del backend |
| `docs/FrontendREADME.md` | Documentación general del frontend |
| `docs/diagramas/` | Fuentes de diagramas C1, C2 y C3 |

Cada servicio expone además su propia documentación Swagger UI en `http://localhost:{puerto}/swagger-ui.html` cuando se ejecuta localmente.
