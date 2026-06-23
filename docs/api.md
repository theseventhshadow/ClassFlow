# API Reference

## Vista General

ClassFlow expone sus APIs a traves del **API Gateway** (`http://localhost:8080`), que enruta las peticiones a los servicios correspondientes. Todas las rutas usan el prefijo `/api/`.

### Formato de respuesta

Todas las respuestas son en JSON. Las respuestas exitosas retornan el objeto o lista solicitada directamente. Las respuestas de error siguen este formato estandar:

```json
{
  "error": "Mensaje descriptivo del error"
}
```

Para errores de validacion de campos:

```json
{
  "campo": "Mensaje de error para este campo",
  "otroCampo": "Otro mensaje"
}
```

### Codigos de estado HTTP

| Codigo | Significado |
|---|---|
| `200` | OK - Peticion exitosa |
| `201` | Created - Recurso creado exitosamente |
| `400` | Bad Request - Error de validacion o negocio |
| `401` | Unauthorized - Token invalido o no proporcionado |
| `404` | Not Found - Recurso no encontrado |
| `409` | Conflict - Recurso duplicado (email, RUT) |
| `500` | Internal Server Error - Error inesperado |

### Autenticacion

Los endpoints protegidos requieren un token JWT en el header `Authorization`:

```
Authorization: Bearer <token>
```

Los tokens se obtienen mediante `POST /api/auth/login`. Los unicos endpoints publicos son `/api/auth/login` y `/api/auth/register`.

---

## 1. ms-auth - Autenticacion y Usuarios

**Base URL:** `http://localhost:8081` (a traves del gateway: `http://localhost:8080/api/auth`)

### Autenticacion

#### POST /api/auth/login

Inicia sesion y retorna un token JWT.

**Request body:**
```json
{
  "email": "admin@classflow.cl",
  "password": "admin123"
}
```

**Response 200:**
```json
{
  "id": 1,
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "type": "Bearer",
  "email": "admin@classflow.cl",
  "role": "ADMINISTRATOR",
  "fullName": "Admin ClassFlow"
}
```

**Response 401:**
```json
{
  "error": "Credenciales invalidas"
}
```

#### POST /api/auth/register

Crea un nuevo usuario.

**Request body:**
```json
{
  "firstName": "Juan",
  "lastName": "Perez",
  "idNumber": "12.345.678-9",
  "email": "juan.perez@example.com",
  "password": "miPassword123",
  "role": "STUDENT",
  "course": "1A",
  "guardianId": null
}
```

| Campo | Tipo | Obligatorio | Descripcion |
|---|---|---|---|
| `firstName` | string | si | Nombre del usuario |
| `lastName` | string | si | Apellido del usuario |
| `idNumber` | string | si | RUT chileno (formato: `12.345.678-9`) |
| `email` | string | si | Correo electronico |
| `password` | string | si | Minimo 6 caracteres |
| `role` | string | si | `ADMINISTRATOR`, `TEACHER`, `STUDENT` o `GUARDIAN` |
| `course` | string | no | Curso asignado (para estudiantes) |
| `guardianId` | number | no | ID del apoderado (para estudiantes) |

**Response 201:**
```json
{
  "id": 2,
  "firstName": "Juan",
  "lastName": "Perez",
  "fullName": "Juan Perez",
  "idNumber": "12.345.678-9",
  "email": "juan.perez@example.com",
  "role": "STUDENT",
  "course": "1A",
  "active": true
}
```

**Response 409:**
```json
{
  "error": "El email ya esta registrado"
}
```

#### GET /api/auth/validate

Valida un token JWT.

**Headers:** `Authorization: Bearer <token>`

**Response 200:**
```json
{
  "id": 1,
  "firstName": "Admin",
  "lastName": "ClassFlow",
  "fullName": "Admin ClassFlow",
  "email": "admin@classflow.cl",
  "role": "ADMINISTRATOR",
  "active": true
}
```

#### GET /api/auth/me

Obtiene los datos del usuario autenticado mediante el token.

**Headers:** `Authorization: Bearer <token>`

**Response 200:** Misma estructura que `/api/auth/validate`.

### Usuarios

#### GET /api/auth/users/{id}

Obtiene un usuario por ID.

