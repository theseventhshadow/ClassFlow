# Libro de Clases Digital — Frontend
Colegio Bernardo O'Higgins — Coquimbo

Aplicación web SPA (Single Page Application) desarrollada en React 18 con TypeScript que actúa como capa de presentación de la plataforma de libro de clases digital. Consume los microservicios del sistema a través de un API Gateway centralizado (puerto 8080) y soporta comunicación en tiempo real mediante WebSocket (STOMP).

---

## Tabla de Contenidos

- [Stack Tecnológico](#stack-tecnológico)
- [Prerrequisitos](#prerrequisitos)
- [Instalación y Configuración](#instalación-y-configuración)
- [Variables de Entorno](#variables-de-entorno)
- [Comandos Disponibles](#comandos-disponibles)
- [Estructura del Proyecto](#estructura-del-proyecto)
- [Módulos del Sistema](#módulos-del-sistema)
- [Comunicación con el Backend](#comunicación-con-el-backend)
- [Autenticación](#autenticación)
- [Accesibilidad](#accesibilidad)
- [Despliegue con Docker](#despliegue-con-docker)
- [Autores](#autores)

---

## Stack Tecnológico

| Capa | Tecnología | Versión |
|---|---|---|
| Framework UI | React | 18.x |
| Lenguaje | TypeScript | 5.x |
| Estilos | Tailwind CSS | 3.x |
| Bundler | Vite | 5.x |
| Enrutamiento | React Router | 6.x |
| Cliente HTTP | Axios | 1.x |
| WebSocket | STOMP.js | 7.x |
| Testing unitario | Vitest | 1.x |
| Testing E2E | Playwright | 1.x |

---

## Prerrequisitos

- Node.js `>= 20.x`
- npm `>= 10.x`
- Acceso al archivo `.env.local` provisto por el equipo de desarrollo
- Los microservicios del backend levantados localmente o accesibles en red

---

## Instalación y Configuración

Clonar el repositorio e instalar las dependencias:

```bash
git clone https://github.com/organizacion/classflow-frontend.git
cd classflow-frontend
npm install
```

Copiar la plantilla de variables de entorno:

```bash
cp .env.example .env.local
```

Completar los valores en `.env.local` según la sección [Variables de Entorno](#variables-de-entorno) y luego iniciar el servidor de desarrollo:

```bash
npm run dev
```

La aplicación estará disponible en `http://localhost:5173`.

---

## Variables de Entorno

Las variables de entorno se gestionan a través de archivos `.env`. El repositorio incluye un archivo `.env.example` con todas las claves requeridas sin valores. **Nunca versionar `.env.local` ni ningún archivo que contenga valores reales.**

`.env.example`:

```env
# URL base del API Gateway
VITE_API_GATEWAY_URL=

# URL base del BFF (Backend for Frontend)
VITE_BFF_URL=

# URL del endpoint WebSocket (STOMP)
VITE_WS_URL=

# Entorno de ejecución: development | staging | production
VITE_APP_ENV=
```

> Las variables `VITE_` son resueltas por Vite en tiempo de compilación y quedan embebidas en el bundle estático. No existe inyección en runtime para una aplicación servida desde Nginx. Cualquier cambio de valor requiere un nuevo build. El archivo `.env.local` está incluido en `.gitignore` y debe distribuirse de forma segura fuera del control de versiones.

---

## Comandos Disponibles

```bash
# Servidor de desarrollo con hot-reload
npm run dev

# Compilación optimizada para producción
npm run build

# Vista previa del build de producción en local
npm run preview

# Ejecución de tests unitarios
npm run test

# Tests unitarios en modo watch
npm run test:watch

# Reporte de cobertura de código (umbral mínimo: 60%)
npm run test:coverage

# Tests end-to-end con Playwright
npm run test:e2e

# Análisis estático con ESLint
npm run lint

# Formateo de código con Prettier
npm run format

# Auditoría de accesibilidad (A11Y)
npm run audit:a11y
```

---

## Estructura del Proyecto

```
public/                        # Recursos estáticos públicos (favicon, robots.txt)
src/
├── components/
│   ├── common/                # Primitivos de UI reutilizables
│   │   ├── Button.tsx
│   │   ├── Error.tsx
│   │   ├── Input.tsx
│   │   ├── Loading.tsx
│   │   ├── MediaImage.tsx
│   │   └── index.ts
│   └── layout/
│       ├── Layout.tsx         # Componente raíz de estructura de página
│       └── index.ts
├── config/
│   └── index.ts               # Configuración global (baseURL, timeouts, flags)
├── constants/
│   └── index.ts               # Constantes de dominio (roles, estados, rutas)
├── context/
│   ├── AuthContext.tsx        # Contexto de autenticación y sesión
│   ├── ThemeContext.tsx       # Contexto de tema visual
│   └── index.ts
├── hooks/
│   ├── useAsync.ts            # Manejo genérico de operaciones asíncronas
│   ├── useFetch.ts            # Abstracción de peticiones HTTP con estado de carga
│   ├── useForm.ts             # Manejo de formularios y validación
│   └── index.ts
├── pages/
│   ├── DashboardPage.tsx      # Vista principal post-login por rol
│   ├── HomePage.tsx           # Página de inicio / login
│   ├── NotFoundPage.tsx       # Vista 404
│   └── index.ts
├── router/
│   └── index.tsx              # Definición de rutas y guards de autenticación por rol
├── services/
│   ├── api.service.ts         # Instancia de Axios e interceptores JWT
│   ├── media.service.ts       # Integración con el servicio de medios
│   ├── user.service.ts        # Integración con el Auth Service
│   └── index.ts
├── styles/
│   └── index.css              # Estilos globales y directivas de Tailwind
├── types/
│   └── index.ts               # Tipos TypeScript y DTOs compartidos con el backend
├── utils/
│   ├── formatters.ts          # Formateadores de fechas, números y texto
│   ├── helpers.ts             # Funciones auxiliares de propósito general
│   ├── validators.ts          # Validadores reutilizables
│   └── index.ts
├── App.tsx                    # Componente raíz y árbol de providers
├── main.tsx                   # Entry point de la aplicación
└── vite-env.d.ts              # Declaraciones de tipos para variables de entorno Vite
```

---

## Módulos del Sistema

### Autenticación y Control de Acceso

Gestiona el login, la sesión y la navegación por rol para los cuatro actores del sistema. Consume el Auth Service a través de `/api/auth/**`.

### Gestión Académica

Permite a los docentes administrar cursos, asignaturas, evaluaciones y calificaciones. Los estudiantes pueden consultar su rendimiento académico en tiempo real. Consume el Academic Service a través de `/api/courses/**`, `/api/subjects/**`, `/api/evaluations/**` y `/api/grades/**`.

### Registro de Asistencia y Anotaciones

Reemplaza el libro de clases físico. Los docentes registran la asistencia diaria y las anotaciones de conducta positivas y negativas. Consume el Attendance Service a través de `/api/attendance/**` y `/api/annotations/**`.

### Portal de Mensajería

Comunicación bidireccional entre todos los actores del colegio. Los anuncios institucionales se transmiten en tiempo real mediante WebSocket STOMP. Consume el Messaging Service a través de `/api/messages/**` y `/api/announcements/**`.

### Centro de Notificaciones

Muestra las alertas del sistema: inasistencias, nuevas calificaciones y mensajes no leídos. Consume el Notifications Service a través de `/api/notifications/**`.

---

## Comunicación con el Backend

Todas las peticiones HTTP pasan por el API Gateway en el puerto **8080**. La instancia de Axios configurada en `src/services/` aplica los siguientes interceptores de forma transversal:

- Adjunta el token JWT en el header `Authorization: Bearer <token>` en cada petición saliente.
- Detecta respuestas `401 Unauthorized` y ejecuta automáticamente el flujo de refresco mediante `POST /api/auth/refresh`.
- Reintenta la petición original con el token renovado.
- Redirige al login si el refresh token también ha expirado.

### Tabla de Enrutamiento del Gateway

| Prefijo | Servicio destino | Puerto |
|---|---|---|
| `/api/bff/**` | BFF — Backend for Frontend | 8086 |
| `/api/auth/**` | Auth Service | 8081 |
| `/api/courses/**` | Academic Service | 8082 |
| `/api/subjects/**` | Academic Service | 8082 |
| `/api/evaluations/**` | Academic Service | 8082 |
| `/api/grades/**` | Academic Service | 8082 |
| `/api/attendance/**` | Attendance Service | 8083 |
| `/api/annotations/**` | Attendance Service | 8083 |
| `/api/messages/**` | Messaging Service | 8084 |
| `/api/announcements/**` | Messaging Service | 8084 |
| `/api/notifications/**` | Notifications Service | 8085 |

### BFF (Backend for Frontend)

El BFF en el puerto **8086** agrega respuestas de múltiples microservicios en una sola llamada HTTP. El frontend lo consume en los dashboards que requieren datos combinados, por ejemplo el resumen del docente con asistencia, calificaciones y mensajes pendientes en una única respuesta, eliminando waterfalls de peticiones en el cliente.

---

## Autenticación

El sistema implementa autenticación JWT stateless con las siguientes garantías de seguridad:

- El token de acceso se almacena exclusivamente en memoria, nunca en `localStorage` ni `sessionStorage`, para eliminar la superficie de ataque por XSS.
- El refresh token se almacena en una cookie `HttpOnly`, inaccesible desde JavaScript.
- Los guards de ruta (`ProtectedRoute`) validan el rol del usuario autenticado antes de renderizar cada página. Un rol sin permiso recibe una redirección a la vista `403`.

Los roles del sistema son: `ADMINISTRADOR`, `DOCENTE`, `ESTUDIANTE`, `APODERADO`.

---

## Accesibilidad

La plataforma cumple con los estándares WCAG 2.1 nivel AA, garantizando el acceso inclusivo a la información académica para toda la comunidad educativa, en cumplimiento del requerimiento no funcional RNF4.

Las medidas implementadas son:

- Estructura semántica con etiquetas HTML5 (`<main>`, `<nav>`, `<section>`, `<article>`, `<header>`).
- Atributos `aria-label`, `aria-describedby`, `aria-live` y `role` en todos los componentes interactivos.
- Navegación completa por teclado en todos los flujos críticos del sistema.
- Relación de contraste mínima de 4.5:1 en texto sobre fondo, conforme al criterio 1.4.3 de WCAG.
- Auditoría automática integrada en el pipeline de CI mediante `axe-core`.

Para ejecutar la auditoría de forma manual:

```bash
npm run audit:a11y
```

---

## Despliegue con Docker

El `docker-compose.yml` de este repositorio gestiona únicamente el contenedor del frontend. Los servicios del backend (API Gateway, BFF y microservicios) se orquestan desde su propio repositorio y deben estar levantados antes de iniciar el frontend.

Las variables `VITE_` se pasan al build mediante `build.args`, que las lee desde el `.env.local` del host. Vite las embebe en el bundle estático durante la compilación; no existe inyección en runtime sobre una imagen Nginx.

```yaml
version: "3.9"

services:

  frontend:
    build:
      context: .
      dockerfile: Dockerfile
      target: production
      args:
        VITE_API_GATEWAY_URL: ${VITE_API_GATEWAY_URL}
        VITE_BFF_URL: ${VITE_BFF_URL}
        VITE_WS_URL: ${VITE_WS_URL}
        VITE_APP_ENV: ${VITE_APP_ENV}
    container_name: classflow-frontend
    ports:
      - "3000:3000"
    networks:
      - classflow-network
    restart: unless-stopped

networks:
  classflow-network:
    driver: bridge
```

> Para conectar este contenedor con los servicios del backend, la red `classflow-network` debe declararse como externa si los contenedores del backend ya están corriendo en esa red:
>
> ```yaml
> networks:
>   classflow-network:
>     external: true
> ```

### Comandos Docker

```bash
# Construir la imagen del frontend (lee build.args desde .env.local)
docker compose build frontend

# Levantar todo el sistema en segundo plano
docker compose up -d

# Levantar únicamente el frontend
docker compose up -d frontend

# Ver logs del frontend en tiempo real
docker compose logs -f frontend

# Reconstruir y reiniciar el frontend tras cambios en el código
docker compose up -d --build frontend

# Detener todos los contenedores
docker compose down

# Detener y eliminar contenedores, redes y volúmenes
docker compose down -v
```

La aplicación estará disponible en `http://localhost:3000` una vez que el contenedor esté activo.

### Pipeline CI/CD

Antes de construir y publicar la imagen Docker, el pipeline ejecuta las siguientes verificaciones en orden:

1. Instalación reproducible de dependencias (`npm ci`).
2. Análisis estático de código (`npm run lint`).
3. Tests unitarios con reporte de cobertura (`npm run test:coverage`). El pipeline falla si la cobertura no supera el **60%**.
4. Auditoría de accesibilidad (`npm run audit:a11y`).
5. Build de producción (`npm run build`).
6. Construcción y publicación de la imagen Docker con los `build.args` del entorno destino.

---

## Autores

Evens Reneus — Desarrollo Frontend y Arquitectura de Componentes

Angelo Millán — Desarrollo Frontend e Integración con Microservicios

---

Institución: Colegio Bernardo O'Higgins · Coquimbo
Año: 2026
