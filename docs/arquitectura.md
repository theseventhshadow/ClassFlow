# Arquitectura

## Tabla de Contenidos

- [Principios Arquitectonicos](#principios-arquitectonicos)
- [Diagrama de Contexto (C1)](#diagrama-de-contexto-c1)
- [Diagrama de Contenedores (C2)](#diagrama-de-contenedores-c2)
- [Diagrama de Componentes (C3)](#diagrama-de-componentes-c3)
- [Flujo de Datos](#flujo-de-datos)
- [Comunicacion entre Servicios](#comunicacion-entre-servicios)
- [Patron Database per Service](#patron-database-per-service)
- [Arquitectura en Capas](#arquitectura-en-capas)
- [Modelo C4 de Referencia](#modelo-c4-de-referencia)

---

## Principios Arquitectonicos

ClassFlow se basa en los siguientes principios:

| Principio | Aplicacion |
|---|---|
| **Microservicios** | Cada dominio de negocio es un servicio independiente, desplegable y escalable por separado. |
| **Separacion en capas** | Cada servicio se organiza en presentacion, negocio y datos. |
| **Database per Service** | Cada microservicio con estado gestiona su propia base de datos PostgreSQL. |
| **API Gateway** | Unico punto de entrada para todas las peticiones del frontend. |
| **BFF (Backend-for-Frontend)** | Capa de integracion que agrega datos de microservicios para el frontend. |
| **Comunicacion REST** | Toda la comunicacion entre servicios es via HTTP/REST sincrono. |
| **Contenerizacion** | Todos los servicios se empaquetan en Docker y se orquestan con Docker Compose (local) o Kubernetes (produccion). |
| **Migraciones versionadas** | Los esquemas de base de datos se gestionan con Flyway. |

---

## Diagrama de Contexto (C1)

El diagrama C1 muestra a ClassFlow como un sistema central con el que interactuan cuatro tipos de actores a traves del frontend web.

```
+------------------+     +------------------+
|  Administrador   |     |    Docente       |
|  (gestiona       |     |  (asistencia,    |
|   usuarios,      |     |   calificaciones,|
|   cursos, config)|     |   anotaciones)   |
+--------+---------+     +--------+---------+
         |                        |
         |                        |
         v                        v
    +---------------------------------------------+
    |              ClassFlow                       |
    |  Sistema de Gestion Educativa                |
    |  (Frontend + API Gateway + BFF + 5 MS + 5 DB)|
    +---------------------------------------------+
         ^                        ^
         |                        |
+--------+---------+     +--------+---------+
|   Estudiante     |     |   Apoderado      |
|  (notas,         |     |  (supervision    |
|   asistencias,   |     |   de pupilos:    |
|   evaluaciones)  |     |   notas, asis.)  |
+------------------+     +------------------+
```

| Actor | Responsabilidad |
|---|---|
| **Administrador** | Gestiona usuarios, cursos y configuracion general del sistema. |
| **Docente** | Registra asistencia, calificaciones y anotaciones de estudiantes. |
| **Estudiante** | Consulta sus notas, asistencias y evaluaciones pendientes. |
| **Apoderado** | Supervisa el rendimiento academico y la asistencia de sus pupilos. |

Los cuatro actores acceden al sistema exclusivamente a traves del frontend web. No existe acceso directo al backend ni a las bases de datos.

---

## Diagrama de Contenedores (C2)

```
+-------------------------------------------------------------------+
|                        ClassFlow                                  |
|                                                                   |
|  +------------------+                                             |
|  |    Frontend      |  React 18 + TS + Vite                      |
|  |    (SPA)         |  Nginx :3000                               |
|  +--------+---------+                                             |
|           |                                                       |
|           | HTTPS                                                 |
|           v                                                       |
|  +------------------+                                             |
|  |   API Gateway    |  Spring Cloud Gateway :8080                |
|  |  (unico punto    |  CORS, enrutamiento                        |
|  |   de entrada)    |                                             |
|  +--------+---------+                                             |
|           |                                                       |
|           | enruta al BFF                                         |
|           v                                                       |
|  +------------------+                                             |
|  |      BFF         |  Spring WebFlux :8086                      |
|  |  (Backend-for-   |  Agregacion de datos                       |
|  |   Frontend)      |  WebClient reactivo                        |
|  +--+---+---+---+---+                                            |
|     |   |   |   |   |                                             |
|     |   |   |   |   |  REST                                      |
|     v   v   v   v   v                                             |
|  +--+ +--+ +--+ +--+ +--+                                        |
|  |Au| |Ac| |As| |Me| |No|   Microservicios de dominio           |
|  |th| |ad| |si| |ss| |ti|   (Spring Boot, JDK 25)               |
|  |  | |em| |st| |ag| |fi|                                        |
|  |81| |82| |83| |84| |85|                                        |
|  +-++ +-++ +-++ +-++ +-++                                        |
|    |    |    |    |    |                                          |
|    v    v    v    v    v                                          |
|  +--+ +--+ +--+ +--+ +--+                                        |
|  |au| |ac| |as| |me| |no|   Bases de datos PostgreSQL 15        |
|  |th| |ad| |si| |ss| |ti|   (Database per Service)              |
|  |-d| |-d| |st| |-d| |-d|                                        |
|  |b | |b | |-d| |b | |b |                                        |
|  +--+ +--+ |b | +--+ +--+                                        |
|            +--+                                                   |
+-------------------------------------------------------------------+
```

### Contenedores

| Contenedor | Tecnologia | Puerto | Descripcion |
|---|---|---|---|
| **Frontend** | React 18 + TypeScript + Vite + Nginx | 3000 | SPA con portales diferenciados por rol. |
| **API Gateway** | Spring Cloud Gateway (WebFlux) | 8080 | Unico punto de entrada. Enruta por path. Centraliza CORS. |
| **BFF** | Spring WebFlux | 8086 | Agrega datos de microservicios para el frontend. |
| **ms-auth** | Spring Boot + Security | 8081 | Autenticacion JWT, CRUD de usuarios. |
| **ms-academic** | Spring Boot | 8082 | Cursos, asignaturas, evaluaciones, calificaciones. |
| **ms-assistance** | Spring Boot | 8083 | Asistencia diaria y anotaciones de estudiantes. |
| **ms-message** | Spring Boot | 8084 | Mensajeria entre usuarios y anuncios por curso. |
| **ms-notification** | Spring Boot | 8085 | Notificaciones por email y alertas del sistema. |

---

## Diagrama de Componentes (C3)

Cada microservicio sigue una estructura interna de tres capas. A continuacion se toma `ms-academic` como referencia representativa.

```
+-------------------------------------------------------------------+
| ms-academic :8082                                                 |
|                                                                   |
|  +-------------------------------------------------------------+  |
|  | CAPA DE PRESENTACION                                        |  |
|  |                                                             |  |
|  |  +----------------+  +----------------+                     |  |
|  |  | CourseController| | SubjectController|                   |  |
|  |  | /api/courses   |  | /api/subjects   |                   |  |
|  |  +-------+--------+  +-------+--------+                     |  |
|  |          |                   |                              |  |
|  |  +-------+--------+  +-------+--------+                     |  |
|  |  |EvaluationCtrl  |  | GradeController |                   |  |
|  |  |/api/evaluations|  | /api/grades     |                   |  |
|  |  +-------+--------+  +-------+--------+                     |  |
|  +-------------------------------------------------------------+  |
|             |                   |                                 |
|  +----------+-------------------+-----------------------------+  |
|  | CAPA DE NEGOCIO             |                              |  |
|  |                             v                              |  |
|  |  +----------------+  +----------------+                     |  |
|  |  | CourseService  |  | SubjectService |                   |  |
|  |  +-------+--------+  +-------+--------+                     |  |
|  |          |                   |                              |  |
|  |  +-------+--------+  +-------+--------+                     |  |
|  |  |EvaluationSvc   |  | GradeService   |                   |  |
|  |  +-------+--------+  +-------+--------+                     |  |
|  +-------------------------------------------------------------+  |
|             |                   |                                 |
|  +----------+-------------------+-----------------------------+  |
|  | CAPA DE DATOS               |                              |  |
|  |                             v                              |  |
|  |  +----------------+  +----------------+                     |  |
|  |  |CourseRepository|  |SubjectRepo     |                   |  |
|  |  +-------+--------+  +-------+--------+                     |  |
|  |          |                   |                              |  |
|  |  +-------+--------+  +-------+--------+                     |  |
|  |  |EvaluationRepo  |  | GradeRepository |                   |  |
|  |  +-------+--------+  +-------+--------+                     |  |
|  +-----------------------------+-------------------------------+  |
|                                |                                  |
|                                v                                  |
|                 +----------------------------------+              |
|                 |  academic-db (PostgreSQL)        |              |
|                 |  courses, subjects, evaluations,  |              |
|                 |  grades                          |              |
|                 +----------------------------------+              |
|                                                                   |
|  Componentes transversales:                                       |
|  +------------------+ +----------------+ +------------------+    |
|  |GlobalException   | |SwaggerConfig   | | DTOs             |    |
|  |Handler           | |(OpenAPI)       | | (CourseDTO, etc) |    |
|  +------------------+ +----------------+ +------------------+    |
+-------------------------------------------------------------------+
```

### Capas

| Capa | Responsabilidad | Tecnologia |
|---|---|---|
| **Presentacion** | Recibe peticiones HTTP, delega en servicios, retorna DTOs. | `@RestController`, `@RequestMapping` |
| **Negocio** | Implementa reglas de negocio y orquestacion. | `@Service` |
| **Datos** | Acceso a base de datos y mapeo objeto-relacional. | `JpaRepository`, `@Entity` |
| **Transversal** | Manejo de errores, documentacion, contratos. | `@RestControllerAdvice`, OpenAPI, DTOs |

### Flujo de una peticion tipica

```
Cliente HTTP
    |
    v
Controller (@RestController)
    |  Recibe DTO de entrada, delega en Service
    v
Service (@Service)
    |  Valida reglas de negocio, llama a Repository
    v
Repository (JpaRepository)
    |  CRUD contra la base de datos
    v
Base de datos (PostgreSQL / H2)
    |
    v (respuesta)
Repository -> Service -> Controller -> DTO de salida -> Cliente HTTP
```

---

## Flujo de Datos

### Principio fundamental

El frontend **nunca** se comunica directamente con los microservicios de dominio. Su unica interfaz con el backend es el BFF, expuesto a traves del API Gateway.

```
Frontend -> API Gateway -> BFF -> Microservicios de dominio
```

### Flujo del dashboard

```
+---------+     +-------------+     +-----+     +---------------+
| Frontend | --> | API Gateway | --> | BFF | --> | ms-auth       |
| (React)  |     | :8080       |     |:8086|     | /users/{id}   |
+---------+     +-------------+     +-----+     +---------------+
                                        |            |
                                        |            v
                                        |     +---------------+
                                        |     | ms-academic   |
                                        |     | /courses,     |
                                        |     | /subjects,    |
                                        |     | /evaluations  |
                                        |     | /grades       |
                                        |     +---------------+
                                        |            |
                                        |            v
                                        |     +---------------+
                                        |     | ms-assistance |
                                        |     | /attendance,  |
                                        |     | /annotations  |
                                        |     +---------------+
                                        |            |
                                        |            v
                                        |     +---------------+
                                        |     | ms-message    |
                                        |     | /messages,    |
                                        |     | /announcements|
                                        |     +---------------+
                                        |            |
                                        |            v
                                        |     +---------------+
                                        |     | ms-notificat. |
                                        |     | /notifications |
                                        |     +---------------+
                                        |
                                        v
                                   Mono.zip() -> DashboardResponse
                                        |
                                        v
                                   Frontend
```

### BFF: DashboardService

El BFF consulta en paralelo a los 5 microservicios usando `Mono.zip` (WebFlux reactivo) y construye un unico `DashboardResponse`.

| Llamada | Microservicio | Endpoint |
|---|---|---|
| Usuario | ms-auth | `GET /api/auth/users/{userId}` |
| Cursos | ms-academic | `GET /api/courses` |
| Asignaturas | ms-academic | `GET /api/subjects` |
| Evaluaciones | ms-academic | `GET /api/evaluations` |
| Calificaciones | ms-academic | `GET /api/grades/student/{userId}` o `GET /api/grades` |
| Asistencias | ms-assistance | `GET /api/attendance/student/{userId}` o `GET /api/attendance` |
| Anotaciones | ms-assistance | `GET /api/annotations/student/{userId}` o `GET /api/annotations` |
| Mensajes | ms-message | `GET /api/messages/receiver/{userId}` |
| Mensajes no leidos | ms-message | `GET /api/messages/receiver/{userId}/unread` |
| Anuncios | ms-message | `GET /api/announcements/active` |
| Notificaciones | ms-notification | `GET /api/notifications/user/{userId}` |
| Pendientes | ms-notification | `GET /api/notifications/user/{userId}/pending` |

Los errores de cada llamada se manejan de forma aislada: si un microservicio falla, se retorna una lista vacia para esa seccion sin afectar al resto del dashboard.

---

## Comunicacion entre Servicios

### API Gateway: tabla de enrutamiento

El API Gateway (Spring Cloud Gateway) define las rutas en `application.yml`:

| ID de ruta | Path | Destino | Filtros |
|---|---|---|---|
| `bff` | `/api/bff/**` | `http://localhost:8086` | `StripPrefix=0` |
| `ms-auth` | `/api/auth/**` | `http://localhost:8081` | `StripPrefix=0` |
| `ms-academic-courses` | `/api/courses/**,/api/subjects/**,/api/evaluations/**,/api/grades/**` | `http://localhost:8082` | `StripPrefix=0` |
| `ms-assistance` | `/api/attendance/**,/api/annotations/**` | `http://localhost:8083` | `StripPrefix=0` |
| `ms-message` | `/api/messages/**,/api/announcements/**` | `http://localhost:8084` | `StripPrefix=0` |
| `ms-notification` | `/api/notifications/**` | `http://localhost:8085` | `StripPrefix=0` |

### BFF: WebClientConfig

El BFF define 5 beans de `WebClient` para comunicarse con los microservicios:

```yaml
services:
  auth:       base-url: http://localhost:8081
  academic:   base-url: http://localhost:8082
  assistance: base-url: http://localhost:8083
  message:    base-url: http://localhost:8084
  notification: base-url: http://localhost:8085
```

### Reglas de comunicacion

1. **Frontend -> API Gateway:** Unico punto de entrada. El frontend desconoce la existencia de los microservicios individuales.
2. **API Gateway -> BFF:** Las rutas `/api/bff/**` se redirigen al BFF.
3. **API Gateway -> Microservicios:** Solo las rutas de autenticacion (`/api/auth/**`) llegan directamente a `ms-auth` sin pasar por el BFF.
4. **BFF -> Microservicios:** El BFF consulta a los 5 microservicios via REST y consolida las respuestas.
5. **No hay comunicacion directa entre microservicios de dominio:** Toda la colaboracion entre dominios ocurre a traves del BFF.

---

## Patron Database per Service

Cada microservicio con estado gestiona su propia base de datos PostgreSQL con aislamiento total.

### Bases de datos

| Microservicio | Base de datos | Usuario | Volumen Docker |
|---|---|---|---|
| `ms-auth` | `auth_db` | `auth_user` | `auth-postgres-data` |
| `ms-academic` | `academic_db` | `academic_user` | `academic-postgres-data` |
| `ms-assistance` | `assistance_db` | `assistance_user` | `assistance-postgres-data` |
| `ms-message` | `message_db` | `message_user` | `message-postgres-data` |
| `ms-notification` | `notification_db` | `notification_user` | `notification-postgres-data` |

### Reglas del patron

- **Aislamiento total:** Ningun microservicio accede directamente a la base de datos de otro. No hay claves foraneas entre bases de datos ni consultas entre servicios.
- **Migraciones versionadas con Flyway:** Cada servicio gestiona su propio esquema con archivos SQL versionados. Flyway aplica las migraciones pendientes al iniciar.
- **Dos perfiles de ejecucion:**
  - `default`: H2 en memoria (desarrollo local sin Docker).
  - `docker`: PostgreSQL 15 (contenedores Docker y produccion).
- **Volumenes persistentes:** Cada base de datos tiene su propio volumen Docker nombrado. `docker compose down -v` elimina los volumenes y las migraciones se re-ejecutan desde cero.

### Dependencias en Docker Compose

```yaml
ms-auth:
  depends_on:
    auth-db:
      condition: service_healthy
```

Cada microservicio espera a que su base de datos responda `pg_isready` antes de iniciar. Ademas, cada servicio expone `/actuator/health` para healthchecks.

---

## Arquitectura en Capas

### Backend (por servicio)

Cada microservicio sigue esta estructura de paquetes:

```
com.ohiggins.classflow.{artefacto}/
  controller/       # @RestController, @RequestMapping
  service/          # @Service, logica de negocio
  repository/       # JpaRepository, @Query
  entity/           # @Entity, JPA
  dto/              # DTOs de entrada y salida (@Builder, record)
  config/           # SwaggerConfig, otra configuracion
  exception/        # GlobalExceptionHandler, ErrorResponse
  security/         # (solo ms-auth) SecurityConfig, JwtTokenProvider, etc.
```

### Frontend

```
src/
  config/           # Configuracion global (API base URL, timeout)
  constants/        # Rutas, HTTP status, localStorage keys
  types/            # Interfaces globales (ApiResponse, ApiError)
  context/          # AuthContext, ThemeContext
  services/         # ApiService (Axios), auth, user, dashboard, course
  hooks/            # useDashboardData, useAsync, useFetch, useForm
  components/
    common/         # Button, Input, Loading, Error, ProtectedRoute
    layout/         # Layout principal
  pages/            # Login, AdminDashboard, TeacherAccount, etc.
  styles/           # CSS global
  utils/            # formatters, validators, helpers
  router/           # Configuracion de rutas con react-router-dom
```

---

## Modelo C4 de Referencia

| Nivel | Diagrama | Descripcion |
|---|---|---|
| **C1 (Contexto)** | Sistema y actores | ClassFlow como caja negra con 4 tipos de usuarios. |
| **C2 (Contenedores)** | Frontend, Gateway, BFF, MS, DB | 14 contenedores que ejecutan el sistema. |
| **C3 (Componentes)** | Capas internas de cada MS | Controllers, Services, Repositories + componentes transversales. |
| **C4 (Codigo)** | Clases especificas | Detalle de cada clase Java y componente React. |

> **Nota:** Los diagramas C4 se pueden generar a partir de este documento usando herramientas como Mermaid, PlantUML o Structurizr. Ver `docs/diagramas/` para archivos fuente.
