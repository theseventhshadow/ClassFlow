# BFF Service

Backend-for-Frontend (BFF) - Servicio agregador de datos para el dashboard.

## Descripción

Servicio reactivo que agrega datos de otros microservicios para proporcionar endpoints optimizados para el frontend (dashboard, estadísticas, etc.).

## Detalles Técnicos

- **Puerto**: `8086`
- **Framework**: Spring Boot 3.5.14 + WebFlux (reactivo)
- **Java**: JDK 25
- **Base de datos**: No requiere (servicio sin estado, consume otros servicios)
- **Comunicación**: HTTP reactivo hacia otros microservicios

## Endpoints Principales

| Método | Ruta | Descripción |
|--------|------|------------|
| `GET` | `/api/bff/dashboard/{userId}` | Dashboard completo para usuario |
| `GET` | `/api/bff/dashboard/stats/{userId}` | Estadísticas del usuario |
| `GET` | `/api/bff/dashboard/users/recent` | Últimos usuarios registrados |
| `GET` | `/api/bff/dashboard/attendance/by-course` | Asistencia por curso |
| `GET` | `/api/bff/dashboard/activity` | Actividad reciente |
| `GET` | `/api/bff/dashboard/alerts` | Alertas del sistema |

## Inicio Rápido

```bash
# Desarrollo local
./mvnw spring-boot:run

# Con Docker (conexiones a otros servicios del docker-compose)
docker build -t bff:local .
docker run -p 8086:8086 -e SPRING_PROFILES_ACTIVE=docker bff:local
```

## Configuración

- **Desarrollo**: `application.yml` (conecta a servicios en localhost:8081-8085)
- **Docker**: `application-docker.yml` (conecta a servicios por nombre DNS dentro de docker-compose)

### URLs de servicios

Las URL base de los microservicios se configuran en `application.yml`:

```yaml
services:
  auth:
    base-url: http://localhost:8081  # Desarrollo
    # base-url: http://ms-auth:8081     # Docker
  academic:
    base-url: http://localhost:8082  # Desarrollo
  assistance:
    base-url: http://localhost:8083
  message:
    base-url: http://localhost:8084
  notification:
    base-url: http://localhost:8085
```

## Ejemplos de Uso

```bash
# Obtener dashboard completo
curl -X GET "http://localhost:8086/api/bff/dashboard/1" \
  -H "Authorization: Bearer <JWT_TOKEN>"

# Obtener estadísticas del usuario
curl -X GET "http://localhost:8086/api/bff/dashboard/stats/1" \
  -H "Authorization: Bearer <JWT_TOKEN>"

# Obtener últimos usuarios registrados (límite: 5 por defecto)
curl -X GET "http://localhost:8086/api/bff/dashboard/users/recent?limit=10" \
  -H "Authorization: Bearer <JWT_TOKEN>"

# Obtener asistencia por curso
curl -X GET "http://localhost:8086/api/bff/dashboard/attendance/by-course" \
  -H "Authorization: Bearer <JWT_TOKEN>"

# Obtener actividad reciente (límite: 4 por defecto)
curl -X GET "http://localhost:8086/api/bff/dashboard/activity?limit=10" \
  -H "Authorization: Bearer <JWT_TOKEN>"

# Obtener alertas del sistema
curl -X GET "http://localhost:8086/api/bff/dashboard/alerts" \
  -H "Authorization: Bearer <JWT_TOKEN>"
```

## Ejecutar Pruebas

```bash
./mvnw test
```

## Dependencias Principales

- `spring-boot-starter-webflux` - Framework reactivo
- `spring-boot-starter-validation` - Validación de datos
- `org.projectlombok:lombok` - Reducir boilerplate

## Arquitectura y Patrón

**BFF (Backend-for-Frontend)**:
- Actúa como agregador de datos
- Consulta múltiples microservicios en paralelo
- Transforma y combina respuestas
- Optimiza respuestas para el frontend
- Usa WebFlux para mejor rendimiento en I/O

**Comunicación reactiva**:
```
Frontend -> BFF (8086) -> Auth Service (8081)
                      -> Academic Service (8082)
                      -> Assistance Service (8083)
                      -> Message Service (8084)
                      -> Notification Service (8085)
```

## Troubleshooting

- **"Connection refused"**: Verificar que otros microservicios estén activos
- **"Gateway timeout"**: Los servicios remotos tardan demasiado; revisar logs
- **Puerto 8086 en uso**: Cambiar en `application.yml`
- **Servicios no resuelven en Docker**: Verificar que docker-compose tenga la network correcta

## Mejoras Futuras

- Implementar caching de respuestas (Redis)
- Rate limiting por usuario
- Circuit breaker para fallos en servicios remotos
- Logging distribuido (ELK stack)

## Referencias

- [Documentación raíz](../README.md)
- [Spring WebFlux Docs](https://spring.io/projects/spring-webflux)
- [BFF Pattern](https://samnewman.io/patterns/architectural/bff/)