**Response 200:**
```json
{
  "id": 1,
  "firstName": "Admin",
  "lastName": "ClassFlow",
  "fullName": "Admin ClassFlow",
  "idNumber": "11.111.111-1",
  "email": "admin@classflow.cl",
  "role": "ADMINISTRATOR",
  "course": null,
  "active": true
}
```

**Response 404:**
```json
{
  "error": "User not found with ID: 999"
}
```

#### GET /api/auth/users/email/{email}

Obtiene un usuario por email.

#### GET /api/auth/users/idnumber/{idNumber}

Obtiene un usuario por RUT.

#### PUT /api/auth/users/{id}

Actualiza datos de un usuario.

**Request body:**
```json
{
  "firstName": "Juan",
  "lastName": "Perez",
  "email": "juan.nuevo@example.com",
  "course": "2A"
}
```

#### DELETE /api/auth/users/{id}

Eliminacion logica (soft-delete) de un usuario. Establece `active = false`.

#### POST /api/auth/change-password

Cambia la contrasena del usuario autenticado.

**Request body:**
```json
{
  "currentPassword": "miPassword123",
  "newPassword": "miNuevoPassword456"
}
```

---

## 2. ms-academic - Academico

**Base URL:** `http://localhost:8082` (a traves del gateway: `http://localhost:8080/api`)

### Cursos (`/api/courses`)

#### GET /api/courses

Lista todos los cursos.

**Response 200:**
```json
[
  {
    "id": 1,
    "name": "1 Basico A",
    "description": "Primero basico, seccion A",
    "academicYear": 2026,
    "active": true
  }
]
```

#### GET /api/courses/{id}

Obtiene un curso por ID.

#### POST /api/courses

Crea un nuevo curso.

**Request body:**
```json
{
  "name": "1 Basico A",
  "description": "Primero basico, seccion A",
  "academicYear": 2026
}
```

#### PUT /api/courses/{id}

Actualiza un curso.

#### DELETE /api/courses/{id}

Eliminacion logica (soft-delete). Cambia `active` a `false`.

### Asignaturas (`/api/subjects`)

#### GET /api/subjects

Lista todas las asignaturas.

#### GET /api/subjects/course/{courseId}

Asignaturas de un curso especifico.

#### GET /api/subjects/{id}

Obtiene una asignatura por ID.

#### POST /api/subjects

**Request body:**
```json
{
  "name": "Matematicas",
  "description": "Asignatura de matematicas",
  "courseId": 1
}
```

#### PUT /api/subjects/{id}

#### DELETE /api/subjects/{id}

Soft-delete.

### Evaluaciones (`/api/evaluations`)

#### GET /api/evaluations

Lista todas las evaluaciones.

#### GET /api/evaluations/subject/{subjectId}

Evaluaciones de una asignatura.

#### GET /api/evaluations/{id}

#### POST /api/evaluations

**Request body:**
```json
{
  "name": "Prueba 1",
  "description": "Primera evaluacion del semestre",
  "maxScore": 7.0,
  "percentage": 25.0,
  "date": "2026-04-15",
  "subjectId": 1
}
```

#### PUT /api/evaluations/{id}

#### DELETE /api/evaluations/{id}

Hard-delete (eliminacion fisica).

### Calificaciones (`/api/grades`)

#### GET /api/grades

Lista todas las calificaciones.

#### GET /api/grades/student/{studentId}

Calificaciones de un estudiante.

#### GET /api/grades/evaluation/{evaluationId}

Calificaciones de una evaluacion.

#### GET /api/grades/{id}

#### POST /api/grades

**Request body:**
```json
{
  "studentId": 5,
  "score": 6.5,
  "observations": "Buen desempeno",
  "evaluationId": 1
}
```

**Regla de negocio:** `score` debe ser menor o igual a `maxScore` de la evaluacion asociada.

#### PUT /api/grades/{id}

#### DELETE /api/grades/{id}

Hard-delete.

---

## 3. ms-assistance - Asistencia

**Base URL:** `http://localhost:8083` (a traves del gateway: `http://localhost:8080/api`)

### Asistencia (`/api/attendance`)

#### GET /api/attendance

Lista todos los registros de asistencia.

#### GET /api/attendance/student/{studentId}

Asistencia filtrada por estudiante.

#### GET /api/attendance/course/{courseId}/date/{date}

Asistencia de un curso en una fecha especifica.

