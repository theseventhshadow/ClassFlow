# Microservicios

## Vista General

ClassFlow esta compuesto por **siete servicios Java** desarrollados con Spring Boot 3.5.14 y JDK 25. Cada microservicio de dominio gestiona su propia base de datos PostgreSQL (patron Database per Service) con migraciones versionadas mediante Flyway. El BFF y el API Gateway no tienen base de datos propia.

| Servicio | Puerto | Tecnologia | Base de datos | Propósito |
|---|---|---|---|---|
| `api-gateway` | 8080 | Spring Cloud Gateway (WebFlux) | - | Punto de entrada unico, enrutamiento y CORS |
| `bff` | 8086 | Spring WebFlux (reactivo) | - | Agregacion de datos para el frontend |
| `ms-auth` | 8081 | Spring Boot Web + Security | `auth-db` | Autenticacion JWT y CRUD de usuarios |
| `ms-academic` | 8082 | Spring Boot Web | `academic-db` | Cursos, asignaturas, evaluaciones y calificaciones |
| `ms-assistance` | 8083 | Spring Boot Web | `assistance-db` | Asistencia diaria y anotaciones |
| `ms-message` | 8084 | Spring Boot Web | `message-db` | Mensajeria y anuncios por curso |
| `ms-notification` | 8085 | Spring Boot Web | `notification-db` | Notificaciones por email y alertas |

---

## 1. API Gateway (`api-gateway`)

**Puerto:** 8080
**Artefacto:** `api-gateway`
**Package base:** `com.ohiggins.classflow.gateway`
**Framework:** Spring Cloud Gateway (reactivo)

### Rutas definidas

Todas las rutas estan configuradas en `application.yml` mediante predicados y filtros de Spring Cloud Gateway.

| ID de ruta | Patron de path | Destino |
|---|---|---|
| `bff` | `/api/bff/**` | `http://localhost:8086` |
| `ms-auth` | `/api/auth/**` | `http://localhost:8081` |
| `ms-academic-courses` | `/api/courses/**` | `http://localhost:8082` |
| `ms-academic-subjects` | `/api/subjects/**` | `http://localhost:8082` |
| `ms-academic-evaluations` | `/api/evaluations/**` | `http://localhost:8082` |
| `ms-academic-grades` | `/api/grades/**` | `http://localhost:8082` |
| `ms-assistance-attendance` | `/api/attendance/**` | `http://localhost:8083` |
| `ms-assistance-annotations` | `/api/annotations/**` | `http://localhost:8083` |
| `ms-message-messages` | `/api/messages/**` | `http://localhost:8084` |
| `ms-message-announcements` | `/api/announcements/**` | `http://localhost:8084` |
| `ms-notification` | `/api/notifications/**` | `http://localhost:8085` |

### Clases de configuracion

| Clase | Descripcion |
|---|---|
| `config/CorsConfig.java` | Configuracion global CORS con `CorsWebFilter`. Permite todos los origenes, metodos y headers. Expone el header `Authorization`. |
| `config/SwaggerConfig.java` | OpenAPI con titulo "ClassFlow - API Gateway", esquema de seguridad JWT Bearer y servidores `localhost:8080` / `http://api-gateway:8080`. |

### Manejo de errores

| Clase | Descripcion |
|---|---|
| `exception/GlobalExceptionHandler.java` | Manejador reactivo con `@RestControllerAdvice`. `RuntimeException` -> 400, `Exception` -> 500. |
| `exception/ErrorResponse.java` | DTO con `@Builder`: `timestamp`, `status`, `error`, `message`, `path`. |

---

## 2. BFF - Backend for Frontend (`bff`)

**Puerto:** 8086
**Artefacto:** `bff`
**Package base:** `com.ohiggins.classflow.bff`
**Framework:** Spring WebFlux (reactivo)

### Endpoints

| Metodo | Ruta | Descripcion |
|---|---|---|
| `GET` | `/api/bff/dashboard/{userId}` | Retorna el dashboard completo de un usuario, agregando datos de los 5 microservicios |

