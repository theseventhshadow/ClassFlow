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
│   ├── api-gateway/                # Spring Cloud Gateway (8080)
│   ├── bff/                        # Backend-for-Frontend (8086)
│   ├── ms-auth/                    # Autenticación y usuarios (8081)
│   ├── ms-academic/                # Cursos, evaluaciones, notas (8082)
│   ├── ms-assistance/              # Asistencia y anotaciones (8083)
│   ├── ms-message/                 # Mensajes y anuncios (8084)
│   ├── ms-notification/            # Notificaciones (8085)
│   └── k8s/                        # Manifiestos de Kubernetes
├── frontend/                       # React 18 + TypeScript + Vite
│   └── src/
│       ├── pages/                  # Login, AdminDashboard, TeacherDashboard,
│       │                           # StudentDashboard, GuardianDashboard
│       ├── services/ context/ hooks/ components/
│       ├── router/                 # Rutas protegidas por rol
│       └── config/ constants/ types/ utils/ styles/
└── docs/                           # Documentación detallada
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

Cada servicio expone además su propia documentación Swagger UI en `http://localhost:{puerto}/swagger-ui.html` cuando se ejecuta localmente.