**Formato de fecha:** `YYYY-MM-DD`

#### POST /api/attendance/register

Registra asistencia. No permite duplicados (mismo `studentId` + `date`).

**Request body:**
```json
{
  "studentId": 5,
  "courseId": 1,
  "date": "2026-04-15",
  "present": true,
  "justification": null
}
```

| Campo | Tipo | Obligatorio |
|---|---|---|
| `studentId` | number | si |
| `courseId` | number | si |
| `date` | string (date) | si |
| `present` | boolean | si |
| `justification` | string | no |

#### PUT /api/attendance/{id}

Actualiza un registro de asistencia.

### Anotaciones (`/api/annotations`)

#### GET /api/annotations

Lista todas las anotaciones.

#### GET /api/annotations/student/{studentId}

Anotaciones de un estudiante.

#### GET /api/annotations/student/{studentId}/type/{type}

Anotaciones filtradas por tipo. `type` puede ser `POSITIVE` o `NEGATIVE`.

#### POST /api/annotations

**Request body:**
```json
{
  "studentId": 5,
  "teacherId": 2,
  "type": "POSITIVE",
  "description": "Buena participacion en clase"
}
```

| Campo | Tipo | Obligatorio |
|---|---|---|
| `studentId` | number | si |
| `teacherId` | number | si |
| `type` | string | si (`POSITIVE` o `NEGATIVE`) |
| `description` | string | si |

#### DELETE /api/annotations/{id}

Soft-delete.

---

## 4. ms-message - Mensajeria

**Base URL:** `http://localhost:8084` (a traves del gateway: `http://localhost:8080/api`)

### Mensajes (`/api/messages`)

#### GET /api/messages

Lista todos los mensajes.

#### GET /api/messages/receiver/{receiverId}

Mensajes recibidos por un usuario.

#### GET /api/messages/sender/{senderId}

Mensajes enviados por un usuario.

#### GET /api/messages/receiver/{receiverId}/unread

Mensajes no leidos de un usuario.

#### POST /api/messages/send

Envia un nuevo mensaje.

**Request body:**
```json
{
  "senderId": 2,
  "receiverId": 5,
  "subject": "Recordatorio",
  "body": "Recuerda entregar la tarea manana"
}
```

| Campo | Tipo | Obligatorio |
|---|---|---|
| `senderId` | number | si |
| `receiverId` | number | si |
| `subject` | string | no |
| `body` | string | si |

#### PUT /api/messages/{id}/read

Marca un mensaje como leido.

#### DELETE /api/messages/{id}

Hard-delete.

### Anuncios (`/api/announcements`)

#### GET /api/announcements

Lista todos los anuncios.

#### GET /api/announcements/active

Anuncios activos actualmente.

#### GET /api/announcements/course/{courseId}

Anuncios de un curso especifico. Incluye anuncios globales (sin `courseId` asignado).

#### POST /api/announcements

**Request body:**
```json
{
  "title": "Suspension de clases",
  "content": "El dia 01/05 no habra clases por feriado",
  "courseId": null,
  "senderId": 1
}
```

| Campo | Tipo | Obligatorio | Descripcion |
|---|---|---|---|
| `title` | string | si | Titulo del anuncio |
| `content` | string | si | Contenido del anuncio |
| `courseId` | number | no | `null` = visible para todos los cursos |
| `senderId` | number | si | ID del remitente |

#### DELETE /api/announcements/{id}

Soft-delete.

---

## 5. ms-notification - Notificaciones

**Base URL:** `http://localhost:8085` (a traves del gateway: `http://localhost:8080/api`)

### Notificaciones (`/api/notifications`)

#### POST /api/notifications/email

Envia una notificacion por email.

**Request body:**
```json
{
  "to": "usuario@example.com",
  "subject": "Bienvenido a ClassFlow",
  "body": "Su cuenta ha sido creada exitosamente."
}
```

| Campo | Tipo | Obligatorio |
|---|---|---|
| `to` | string (email) | si |
| `subject` | string | si |
| `body` | string | si |

#### POST /api/notifications/alert

Crea y envia una alerta inmediatamente.

**Request body:**
```json
{
  "userId": 5,
  "type": "ALERT",
  "subject": "Bajo rendimiento",
  "content": "El estudiante ha obtenido notas bajo el minimo en dos asignaturas.",
  "userEmail": "apoderado@example.com"
}
```