### Estructura interna

```
bff/
  controller/
    DashboardController.java
  service/
    DashboardService.java
  dto/
    DashboardResponse.java
  config/
    WebClientConfig.java
    SwaggerConfig.java
  exception/
    GlobalExceptionHandler.java
    ErrorResponse.java
```

### DashboardResponse (record)

```java
public record DashboardResponse(
    JsonNode user,
    String role,
    List<JsonNode> courses,
    List<JsonNode> subjects,
    List<JsonNode> evaluations,
    List<JsonNode> grades,
    List<JsonNode> attendances,
    List<JsonNode> annotations,
    List<JsonNode> messages,
    Long unreadMessages,
    List<JsonNode> announcements,
    List<JsonNode> notifications,
    Long pendingNotifications
) {}
```

### Llamadas a microservicios

El `DashboardService` utiliza `Mono.zip` para consultar en paralelo a los 5 microservicios mediante `WebClient`:

| Microservicio | Endpoints consultados |
|---|---|
| `ms-auth` | `GET /api/auth/users/{userId}` |
| `ms-academic` | `GET /api/courses`, `GET /api/subjects`, `GET /api/evaluations` |
| `ms-academic` (grados) | `GET /api/grades/student/{userId}` o `GET /api/grades` |
| `ms-assistance` | `GET /api/attendance/student/{userId}` o `GET /api/attendance` |
| `ms-assistance` (annotaciones) | `GET /api/annotations/student/{userId}` o `GET /api/annotations` |
| `ms-message` | `GET /api/messages/receiver/{userId}`, `GET /api/messages/receiver/{userId}/unread` |
| `ms-message` (anuncios) | `GET /api/announcements/active` |
| `ms-notification` | `GET /api/notifications/user/{userId}`, `GET /api/notifications/user/{userId}/pending` |

**Comportamiento por rol:** Si el usuario es `STUDENT`, las consultas de notas, asistencias y anotaciones se filtran por su propio ID. En caso contrario, se retornan todos los registros. Los errores de cada llamada se manejan de forma aislada retornando listas vacias.

### WebClientConfig

Define 5 beans de `WebClient` con sus respectivas URLs base desde propiedades:

```yaml
services:
  auth:
    base-url: http://localhost:8081
  academic:
    base-url: http://localhost:8082
  assistance:
    base-url: http://localhost:8083
  message:
    base-url: http://localhost:8084
  notification:
    base-url: http://localhost:8085
```

---

## 3. MS-Auth (`ms-auth`)

**Puerto:** 8081
**Artefacto:** `ms-auth`
**Package base:** `com.ohiggins.classflow.auth`
**Framework:** Spring Boot Web + Spring Security + JWT

### Endpoints

#### AuthController (`/api/auth`)

| Metodo | Ruta | Descripcion | Respuestas |
|---|---|---|---|
| `POST` | `/api/auth/login` | Autentica usuario y retorna token JWT | `200` OK, `401` credenciales invalidas |
| `POST` | `/api/auth/register` | Crea un nuevo usuario | `201` creado, `409` email o RUT duplicado |
| `GET` | `/api/auth/validate` | Valida token JWT (header `Authorization`) | `200` valido, `401` invalido/expirado |
| `GET` | `/api/auth/users/{id}` | Obtiene usuario por ID | `200`, `404` |
| `GET` | `/api/auth/users/email/{email}` | Obtiene usuario por email | `200`, `404` |
| `GET` | `/api/auth/users/idnumber/{idNumber}` | Obtiene usuario por RUT | `200`, `404` |
| `GET` | `/api/auth/me` | Obtiene usuario actual desde el token | `200`, `401` |
| `PUT` | `/api/auth/users/{id}` | Actualiza datos de un usuario | `200` |
| `DELETE` | `/api/auth/users/{id}` | Eliminacion logica (soft-delete) de un usuario | `200` |
| `POST` | `/api/auth/change-password` | Cambia la contraseña del usuario | `200` |

