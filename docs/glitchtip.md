# Glitchtip — Error Tracking

Glitchtip es un servicio de **error tracking self-hospedado**, compatible con el SDK de [Sentry](https://develop.sentry.dev/sdk/). Permite centralizar y monitorear los errores que ocurren en todos los microservicios del backend en un solo panel web.

---

## Stack

| Componente | Tecnología |
|---|---|
| Servicio principal | [Glitchtip](https://glitchtip.com/) v6 |
| Base de datos | PostgreSQL 18 (contenedor `glitchtip-db`) |
| Cache | Valkey 9 (contenedor `glitchtip-valkey`) |

---

## Arquitectura

```
Microservicio (sentry-spring-boot-starter)
    │  Reporta error vía SDK de Sentry
    ▼
Glitchtip (http://glitchtip:8000)
    │  Almacena y agrupa errores
    ▼
glitchtip-db (PostgreSQL) ◄── glitchtip-valkey (caché)
```

Todos los microservicios Spring Boot incluyen la dependencia `sentry-spring-boot-starter` y apuntan al DSN de Glitchtip. Cuando ocurre una excepción no manejada, el SDK la envía automáticamente a Glitchtip, donde queda registrada con stacktrace, contexto y metadata.

---

## Configuración inicial (primer uso)

La primera vez que se levanta el proyecto, Glitchtip está vacío. Hay que configurarlo una vez:

1. Abrir http://localhost:8000
2. **Crear una cuenta** (la primera cuenta se convierte en administradora)
3. **Crear una organización** (ej. "ClassFlow")
4. **Crear un proyecto** (ej. "classflow-backend", plataforma "Spring Boot")
5. Glitchtip generará un **DSN** con este formato:
   ```
   http://<public-key>@glitchtip:8000/<project-id>
   ```
6. Copiar ese DSN a `backend/.env`:
   ```env
   SENTRY_DSN=http://<public-key>@glitchtip:8000/<project-id>
   ```
7. **Reconstruir los contenedores** para que tomen el nuevo DSN:
   ```bash
   cd backend
   docker compose up -d --build
   ```



---

## Variables de entorno

### `backend/.env`

| Variable | Descripción | Ejemplo |
|---|---|---|
| `GLITCHTIP_SECRET_KEY` | Clave secreta del servicio Glitchtip | `a754954de7c4f3d7...` |
| `SENTRY_DSN` | DSN del proyecto en Glitchtip | `http://dd12f...@glitchtip:8000/1` |

### `docker-compose.yml`

```yaml
glitchtip:
  environment:
    SECRET_KEY: "${GLITCHTIP_SECRET_KEY:?Definir GLITCHTIP_SECRET_KEY en backend/.env}"
```

Cada microservicio recibe `SENTRY_DSN` como variable de entorno:

```yaml
environment:
  - SENTRY_DSN=${SENTRY_DSN:-}
```

Si `SENTRY_DSN` está vacío, Sentry se desactiva y no se reportan errores.

---

## Configuración en cada microservicio

### Dependencia Maven (`pom.xml`)

```xml
<dependency>
    <groupId>io.sentry</groupId>
    <artifactId>sentry-spring-boot-starter</artifactId>
</dependency>
```

### Application YAML

```yaml
sentry:
  dsn: ${SENTRY_DSN:}
  environment: docker   # o "local" según el perfil
  exception-resolver-order: -2147483648
```

---

## Generar un nuevo `GLITCHTIP_SECRET_KEY`

Si se necesita regenerar la clave secreta de Glitchtip:

```bash
openssl rand -hex 32
```

Luego actualizar `GLITCHTIP_SECRET_KEY` en `backend/.env` y reconstruir los contenedores.

---

## Comandos útiles

```bash
# Ver logs de Glitchtip
docker compose logs glitchtip

# Ver logs de errores reportados por un microservicio
docker compose logs ms-auth | grep -i sentry

# Ver los volúmenes de Glitchtip
docker volume ls | grep glitchtip
```
