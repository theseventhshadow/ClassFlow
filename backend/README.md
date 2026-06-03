# ClassFlow Back

Sobre el Proyecto
-------
Proyecto backend compuesto por microservicios Spring Boot para gestionar funcionalidades de un sistema escolar (autenticación, académico, asistencia, mensajería, notificaciones, gateway y BFF). Orquestado con `docker-compose` para despliegue local mediante contenedores PostgreSQL por servicio.

Proyecto Relacionado
-------------------
Este repositorio contiene el **Backend**. 
Para la interfaz de usuario, revisa el **Frontend** aquí:
[ClassFlow-Front](https://github.com/theseventhshadow/ClassFlow-Front)

Ambos proyectos son necesarios para ejecutar ClassFlow completo.

Arquitectura y servicios
------------------------
- [gateway-service](gateway-service/README.md) (puerto 8080): API Gateway (Spring Cloud Gateway).
- [auth-service](auth-service/README.md) (puerto 8081): Autenticación y gestión de usuarios (JWT + Spring Security).
- [academic-service](academic-service/README.md) (puerto 8082): Cursos, asignaturas, evaluaciones, notas.
- [assistance-service](assistance-service/README.md) (puerto 8083): Asistencia y anotaciones.
- [message-service](message-service/README.md) (puerto 8084): Mensajes y anuncios.
- [notification-service](notification-service/README.md) (puerto 8085): Envío de emails/alertas.
- [bff-service](bff-service/README.md) (puerto 8086): Backend-for-Frontend con endpoints agregados (dashboard).

Estructura de Carpetas
---------------------
```
ClassFlow-Back/
├── docker-compose.yml                  # Orquestación de servicios y bases de datos
├── README.md                           # Este archivo
├── docs/
│   └── README.md                       # Documentación complementaria
│
├── gateway-service/                    # API Gateway (Spring Cloud Gateway)
│   ├── pom.xml
│   ├── Dockerfile
│   ├── mvnw / mvnw.cmd
│   └── src/
│       ├── main/
│       │   ├── java/
│       │   │   └── gateway_service/
│       │   │       ├── controller/
│       │   │       └── config/
│       │   └── resources/
│       │       ├── application.yml
│       │       └── application-docker.yml
│       └── test/
│
├── auth-service/                       # Autenticación, Usuarios, JWT
│   ├── pom.xml
│   ├── Dockerfile
│   ├── mvnw / mvnw.cmd
│   └── src/
│       ├── main/
│       │   ├── java/
│       │   │   └── auth_service/
│       │   │       ├── controller/
│       │   │       ├── service/
│       │   │       ├── repository/
│       │   │       ├── entity/
│       │   │       └── security/
│       │   └── resources/
│       │       ├── application.yml
│       │       ├── application-docker.yml
│       │       ├── application.properties
│       │       └── db/migration/
│       │           ├── V1__initial_schema.sql
│       │           ├── V2__seed_data.sql
│       │           └── V5__encrypt_seed_passwords.sql
│       └── test/
│
├── academic-service/                   # Cursos, Asignaturas, Evaluaciones, Notas
│   ├── pom.xml
│   ├── Dockerfile
│   ├── mvnw / mvnw.cmd
│   └── src/
│       ├── main/
│       │   ├── java/
│       │   │   └── academic_service/
│       │   │       ├── controller/
│       │   │       ├── service/
│       │   │       ├── repository/
│       │   │       ├── entity/
│       │   │       └── dto/
│       │   └── resources/
│       │       ├── application.yml
│       │       ├── application-docker.yml
│       │       └── db/migration/
│       └── test/
│
├── assistance-service/                 # Asistencia, Anotaciones
│   ├── pom.xml
│   ├── Dockerfile
│   └── src/
│       ├── main/java/assistance_service/
│       │   ├── controller/
│       │   ├── service/
│       │   └── repository/
│       └── main/resources/db/migration/
│
├── message-service/                    # Mensajes, Anuncios
│   ├── pom.xml
│   ├── Dockerfile
│   └── src/
│       ├── main/java/message_service/
│       │   ├── controller/
│       │   ├── service/
│       │   └── repository/
│       └── main/resources/db/migration/
│
├── notification-service/               # Notificaciones, Emails/Alertas
│   ├── pom.xml
│   ├── Dockerfile
│   └── src/
│       ├── main/java/notification_service/
│       │   ├── controller/
│       │   ├── service/
│       │   └── repository/
│       └── main/resources/db/migration/
│
└── bff-service/                        # Backend-for-Frontend, Dashboard
    ├── pom.xml
    ├── Dockerfile
    └── src/
        ├── main/
        │   ├── java/bff_service/
        │   │   ├── controller/
        │   │   ├── service/
        │   │   └── dto/
        │   └── resources/
        │       ├── application.yml
        │       └── application-docker.yml
        └── test/
```

Archivos clave
-------------
- Orquestación: [docker-compose.yml](docker-compose.yml#L1-L200)
- Configuración por servicio: `application.yml` / `application.properties` y `application-docker.yml` (ver carpetas de cada servicio).
- Migraciones Flyway: `src/main/resources/db/migration` en cada servicio.

Requisitos
----------
- JDK 25
- Maven (o usar `./mvnw` incluido)
- Docker and Docker Compose (para ejecutar con contenedores)

Obtener el código
-----------------

**Opción 1: Clonar desde GitHub**
```bash
git clone https://github.com/theseventhshadow/ClassFlow-Back.git
cd ClassFlow-Back
```

**Opción 2: Descargar como ZIP**
1. Ve a https://github.com/theseventhshadow/ClassFlow-Back
2. Haz clic en el botón verde "Code"
3. Selecciona "Download ZIP"
4. Extrae el archivo en tu directorio de trabajo
5. Abre la carpeta en tu editor (VS Code, IntelliJ, etc.)

Inicio rápido (desarrollo con Maven)
----------------------------------
Desde la raíz de cada servicio (ej. `auth-service`) ejecutar:

```bash
./mvnw spring-boot:run
```

Inicio rápido (con Docker Compose)
---------------------------------
Levanta todos los servicios y bases de datos PostgreSQL definidos en `docker-compose.yml`:

```bash
docker compose up --build
```

Usar el perfil `docker` (ya configurado en `docker-compose.yml`) que hace que cada servicio apunte a los nombres de servicio DB y a los hosts internos.

Bases de datos y migraciones
---------------------------
- En desarrollo los servicios usan H2 en memoria. En Docker se usan contenedores Postgres dedicados por servicio (p. ej. `auth-db`).
- Flyway está habilitado y las migraciones se almacenan en `src/main/resources/db/migration` (archivos `V1__...`, `V2__...`, ...). Revisar dichos archivos para el esquema y datos seed.

Endpoints principales (resumen)
------------------------------

- Auth (`/api/auth`): `POST /login`, `POST /register`, `GET /validate`, `GET /users/{id}`, `PUT /users/{id}`, `POST /change-password`, `GET /me`.
- Academic (`/api/courses`, `/api/subjects`, `/api/evaluations`, `/api/grades`): CRUD estándar (GET, POST, PUT, DELETE) y consultas por relaciones (ej. `/api/subjects/course/{courseId}`).
- Assistance (`/api/attendance`, `/api/annotations`): registro y consultas por estudiante, curso y fecha.
- Message (`/api/messages`, `/api/announcements`): envío, listado, marcar como leído, anuncios activos.
- Notification (`/api/notifications`): `POST /email`, `POST /alert`, `POST /create`, `GET /user/{userId}`, `PUT /{id}/sent`.
- BFF (`/api/bff`): endpoints de dashboard agregados (p. ej. `/api/bff/dashboard/{userId}`, `/api/bff/dashboard/stats/{userId}`).

Build y creación de imágenes Docker
----------------------------------
- Cada servicio incluye un `Dockerfile` y `mvnw` para construir el JAR y generar la imagen.
- Ejemplo (desde la raíz):

```bash
docker build -t auth-service:local ./auth-service
```

Pruebas
------
- Hay clases de prueba en `src/test/java` para cada servicio. Ejecutar pruebas con Maven:

```bash
./mvnw test
```

Consideraciones de seguridad y configuración
-------------------------------------------
- `auth-service` usa JWT (clave en `application.yml` para desarrollo).
- `notification-service` incluye parámetros SMTP de ejemplo en `application.properties` — no dejar credenciales en claro.

Archivos y referencias rápidas
-----------------------------
- `docker-compose.yml` — orquestación
- `*/pom.xml` — dependencias y Java/Spring Boot
- `*/src/main/resources/application-docker.yml` — configuración para contenedores
- `*/src/main/resources/db/migration` — migraciones Flyway
- `*/src/main/java/*/controller` — controladores y endpoints

Diagramas
---------
Arquitectura (visión general):
<img width="1218" height="523" alt="Captura de pantalla 2026-05-15 163922" src="https://github.com/user-attachments/assets/3803f878-ad8a-4aa0-9678-34e6a7b6e317" />


Secuencia: flujo de autenticación (simplificado)
<img width="1361" height="670" alt="Captura de pantalla 2026-05-15 163802" src="https://github.com/user-attachments/assets/e897da4b-dc04-40fb-9c31-293bbc5121d9" />


Ejemplos prácticos
------------------
Comandos útiles:

```bash
# Construir todos los módulos (desde la raíz)
./mvnw -T 1C -DskipTests package

# Ejecutar todos los servicios en contenedores
docker compose up --build

# Ejecutar un servicio localmente (ej. auth-service)
cd auth-service
../mvnw spring-boot:run
```

Ejemplos `curl` (gateway en `localhost:8080` o directamente al servicio por su puerto):

```bash
# 1) Login (Auth) -> obtiene JWT
curl -X POST http://localhost:8081/api/auth/login \
	-H "Content-Type: application/json" \
	-d '{"email":"admin@example.com","password":"password"}'

# 2) Crear usuario (registro)
curl -X POST http://localhost:8081/api/auth/register \
	-H "Content-Type: application/json" \
	-d '{"firstName":"Luis","lastName":"Martín","idNumber":"10000010","email":"luis.martin@example.com","password":"password","role":"TEACHER"}'

# 3) Dashboard BFF (ejemplo de uso con token)
curl -X GET http://localhost:8086/api/bff/dashboard/1 \
	-H "Authorization: Bearer <TOKEN_OBTENIDO>"

# 4) Enviar mensaje (Message Service)
curl -X POST http://localhost:8084/api/messages/send \
	-H "Content-Type: application/json" \
	-d '{"senderId":1,"receiverId":2,"subject":"Prueba","body":"Hola"}'

# 5) Enviar email (Notification Service)
curl -X POST http://localhost:8085/api/notifications/email \
	-H "Content-Type: application/json" \
	-d '{"to":"destino@example.com","subject":"Asunto prueba","body":"Cuerpo del mensaje"}'
```

Notas sobre perfiles y configuración
-----------------------------------
- Para ejecución dentro de Docker, los servicios usan `SPRING_PROFILES_ACTIVE=docker` (configuración en `application-docker.yml`).
- No commits de secretos: sustituir `JWT_SECRET` y credenciales SMTP por variables de entorno o usar un vault.



