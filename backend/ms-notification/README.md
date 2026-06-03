# Notification Service

Servicio de notificaciones y envío de emails para ClassFlow Backend.

## Descripción

Maneja el envío de notificaciones por email y alertas del sistema (ausencias, nuevas calificaciones, anotaciones, mensajes, anuncios).

## Detalles Técnicos

- **Puerto**: `8085`
- **Framework**: Spring Boot 3.5.14 + JavaMail
- **Java**: JDK 25
- **Base de datos**: PostgreSQL (Docker) o H2 en memoria (desarrollo local)
- **Migraciones**: Flyway (ubicación: `src/main/resources/db/migration`)
- **Correo**: SMTP (configurable)

## Endpoints Principales

| Método | Ruta | Descripción |
|--------|------|------------|
| `POST` | `/api/notifications/email` | Enviar email directo |
| `POST` | `/api/notifications/alert` | Enviar alerta (ABSENCE, NEW_GRADE, etc.) |
| `POST` | `/api/notifications/create` | Crear notificación sin enviar |
| `GET` | `/api/notifications/user/{userId}` | Notificaciones de usuario |
| `GET` | `/api/notifications/user/{userId}/pending` | Notificaciones pendientes |
| `GET` | `/api/notifications/pending` | Todas las notificaciones pendientes |
| `PUT` | `/api/notifications/{id}/sent` | Marcar como enviada |

## Inicio Rápido

```bash
# Desarrollo local (H2 en memoria, SMTP configurado)
./mvnw spring-boot:run

# Con Docker (PostgreSQL, SMTP desde variables de entorno)
docker build -t ms-notification:local .
docker run -p 8085:8085 \
  -e SPRING_PROFILES_ACTIVE=docker \
  -e SPRING_MAIL_HOST=smtp.gmail.com \
  -e SPRING_MAIL_USERNAME=tu_email@gmail.com \
  -e SPRING_MAIL_PASSWORD=tu_contraseña \
  ms-notification:local
```

## Configuración

- **Desarrollo**: `application.properties` (H2, SMTP de ejemplo)
- **Docker**: `application-docker.yml` (PostgreSQL, variables de entorno)

### Variables de Entorno (SMTP)

```env
SPRING_MAIL_HOST=smtp.gmail.com
SPRING_MAIL_PORT=587
SPRING_MAIL_USERNAME=tu_email@gmail.com
SPRING_MAIL_PASSWORD=tu_contraseña_o_app_password
SPRING_MAIL_PROPERTIES_MAIL_SMTP_AUTH=true
SPRING_MAIL_PROPERTIES_MAIL_SMTP_STARTTLS_ENABLE=true
```

> Nota: Usar "App Password" en Gmail (no contraseña normal). [Ver instrucciones](https://support.google.com/accounts/answer/185833)

## Base de datos

### Tablas principales

- `notifications`: Registro de notificaciones
- (Sin tabla de templates, usar strings configurables)

### Migraciones Flyway

| Archivo | Descripción |
|---------|------------|
| `V1__initial_schema.sql` | Crear tabla notifications |
| `V2__seed_data.sql` | Datos iniciales |
| `V3__more_seed_data.sql` | Más datos de prueba |

## Ejemplos de Uso

```bash
# Enviar email directo
curl -X POST http://localhost:8085/api/notifications/email \
  -H "Content-Type: application/json" \
  -d '{
    "to":"estudiante@example.com",
    "subject":"Calificación publicada",
    "body":"Tu calificación en Matemáticas es 8.5"
  }'

# Enviar alerta (p. ej. por ausencia)
curl -X POST http://localhost:8085/api/notifications/alert \
  -H "Content-Type: application/json" \
  -d '{
    "userId":1,
    "type":"ABSENCE",
    "message":"Ausencia registrada el 15/05/2026"
  }'

# Obtener notificaciones pendientes de usuario
curl -X GET http://localhost:8085/api/notifications/user/1/pending

# Marcar notificación como enviada
curl -X PUT http://localhost:8085/api/notifications/1/sent
```

## Ejecutar Pruebas

```bash
./mvnw test
```

## Dependencias Principales

- `spring-boot-starter-mail` - Envío de emails
- `spring-boot-starter-data-jpa` - ORM Hibernate
- `spring-boot-starter-web` - REST
- `org.flywaydb:flyway-core` - Migraciones
- `org.postgresql:postgresql` - Driver PostgreSQL
- `com.h2database:h2` - Base de datos en memoria (desarrollo)
- `org.projectlombok:lombok` - Reducir boilerplate

## Troubleshooting

- **"SMTP Authentication failed"**: Verificar credenciales SMTP y usar App Password en Gmail
- **"Connection refused"**: Verificar `SPRING_MAIL_HOST` y `SPRING_MAIL_PORT`
- **Emails no se envían**: Revisar logs de `spring.mail` activando `DEBUG`
- **Puerto 8085 en uso**: Cambiar en `application.properties`

```bash
# Habilitar logs SMTP
export SPRING_JPA_HIBERNATE_DDL_AUTO=validate
export LOGGING_LEVEL_ORG_SPRINGFRAMEWORK_MAIL=DEBUG
./mvnw spring-boot:run
```

## Seguridad

- No guardar credenciales SMTP en repositorios; usar variables de entorno
- En producción, usar un servicio de email dedicado (SendGrid, AWS SES, etc.)
- Validar direcciones de email antes de enviar

## Referencias

- [Documentación raíz](../README.md)
- [Spring Mail Docs](https://spring.io/guides/gs/sending-email/)
- [Gmail App Passwords](https://support.google.com/accounts/answer/185833)