### Entidad: User

| Campo | Tipo | Restricciones |
|---|---|---|
| `id` | `Long` | PK, autoincremental |
| `firstName` | `String` | @NotBlank |
| `lastName` | `String` | @NotBlank |
| `idNumber` | `String` | Unico, 9 caracteres (RUT chileno) |
| `email` | `String` | Unico, @Email |
| `password` | `String` | Cifrada con BCrypt |
| `role` | `Role` (enum) | ADMINISTRATOR, TEACHER, STUDENT, GUARDIAN |
| `course` | `String` | Nullable |
| `guardianId` | `Long` | Nullable, ID del apoderado |
| `active` | `boolean` | Default true |
| `createdAt` | `LocalDateTime` | @CreatedDate |

Implementa `UserDetails` de Spring Security. La autoridad se asigna como `ROLE_{role}`.

### Roles disponibles

| Rol | Descripcion |
|---|---|
| `ADMINISTRATOR` | Acceso total al sistema |
| `TEACHER` | Gestion de cursos, asistencia, calificaciones y anotaciones |
| `STUDENT` | Consulta de notas, asistencias y evaluaciones |
| `GUARDIAN` | Supervision de estudiantes a cargo |

### Seguridad

| Clase | Descripcion |
|---|---|
| `security/SecurityConfig.java` | Desactiva CSRF, sesiones sin estado (stateless). Permite `/api/auth/**` y Swagger. Anade `JwtAuthenticationFilter`. Beans: `AuthenticationManager`, `BCryptPasswordEncoder`. |
| `security/JwtTokenProvider.java` | Genera tokens JWT (HS256). Subject = email. Configurable via `jwt.secret` y `jwt.expiration`. Metodos: `generateToken()`, `getEmailFromToken()`, `validateToken()`. |
| `security/JwtAuthenticationFilter.java` | Extrae token Bearer del header `Authorization`, lo valida y establece el `SecurityContext`. |

### DTOs

| Clase | Campos clave |
|---|---|
| `LoginRequestDTO` | `email` (@Email @NotBlank), `password` (@NotBlank) |
| `LoginResponseDTO` | `id`, `token`, `type` ("Bearer"), `email`, `role`, `fullName` |
| `RegisterRequestDTO` | `firstName`, `lastName`, `idNumber` (regex RUT chileno), `email`, `password` (min 6), `role`, `course` (opt), `guardianId` (opt) |
| `UserResponseDTO` | `id`, `firstName`, `lastName`, `fullName`, `idNumber`, `email`, `role`, `course`, `active` (@Builder) |
| `UpdateUserRequestDTO` | `firstName`, `lastName`, `email`, `course` |
| `ChangePasswordRequestDTO` | `currentPassword`, `newPassword` |

### Repositorio: UserRepository

Metodos adicionales ademas de los CRUD estandar de `JpaRepository`:

- `findByEmail(String email)`
- `findByIdNumber(String idNumber)`
- `existsByEmail(String email)`
- `existsByIdNumber(String idNumber)`
- `findByRole(Role role)`
- `findByCourse(String course)`
- `findByGuardianId(Long guardianId)`

---

## 4. MS-Academic (`ms-academic`)

**Puerto:** 8082
**Artefacto:** `ms-academic`
**Package base:** `com.ohiggins.classflow.academic`
**Entidades:** Course, Subject, Evaluation, Grade

### Endpoints

#### CourseController (`/api/courses`)

| Metodo | Ruta | Descripcion |
|---|---|---|
| `GET` | `/api/courses` | Lista todos los cursos |
| `GET` | `/api/courses/{id}` | Obtiene un curso por ID |
| `POST` | `/api/courses` | Crea un nuevo curso |
| `PUT` | `/api/courses/{id}` | Actualiza un curso |
| `DELETE` | `/api/courses/{id}` | Eliminacion logica (soft-delete) |

