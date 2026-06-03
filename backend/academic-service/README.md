# Academic Service

Servicio académico para gestión de cursos, asignaturas, evaluaciones y calificaciones.

## Descripción

Maneja toda la información académica: estructura de cursos, asignaturas, evaluaciones, y calificaciones de estudiantes.

## Detalles Técnicos

- **Puerto**: `8082`
- **Framework**: Spring Boot 3.5.14
- **Java**: JDK 25
- **Base de datos**: PostgreSQL (Docker) o H2 en memoria (desarrollo local)
- **Migraciones**: Flyway (ubicación: `src/main/resources/db/migration`)

## Endpoints Principales

| Método | Ruta | Descripción |
|--------|------|------------|
| `GET` | `/api/courses` | Listar cursos |
| `POST` | `/api/courses` | Crear curso |
| `GET` | `/api/courses/{id}` | Obtener curso |
| `PUT` | `/api/courses/{id}` | Actualizar curso |
| `DELETE` | `/api/courses/{id}` | Eliminar curso |
| `GET` | `/api/subjects/course/{courseId}` | Asignaturas por curso |
| `GET` | `/api/evaluations/subject/{subjectId}` | Evaluaciones por asignatura |
| `GET` | `/api/grades/student/{studentId}` | Calificaciones por estudiante |
| `POST` | `/api/grades` | Crear calificación |

## Inicio Rápido

```bash
# Desarrollo local (H2 en memoria)
./mvnw spring-boot:run

# Con Docker (PostgreSQL)
docker build -t academic-service:local .
docker run -p 8082:8082 -e SPRING_PROFILES_ACTIVE=docker academic-service:local
```

## Configuración

- **Desarrollo**: `application.yml` (H2 en memoria)
- **Docker**: `application-docker.yml` (PostgreSQL)

## Base de datos

### Tablas principales

- `courses`: Cursos disponibles
- `subjects`: Asignaturas dentro de cursos
- `evaluations`: Evaluaciones/exámenes
- `grades`: Calificaciones de estudiantes

### Migraciones Flyway

| Archivo | Descripción |
|---------|------------|
| `V1__initial_schema.sql` | Crear todas las tablas principales |
| `V2__seed_data.sql` | Datos iniciales (cursos, asignaturas) |
| `V3__more_seed_data.sql` | Más datos de prueba |

## Ejemplos de Uso

```bash
# Crear curso
curl -X POST http://localhost:8082/api/courses \
  -H "Content-Type: application/json" \
  -d '{"name":"1º ESO","description":"Primer año educación secundaria","academicYear":2026,"active":true}'

# Listar cursos
curl -X GET http://localhost:8082/api/courses

# Obtener asignaturas de un curso
curl -X GET http://localhost:8082/api/subjects/course/1

# Crear asignatura
curl -X POST http://localhost:8082/api/subjects \
  -H "Content-Type: application/json" \
  -d '{"name":"Matemáticas","description":"Matemáticas 1º ESO","courseId":1,"active":true}'
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

- **"Foreign key constraint failed"**: Verificar que curso/asignatura/evaluación padre exista
- **Sin datos de prueba**: Ejecutar migraciones Flyway (V1, V2, V3)
- **Puerto 8082 en uso**: Cambiar en `application.yml`

## Referencias

- [Documentación raíz](../README.md)
- [Spring Data JPA Docs](https://spring.io/projects/spring-data-jpa)
- [Flyway Migration Docs](https://flywaydb.org)