| Campo | Tipo | Obligatorio | Descripcion |
|---|---|---|---|
| `userId` | number | si | ID del usuario destino |
| `type` | string | si | `EMAIL`, `SMS`, `ALERT` o `PUSH_NOTIFICATION` |
| `subject` | string | si | Asunto |
| `content` | string | si | Contenido |
| `userEmail` | string | no | Email opcional para envio |

#### POST /api/notifications/create

Crea una notificacion en estado pendiente (no enviada).

**Request body:**
```json
{
  "userId": 5,
  "type": "EMAIL",
  "subject": "Recordatorio",
  "content": "Tiene una evaluacion pendiente."
}
```

#### GET /api/notifications/user/{userId}

Obtiene todas las notificaciones de un usuario.

#### GET /api/notifications/user/{userId}/pending

Notificaciones pendientes (no enviadas) de un usuario.

#### GET /api/notifications/pending

Todas las notificaciones pendientes del sistema.

#### PUT /api/notifications/{id}/sent

Marca una notificacion como enviada.

---

## 6. BFF - Backend for Frontend

**Base URL:** `http://localhost:8086` (a traves del gateway: `http://localhost:8080/api/bff`)

### Dashboard

#### GET /api/bff/dashboard/{userId}

Retorna el dashboard completo de un usuario, agregando datos de los 5 microservicios.

**Response 200:**
```json
{
  "user": { ... },
  "role": "STUDENT",
  "courses": [ ... ],
  "subjects": [ ... ],
  "evaluations": [ ... ],
  "grades": [ ... ],
  "attendances": [ ... ],
  "annotations": [ ... ],
  "messages": [ ... ],
  "unreadMessages": 3,
  "announcements": [ ... ],
  "notifications": [ ... ],
  "pendingNotifications": 1
}
```

La respuesta incluye datos de todos los microservicios consolidados en una sola llamada. Cada propiedad es un array de objetos JSON o un valor escalar.

---

## 7. Swagger UI

Cada servicio expone su documentacion interactiva Swagger UI:

| Servicio | URL |
|---|---|
| API Gateway | `http://localhost:8080/swagger-ui.html` |
| BFF | `http://localhost:8086/swagger-ui.html` |
| ms-auth | `http://localhost:8081/swagger-ui.html` |
| ms-academic | `http://localhost:8082/swagger-ui.html` |
| ms-assistance | `http://localhost:8083/swagger-ui.html` |
| ms-message | `http://localhost:8084/swagger-ui.html` |
| ms-notification | `http://localhost:8085/swagger-ui.html` |

Cada Swagger UI incluye:

- Documentacion de todos los endpoints con metodos HTTP, parametros y respuestas.
- Esquema de seguridad JWT Bearer (probar endpoints protegidos haciendo clic en "Authorize" e ingresando el token).
- Modelos de datos (DTOs, entidades).
- Capacidad de ejecutar peticiones directamente desde el navegador.

---

## Resumen de rutas

