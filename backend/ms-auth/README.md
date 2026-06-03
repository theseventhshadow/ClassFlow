# Auth Service

Servicio de autenticación y gestión de usuarios para ClassFlow Backend.

## Descripción

Maneja autenticación de usuarios, registro, validación de tokens JWT y gestión de roles (ADMIN, TEACHER, STUDENT, GUARDIAN).

## Detalles Técnicos

- **Puerto**: `8081`
- **Framework**: Spring Boot 3.5.14 + Spring Security
- **Java**: JDK 25
- **Base de datos**: PostgreSQL (Docker) o H2 en memoria (desarrollo local)
- **Autenticación**: JWT (JSON Web Tokens)
- **Migraciones**: Flyway (ubicación: `src/main/resources/db/migration`)

## Endpoints Principales

| Método | Ruta | Descripción |
|--------|------|------------|
| `POST` | `/api/auth/login` | Login (devuelve JWT) |
| `POST` | `/api/auth/register` | Registro de usuario |
| `GET` | `/api/auth/validate` | Validar token |
| `GET` | `/api/auth/me` | Obtener usuario actual |
| `GET` | `/api/auth/users/{id}` | Obtener usuario por ID |
| `PUT` | `/api/auth/users/{id}` | Actualizar usuario |
| `DELETE` | `/api/auth/users/{id}` | Eliminar usuario |
| `POST` | `/api/auth/change-password` | Cambiar contraseña |

## Inicio Rápido

```bash
# Desarrollo local (H2 en memoria)
./mvnw spring-boot:run

# Con Docker (PostgreSQL)
docker build -t ms-auth:local .
docker run -p 8081:8081 -e SPRING_PROFILES_ACTIVE=docker ms-auth:local
```

## Configuración

- **Desarrollo**: `application.yml` (H2 en memoria)
- **Docker**: `application-docker.yml` (PostgreSQL)

**Variables de entorno importantes**:
```env
JWT_SECRET=tu_clave_secreta_aqui
JWT_EXPIRATION=86400000  # 24 horas en milisegundos
```

## Base de datos

### Tablas principales

- `users`: Usuarios del sistema
- `roles`: Roles disponibles (ADMIN, TEACHER, STUDENT, GUARDIAN)

### Migraciones Flyway

| Archivo | Descripción |
|---------|------------|
| `V1__initial_schema.sql` | Crear tablas users y roles |
| `V2__seed_data.sql` | Datos iniciales (usuarios de prueba) |
| `V5__encrypt_seed_passwords.sql` | Encriptar contraseñas con BCrypt |

## Ejecutar Pruebas

```bash
./mvnw test
```

## Ejemplos de Uso

```bash
# Login
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@example.com","password":"password"}'

# Registrar usuario
curl -X POST http://localhost:8081/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"firstName":"Juan","lastName":"Pérez","idNumber":"12345678","email":"juan@example.com","password":"secure123","role":"TEACHER"}'

# Validar token
curl -X GET http://localhost:8081/api/auth/validate \
  -H "Authorization: Bearer <JWT_TOKEN>"
```

## Seguridad

- Contraseñas encriptadas con BCrypt (fuerza 10)
- JWT con expiración configurable
- Validación de roles en endpoints protegidos
- CORS habilitado (ajustar en producción)

## Dependencias Principales

- `spring-boot-starter-security` - Seguridad
- `spring-boot-starter-data-jpa` - Persistencia
- `io.jsonwebtoken:jjwt` (v0.11.5) - JWT
- `org.flywaydb:flyway-core` - Migraciones
- `org.postgresql:postgresql` - Driver PostgreSQL
- `com.h2database:h2` - Base de datos en memoria (desarrollo)

## Troubleshooting

- **"Invalid token"**: Verificar JWT_SECRET coincida entre servicios
- **"No user found"**: Revisar datos seed en V2 o crear usuario manualmente
- **Puerto 8081 en uso**: Cambiar en `application.yml` o usar `--server.port=8082`

## Referencias

- [Documentación raíz](../README.md)
- [Spring Security Docs](https://spring.io/projects/spring-security)
- [JWT.io](https://jwt.io)