#### SubjectController (`/api/subjects`)

| Metodo | Ruta | Descripcion |
|---|---|---|
| `GET` | `/api/subjects` | Lista todas las asignaturas |
| `GET` | `/api/subjects/course/{courseId}` | Asignaturas de un curso |
| `GET` | `/api/subjects/{id}` | Obtiene una asignatura por ID |
| `POST` | `/api/subjects` | Crea una nueva asignatura |
| `PUT` | `/api/subjects/{id}` | Actualiza una asignatura |
| `DELETE` | `/api/subjects/{id}` | Eliminacion logica (soft-delete) |

#### EvaluationController (`/api/evaluations`)

| Metodo | Ruta | Descripcion |
|---|---|---|
| `GET` | `/api/evaluations` | Lista todas las evaluaciones |
| `GET` | `/api/evaluations/subject/{subjectId}` | Evaluaciones de una asignatura |
| `GET` | `/api/evaluations/{id}` | Obtiene una evaluacion por ID |
| `POST` | `/api/evaluations` | Crea una nueva evaluacion |
| `PUT` | `/api/evaluations/{id}` | Actualiza una evaluacion |
| `DELETE` | `/api/evaluations/{id}` | Eliminacion fisica (hard-delete) |

#### GradeController (`/api/grades`)

| Metodo | Ruta | Descripcion |
|---|---|---|
| `GET` | `/api/grades` | Lista todas las calificaciones |
| `GET` | `/api/grades/student/{studentId}` | Calificaciones de un estudiante |
| `GET` | `/api/grades/evaluation/{evaluationId}` | Calificaciones de una evaluacion |
| `GET` | `/api/grades/{id}` | Obtiene una calificacion por ID |
| `POST` | `/api/grades` | Crea una calificacion (valida score <= maxScore) |
| `PUT` | `/api/grades/{id}` | Actualiza una calificacion |
| `DELETE` | `/api/grades/{id}` | Eliminacion fisica (hard-delete) |

### Modelo de datos

```
Course (1) ---- (N) Subject (1) ---- (N) Evaluation (1) ---- (N) Grade
```

| Entidad | Campos | Relaciones |
|---|---|---|
| `Course` | `id`, `name` (unico), `description`, `academicYear`, `active` | - |
| `Subject` | `id`, `name`, `description`, `active` | `@ManyToOne -> Course` |
| `Evaluation` | `id`, `name`, `description`, `maxScore`, `percentage`, `date` (LocalDate) | `@ManyToOne -> Subject` |
| `Grade` | `id`, `studentId`, `score`, `observations` | `@ManyToOne -> Evaluation` |

### Reglas de negocio

- **Course:** El nombre debe ser unico. Soft-delete (activo/inactivo).
- **Subject:** Vinculada a un Course. Soft-delete.
- **Evaluation:** Vinculada a un Subject. Hard-delete. Incluye `percentage` (ponderacion) y `maxScore` (nota maxima).
- **Grade:** Vinculada a una Evaluation. Valida que `score <= maxScore`. Hard-delete.

---

## 5. MS-Assistance (`ms-assistance`)

**Puerto:** 8083
**Artefacto:** `ms-assistance`
**Package base:** `com.ohiggins.classflow.assistance`
**Entidades:** Attendance, Annotation

### Endpoints

#### AttendanceController (`/api/attendance`)

| Metodo | Ruta | Descripcion |
|---|---|---|
| `GET` | `/api/attendance` | Lista todos los registros de asistencia |
| `GET` | `/api/attendance/student/{studentId}` | Asistencia de un estudiante |
| `GET` | `/api/attendance/course/{courseId}/date/{date}` | Asistencia de un curso en una fecha |
| `POST` | `/api/attendance/register` | Registra asistencia (evita duplicados por estudiante+fecha) |
| `PUT` | `/api/attendance/{id}` | Actualiza un registro de asistencia |

