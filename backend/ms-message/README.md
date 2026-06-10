# Message Service

Servicio de mensajería y anuncios para ClassFlow Backend.

## Descripción

Maneja mensajes directo entre usuarios y anuncios distribuidos a grupos de estudiantes (cursos, etc.).

## Detalles Técnicos

- **Puerto**: `8084`
- **Framework**: Spring Boot 3.5.14
- **Java**: JDK 25
- **Base de datos**: PostgreSQL (Docker) o H2 en memoria (desarrollo local)
- **Migraciones**: Flyway (ubicación: `src/main/resources/db/migration`)

## Endpoints Principales

| Método | Ruta | Descripción |
|--------|------|------------|
| `GET` | `/api/messages` | Listar mensajes |
| `GET` | `/api/messages/sender/{senderId}` | Mensajes enviados por usuario |
| `GET` | `/api/messages/receiver/{receiverId}` | Mensajes recibidos por usuario |
| `GET` | `/api/messages/receiver/{receiverId}/unread` | Mensajes no leídos |
| `POST` | `/api/messages/send` | Enviar mensaje |
| `PUT` | `/api/messages/{id}/read` | Marcar mensaje como leído |
| `DELETE` | `/api/messages/{id}` | Eliminar mensaje |
| `GET` | `/api/announcements` | Listar anuncios |
| `GET` | `/api/announcements/active` | Anuncios activos |
| `GET` | `/api/announcements/course/{courseId}` | Anuncios por curso |
| `POST` | `/api/announcements` | Crear anuncio |
| `DELETE` | `/api/announcements/{id}` | Eliminar anuncio |

## Inicio Rápido

```bash
# Desarrollo local (H2 en memoria)
./mvnw spring-boot:run

# Con Docker (PostgreSQL)
docker build -t ms-message:local .
docker run -p 8084:8084 -e SPRING_PROFILES_ACTIVE=docker ms-message:local
```

## Configuración

- **Desarrollo**: `application.properties` (H2 en memoria)
- **Docker**: `application-docker.yml` (PostgreSQL)

## Base de datos

### Tablas principales

- `messages`: Mensajes entre usuarios
- `announcements`: Anuncios para cursos/grupos

### Migraciones Flyway

| Archivo | Descripción |
|---------|------------|
| `V1__initial_schema.sql` | Crear tablas messages y announcements |
| `V2__seed_data.sql` | Datos iniciales |
| `V3__more_seed_data.sql` | Más datos de prueba |

## Ejemplos de Uso

```bash
# Enviar mensaje
curl -X POST http://localhost:8084/api/messages/send \
  -H "Content-Type: application/json" \
  -d '{"senderId":1,"receiverId":2,"subject":"Tarea pendiente","body":"Completa el ejercicio 5"}'

# Obtener mensajes recibidos
curl -X GET http://localhost:8084/api/messages/receiver/2

# Obtener mensajes no leídos
curl -X GET http://localhost:8084/api/messages/receiver/2/unread

# Marcar mensaje como leído
curl -X PUT http://localhost:8084/api/messages/1/read

# Crear anuncio
curl -X POST http://localhost:8084/api/announcements \
  -H "Content-Type: application/json" \
  -d '{"courseId":1,"title":"Cambio de horario","description":"La clase se traslada a otra aula","active":true}'

# Obtener anuncios activos
curl -X GET http://localhost:8084/api/announcements/active
```

## Ejecutar Pruebas

```bash
./mvnw test
```

## Dependencias Principales

- `spring-boot-starter-data-jpa` - ORM Hibernate
- `spring-boot-starter-web` - REST
- `org.flywaydb:flyway-core` - Migraciones
- `org.postgresql:postgresql` - Driver PostgreSQL
- `com.h2database:h2` - Base de datos en memoria (desarrollo)
- `org.projectlombok:lombok` - Reducir boilerplate

## Troubleshooting

- **"User not found"**: Verificar que sender/receiver existan en ms-auth
- **Anuncio no visible**: Verificar que `active=true`
- **Puerto 8084 en uso**: Cambiar en `application.properties`

## Referencias

- [Documentación raíz](../README.md)
- [Spring Data JPA Docs](https://spring.io/projects/spring-data-jpa)
