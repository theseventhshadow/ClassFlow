# Gateway Service

API Gateway centralizado para ClassFlow Backend.

## Descripción

Servicio de enrutamiento basado en **Spring Cloud Gateway**. Actúa como punto de entrada único para todas las solicitudes HTTP, enrutándolas a los microservicios correspondientes.

## Detalles Técnicos

- **Puerto**: `8080`
- **Framework**: Spring Boot 3.5.14 + Spring Cloud Gateway 2025.0.0
- **Java**: JDK 25
- **Base de datos**: No requiere (servicio sin estado)

## Rutas Configuradas

| Ruta | Destino | Descripción |
|------|---------|------------|
| `/api/auth/**` | ms-auth:8081 | Autenticación y usuarios |
| `/api/courses/**` | ms-academic:8082 | Cursos |
| `/api/subjects/**` | ms-academic:8082 | Asignaturas |
| `/api/evaluations/**` | ms-academic:8082 | Evaluaciones |
| `/api/grades/**` | ms-academic:8082 | Calificaciones |
| `/api/attendance/**` | ms-assistance:8083 | Asistencia |
| `/api/annotations/**` | ms-assistance:8083 | Anotaciones |
| `/api/messages/**` | ms-message:8084 | Mensajes |
| `/api/announcements/**` | ms-message:8084 | Anuncios |
| `/api/notifications/**` | ms-notification:8085 | Notificaciones |
| `/api/bff/**` | bff:8086 | Dashboard y agregados |

## Inicio Rápido

```bash
# Desarrollo local
./mvnw spring-boot:run

# Con Docker
docker build -t api-gateway:local .
docker run -p 8080:8080 api-gateway:local
```

## Configuración

- **Desarrollo**: `src/main/resources/application.yml`
- **Docker**: `src/main/resources/application-docker.yml` (perfiles activos: `SPRING_PROFILES_ACTIVE=docker`)

## Health Check

Endpoint de salud disponible en:
```bash
curl http://localhost:8080/actuator/health
```

## Dependencias Principales

- `spring-cloud-starter-gateway` - Enrutamiento dinámico
- `spring-boot-starter-actuator` - Monitoreo y health checks

## Troubleshooting

- **Puerto 8080 en uso**: Cambiar en `application.yml` o usar `--server.port=8081`
- **Rutas no resuelven**: Verificar que los servicios destino estén activos
- **CORS issues**: Configuración en `application.yml` bajo `spring.cloud.gateway`

## Referencias

- [Documentación raíz](../README.md)
- [Spring Cloud Gateway Docs](https://spring.io/projects/spring-cloud-gateway)
