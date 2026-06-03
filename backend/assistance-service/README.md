# Assistance Service

Servicio para gestión de asistencia y anotaciones de estudiantes.

## Descripción

Maneja el registro de asistencia diaria y anotaciones (observaciones, sanciones, etc.) de estudiantes.

## Detalles Técnicos

- **Puerto**: `8083`
- **Framework**: Spring Boot 3.5.14
- **Java**: JDK 25
- **Base de datos**: PostgreSQL (Docker) o H2 en memoria (desarrollo local)
- **Migraciones**: Flyway (ubicación: `src/main/resources/db/migration`)

## Endpoints Principales

| Método | Ruta | Descripción |
|--------|------|------------|
| `GET` | `/api/attendance` | Listar asistencias |
| `GET` | `/api/attendance/student/{studentId}` | Asistencias de estudiante |
| `GET` | `/api/attendance/course/{courseId}/date/{date}` | Asistencias de curso en fecha |
| `POST` | `/api/attendance/register` | Registrar asistencia |
| `PUT` | `/api/attendance/{id}` | Actualizar asistencia |
| `GET` | `/api/annotations` | Listar anotaciones |
| `GET` | `/api/annotations/student/{studentId}` | Anotaciones de estudiante |
| `POST` | `/api/annotations` | Crear anotación |
| `DELETE` | `/api/annotations/{id}` | Eliminar anotación |

## Inicio Rápido

```bash
# Desarrollo local (H2 en memoria)
./mvnw spring-boot:run

# Con Docker (PostgreSQL)
docker build -t assistance-service:local .
docker run -p 8083:8083 -e SPRING_PROFILES_ACTIVE=docker assistance-service:local
```

## Configuración

- **Desarrollo**: `application.properties` (H2 en memoria)
- **Docker**: `application-docker.yml` (PostgreSQL)

## Base de datos

### Tablas principales

- `attendance`: Registros de asistencia (presente/ausente)
- `annotations`: Anotaciones sobre conducta/observaciones

### Migraciones Flyway

| Archivo | Descripción |
|---------|------------|
| `V1__initial_schema.sql` | Crear tablas attendance y annotations |
| `V2__seed_data.sql` | Datos iniciales |
| `V3__more_seed_data.sql` | Más datos de prueba |

## Ejemplos de Uso

```bash
# Registrar asistencia
curl -X POST http://localhost:8083/api/attendance/register \
  -H "Content-Type: application/json" \
  -d '{"studentId":1,"courseId":1,"date":"2026-05-15","status":"PRESENT"}'

# Obtener asistencias de estudiante
curl -X GET http://localhost:8083/api/attendance/student/1

# Crear anotación
curl -X POST http://localhost:8083/api/annotations \
  -H "Content-Type: application/json" \
  -d '{"studentId":1,"type":"WARNING","description":"Comportamiento disruptivo en clase","date":"2026-05-15"}'

# Obtener anotaciones por estudiante
curl -X GET http://localhost:8083/api/annotations/student/1
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

- **"Student not found"**: Verificar que el estudiante existe (integración con auth-service)
- **Asistencia duplicada**: Evitar múltiples registros del mismo estudiante en misma fecha
- **Puerto 8083 en uso**: Cambiar en `application.properties`

## Referencias

- [Documentación raíz](../README.md)
- [Spring Data JPA Docs](https://spring.io/projects/spring-data-jpa)