| Metodo | Ruta | Servicio | Proposito |
|---|---|---|---|
| POST | `/api/auth/login` | ms-auth | Inicio de sesion |
| POST | `/api/auth/register` | ms-auth | Registro de usuario |
| GET | `/api/auth/validate` | ms-auth | Validar token JWT |
| GET | `/api/auth/me` | ms-auth | Usuario actual |
| GET | `/api/auth/users/{id}` | ms-auth | Usuario por ID |
| GET | `/api/auth/users/email/{email}` | ms-auth | Usuario por email |
| GET | `/api/auth/users/idnumber/{idNumber}` | ms-auth | Usuario por RUT |
| PUT | `/api/auth/users/{id}` | ms-auth | Actualizar usuario |
| DELETE | `/api/auth/users/{id}` | ms-auth | Eliminar usuario |
| POST | `/api/auth/change-password` | ms-auth | Cambiar contrasena |
| GET | `/api/courses` | ms-academic | Listar cursos |
| GET | `/api/courses/{id}` | ms-academic | Curso por ID |
| POST | `/api/courses` | ms-academic | Crear curso |
| PUT | `/api/courses/{id}` | ms-academic | Actualizar curso |
| DELETE | `/api/courses/{id}` | ms-academic | Eliminar curso |
| GET | `/api/subjects` | ms-academic | Listar asignaturas |
| GET | `/api/subjects/course/{courseId}` | ms-academic | Asignaturas por curso |
| GET | `/api/subjects/{id}` | ms-academic | Asignatura por ID |
| POST | `/api/subjects` | ms-academic | Crear asignatura |
| PUT | `/api/subjects/{id}` | ms-academic | Actualizar asignatura |
| DELETE | `/api/subjects/{id}` | ms-academic | Eliminar asignatura |
| GET | `/api/evaluations` | ms-academic | Listar evaluaciones |
| GET | `/api/evaluations/subject/{subjectId}` | ms-academic | Evaluaciones por asignatura |
| GET | `/api/evaluations/{id}` | ms-academic | Evaluacion por ID |
| POST | `/api/evaluations` | ms-academic | Crear evaluacion |
| PUT | `/api/evaluations/{id}` | ms-academic | Actualizar evaluacion |
| DELETE | `/api/evaluations/{id}` | ms-academic | Eliminar evaluacion |
| GET | `/api/grades` | ms-academic | Listar calificaciones |
| GET | `/api/grades/student/{studentId}` | ms-academic | Calificaciones por estudiante |
| GET | `/api/grades/evaluation/{evaluationId}` | ms-academic | Calificaciones por evaluacion |
| GET | `/api/grades/{id}` | ms-academic | Calificacion por ID |
| POST | `/api/grades` | ms-academic | Crear calificacion |
| PUT | `/api/grades/{id}` | ms-academic | Actualizar calificacion |
| DELETE | `/api/grades/{id}` | ms-academic | Eliminar calificacion |
| GET | `/api/attendance` | ms-assistance | Listar asistencias |
| GET | `/api/attendance/student/{studentId}` | ms-assistance | Asistencia por estudiante |
| GET | `/api/attendance/course/{courseId}/date/{date}` | ms-assistance | Asistencia por curso y fecha |
| POST | `/api/attendance/register` | ms-assistance | Registrar asistencia |
| PUT | `/api/attendance/{id}` | ms-assistance | Actualizar asistencia |
| GET | `/api/annotations` | ms-assistance | Listar anotaciones |
| GET | `/api/annotations/student/{studentId}` | ms-assistance | Anotaciones por estudiante |
| GET | `/api/annotations/student/{studentId}/type/{type}` | ms-assistance | Anotaciones por tipo |
| POST | `/api/annotations` | ms-assistance | Crear anotacion |
| DELETE | `/api/annotations/{id}` | ms-assistance | Eliminar anotacion |
| GET | `/api/messages` | ms-message | Listar mensajes |
| GET | `/api/messages/receiver/{receiverId}` | ms-message | Mensajes recibidos |
| GET | `/api/messages/sender/{senderId}` | ms-message | Mensajes enviados |
| GET | `/api/messages/receiver/{receiverId}/unread` | ms-message | Mensajes no leidos |
| POST | `/api/messages/send` | ms-message | Enviar mensaje |
| PUT | `/api/messages/{id}/read` | ms-message | Marcar como leido |
| DELETE | `/api/messages/{id}` | ms-message | Eliminar mensaje |
| GET | `/api/announcements` | ms-message | Listar anuncios |
| GET | `/api/announcements/active` | ms-message | Anuncios activos |
| GET | `/api/announcements/course/{courseId}` | ms-message | Anuncios por curso |
| POST | `/api/announcements` | ms-message | Crear anuncio |
| DELETE | `/api/announcements/{id}` | ms-message | Eliminar anuncio |
| POST | `/api/notifications/email` | ms-notification | Enviar email |
| POST | `/api/notifications/alert` | ms-notification | Enviar alerta |
| POST | `/api/notifications/create` | ms-notification | Crear notificacion |
| GET | `/api/notifications/user/{userId}` | ms-notification | Notificaciones por usuario |
| GET | `/api/notifications/user/{userId}/pending` | ms-notification | Notificaciones pendientes |
| GET | `/api/notifications/pending` | ms-notification | Todas las pendientes |
| PUT | `/api/notifications/{id}/sent` | ms-notification | Marcar como enviada |
| GET | `/api/bff/dashboard/{userId}` | BFF | Dashboard completo |