#### AnnotationController (`/api/annotations`)

| Metodo | Ruta | Descripcion |
|---|---|---|
| `GET` | `/api/annotations` | Lista todas las anotaciones |
| `GET` | `/api/annotations/student/{studentId}` | Anotaciones de un estudiante |
| `GET` | `/api/annotations/student/{studentId}/type/{type}` | Anotaciones filtradas por tipo (POSITIVE/NEGATIVE) |
| `POST` | `/api/annotations` | Crea una nueva anotacion |
| `DELETE` | `/api/annotations/{id}` | Eliminacion logica (soft-delete) |

### Entidades

| Entidad | Campos | Detalles |
|---|---|---|
| `Attendance` | `id`, `studentId`, `courseId`, `date` (LocalDate), `present`, `justification` | Evita duplicados (mismo studentId + date) |
| `Annotation` | `id`, `studentId`, `teacherId`, `type` ("POSITIVE"/"NEGATIVE"), `description`, `date` (LocalDateTime), `active` | Soft-delete. Fecha se asigna automaticamente al crear. |

### DTOs de entrada

| Clase | Campos |
|---|---|
| `AttendanceRequestDTO` | `studentId` (@NotNull), `courseId` (@NotNull), `date` (@NotNull), `present`, `justification` |
| `AnnotationRequestDTO` | `studentId`, `teacherId`, `type` (@NotBlank), `description` (@NotBlank) |

### Repositorios

| Repositorio | Metodos adicionales |
|---|---|
| `AttendanceRepository` | `findByStudentId()`, `findByCourseIdAndDate()`, `findByStudentIdAndDateBetween()` |
| `AnnotationRepository` | `findByStudentId()`, `findByStudentIdAndType()`, `findByTeacherId()` |

---

## 6. MS-Message (`ms-message`)

**Puerto:** 8084
**Artefacto:** `ms-message`
**Package base:** `com.ohiggins.classflow.message`
**Entidades:** Message, Announcement

### Endpoints

#### MessageController (`/api/messages`)

| Metodo | Ruta | Descripcion |
|---|---|---|
| `GET` | `/api/messages` | Lista todos los mensajes |
| `GET` | `/api/messages/receiver/{receiverId}` | Mensajes recibidos por un usuario |
| `GET` | `/api/messages/sender/{senderId}` | Mensajes enviados por un usuario |
| `GET` | `/api/messages/receiver/{receiverId}/unread` | Mensajes no leidos de un usuario |
| `POST` | `/api/messages/send` | Envia un nuevo mensaje |
| `PUT` | `/api/messages/{id}/read` | Marca un mensaje como leido |
| `DELETE` | `/api/messages/{id}` | Eliminacion fisica (hard-delete) |

#### AnnouncementController (`/api/announcements`)

| Metodo | Ruta | Descripcion |
|---|---|---|
| `GET` | `/api/announcements` | Lista todos los anuncios |
| `GET` | `/api/announcements/active` | Anuncios activos |
| `GET` | `/api/announcements/course/{courseId}` | Anuncios de un curso |
| `POST` | `/api/announcements` | Crea un nuevo anuncio |
| `DELETE` | `/api/announcements/{id}` | Eliminacion logica (soft-delete) |

### Entidades

| Entidad | Campos | Detalles |
|---|---|---|
| `Message` | `id`, `senderId`, `receiverId`, `subject`, `body`, `read`, `sentAt` | `read=false` al crear, `sentAt` se asigna automaticamente |
| `Announcement` | `id`, `title`, `content`, `courseId` (nullable = todos), `senderId`, `publishedAt`, `active` | `courseId=null` significa visible para todos los cursos |

### Reglas de negocio

- **Message:** Al enviar se establece `read=false` y `sentAt=LocalDateTime.now()`. Hard-delete.
- **Announcement:** Si `courseId` es null, el anuncio es global (visible para todos). Soft-delete.

---

## 7. MS-Notification (`ms-notification`)

**Puerto:** 8085
**Artefacto:** `ms-notification`
**Package base:** `com.ohiggins.classflow.notification`
**Entidades:** Notification

### Endpoints

#### NotificationController (`/api/notifications`)

| Metodo | Ruta | Descripcion |
|---|---|---|
| `POST` | `/api/notifications/email` | Envia una notificacion por email |
| `POST` | `/api/notifications/alert` | Envia una alerta (marcada como enviada inmediatamente) |
| `POST` | `/api/notifications/create` | Crea una notificacion en estado pendiente (no enviada) |
| `GET` | `/api/notifications/user/{userId}` | Obtiene notificaciones de un usuario |
| `GET` | `/api/notifications/user/{userId}/pending` | Notificaciones pendientes de un usuario |
| `GET` | `/api/notifications/pending` | Todas las notificaciones pendientes del sistema |
| `PUT` | `/api/notifications/{id}/sent` | Marca una notificacion como enviada |

### Entidad: Notification

| Campo | Tipo | Descripcion |
|---|---|---|
| `id` | `Long` | PK, autoincremental |
| `userId` | `Long` | ID del usuario destino |
| `type` | `NotificationType` (enum) | EMAIL, SMS, ALERT, PUSH_NOTIFICATION |
| `subject` | `String` | Asunto de la notificacion |
| `content` | `String` (TEXT) | Contenido del mensaje |
| `sent` | `boolean` | Indica si fue enviada |
| `sentAt` | `LocalDateTime` | Fecha de envio |
| `createdAt` | `LocalDateTime` | Fecha de creacion (asignada con @PrePersist) |
| `errorMessage` | `String` | Mensaje de error si el envio fallo |

### Tipos de notificacion

| Tipo | Descripcion |
|---|---|
| `EMAIL` | Notificacion enviada por correo electronico |
| `SMS` | Mensaje de texto (no implementado) |
| `ALERT` | Alerta del sistema (marcada como enviada al instante) |
| `PUSH_NOTIFICATION` | Notificacion push (no implementado) |

### EmailService

Utiliza `JavaMailSender` de Spring con configuracion SMTP (Gmail en desarrollo). Envia correos electronicos mediante `SimpleMailMessage`. Retorna `true` si el envio fue exitoso, `false` en caso contrario.

---

## Patrones transversales

### Manejo de errores comun

Todos los servicios (excepto `api-gateway`, que es reactivo) comparten la misma estructura de manejo de errores:

| Excepcion | Codigo HTTP | Formato de respuesta |
|---|---|---|
| `MethodArgumentNotValidException` | 400 | `Map<String, String>` campo -> mensaje de error |
| `RuntimeException` | 400 | `ErrorResponse`: `timestamp`, `status`, `error`, `message`, `path` |
| `Exception` | 500 | `ErrorResponse` (misma estructura) |

### Documentacion Swagger/OpenAPI

Cada servicio expone su documentacion Swagger UI en:

| Servicio | URL |
|---|---|
| `api-gateway` | `http://localhost:8080/swagger-ui.html` |
| `bff` | `http://localhost:8086/swagger-ui.html` |
| `ms-auth` | `http://localhost:8081/swagger-ui.html` |
| `ms-academic` | `http://localhost:8082/swagger-ui.html` |
| `ms-assistance` | `http://localhost:8083/swagger-ui.html` |
| `ms-message` | `http://localhost:8084/swagger-ui.html` |
| `ms-notification` | `http://localhost:8085/swagger-ui.html` |

### Perfiles de ejecucion

| Perfil | Base de datos | Uso |
|---|---|---|
| `default` | H2 en memoria | Desarrollo local sin Docker |
| `docker` | PostgreSQL 15 | Contenedores Docker / produccion |

### Migraciones Flyway

Cada servicio con base de datos gestiona su propio esquema mediante archivos SQL versionados. Las migraciones se aplican automaticamente al iniciar el servicio.
