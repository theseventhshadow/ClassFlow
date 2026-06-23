# Frontend

## Resumen

ClassFlow Frontend es una Single Page Application (SPA) construida con React 18, TypeScript 5.3 y Vite. Sirve como interfaz de usuario para los cuatro roles del sistema (administrador, docente, estudiante y apoderado), comunicandose exclusivamente con el BFF a traves del API Gateway.

---

## Stack tecnologico

| Tecnologia | Version | Proposito |
|---|---|---|
| React | 18.2 | Libreria de UI |
| TypeScript | 5.3 | Tipado estricto |
| Vite | 8.0 | Bundler y dev server |
| React Router DOM | 6.20 | Enrutamiento del lado del cliente |
| Axios | 1.6 | Cliente HTTP |
| Vitest | 4.1 | Testing unitario |
| Testing Library | 16.3 | Testing de componentes React |
| Nginx | - | Servidor de produccion (Docker) |

---

## Estructura del proyecto

```
frontend/
  Dockerfile                    # Imagen Nginx para produccion
  nginx.conf                    # Configuracion de Nginx
  index.html                    # Entry point HTML
  package.json
  tsconfig.json                 # TypeScript strict: true
  vite.config.ts                # Alias de importacion con @
  public/
    assets/                     # Recursos estaticos
  src/
    App.tsx                     # Componente raiz
    main.tsx                    # Punto de entrada
    test-setup.ts               # Configuracion de Vitest
    config/
      index.ts                  # Configuracion global (API base URL, timeout)
    constants/
      index.ts                  # Rutas, HTTP status, localStorage keys
    types/
      index.ts                  # Tipos globales (ApiResponse, ApiError, etc.)
    context/
      AuthContext.tsx            # Estado global de autenticacion
      ThemeContext.tsx           # Estado global del tema
    services/
      api.service.ts            # Instancia de Axios con interceptores
      auth.service.ts           # Login, registro, validacion de token
      user.service.ts           # CRUD de usuarios
      dashboard.service.ts      # Consumo del BFF dashboard
      course.service.ts         # CRUD de cursos
      media.service.ts          # Subida de archivos multimedia
    hooks/
      useDashboardData.ts       # Logica de datos del dashboard admin
      useAsync.ts               # Hook generico para operaciones asincronas
      useFetch.ts               # Hook generico para peticiones GET
      useForm.ts                # Hook para manejo de formularios
    components/
      common/
        Button.tsx              # Componente boton reutilizable
        Input.tsx               # Componente input reutilizable
        Loading.tsx             # Indicador de carga
        Error.tsx               # Visualizacion de errores
        MediaImage.tsx          # Visualizacion de imagenes
        ProtectedRoute.tsx      # Guardian de rutas por rol
      layout/
        Layout.tsx              # Layout principal con Header/Sidebar
    pages/
      LoginPage.tsx             # Pagina de inicio de sesion
      AdminDashboard.tsx        # Dashboard del administrador
      TeacherAccountPage.tsx    # Portal del docente
      StudentDashboardPage.tsx  # Panel del estudiante
      GuardianDashboardPage.tsx # Panel del apoderado
      DashboardPage.tsx         # Dashboard generico
      HomePage.tsx              # Pagina de inicio
      NotFoundPage.tsx          # Pagina 404
      AccessDeniedPage.tsx      # Pagina de acceso denegado
      admin/                    # Componentes del dashboard admin
    styles/
      index.css                 # Estilos globales
    utils/
      formatters.ts             # Formateo de fechas, numeros, etc.
      validators.ts             # Validaciones de formularios
      helpers.ts                # Funciones auxiliares
```

---

## Alias de importacion

Configurados en `vite.config.ts` y `tsconfig.json`:

| Alias | Ruta |
|---|---|
| `@` | `./src` |
| `@components` | `./src/components` |
| `@pages` | `./src/pages` |
| `@hooks` | `./src/hooks` |
| `@services` | `./src/services` |
| `@context` | `./src/context` |
| `@utils` | `./src/utils` |
| `@types` | `./src/types` |
| `@styles` | `./src/styles` |
| `@config` | `./src/config` |
| `@constants` | `./src/constants` |

---

## Enrutamiento

Definido en `src/router/index.tsx` usando React Router DOM v6.

| Ruta | Componente | Rol requerido |
|---|---|---|
| `/` | Redirecciona a `/login` | Publico |
| `/login` | `LoginPage` | Publico |
| `/access-denied` | `AccessDeniedPage` | Todos |
| `/dashboard/admin` | `AdminDashboard` | `ADMINISTRATOR` |
| `/dashboard/teacher` | `TeacherAccountPage` | `TEACHER` |
| `/dashboard/student` | `StudentDashboardPage` | `STUDENT` |
| `/dashboard/guardian` | `GuardianDashboardPage` | `GUARDIAN` |
| `/dashboard` | `DashboardPage` | Autenticado |
| `*` | `NotFoundPage` | Publico |

### Proteccion de rutas

El componente `ProtectedRoute` verifica dos condiciones antes de renderizar una pagina:

1. **Autenticacion:** Valida que el token JWT almacenado en `localStorage` sea valido consultando al backend (`/api/auth/validate`).
2. **Autorizacion:** Verifica que el rol del usuario este incluido en `allowedRoles`. Si no coincide, redirige a `/access-denied`.

Mientras se valida el token, se muestra el componente `Loading`.

---

## Autenticacion

### Flujo de login

```
LoginPage -> AuthContext.login() -> authService.login() -> POST /api/auth/login
                                                              |
                                                    backend retorna { token, user }
                                                              |
                                              AuthContext almacena token y user
                                              en localStorage y estado global
```

- El token JWT se guarda en `localStorage` con clave `user_token`.
- Los datos del usuario se guardan en `localStorage` con clave `user_data`.
- En cada peticion posterior, el interceptor de Axios agrega automaticamente el header `Authorization: Bearer <token>`, excepto en endpoints publicos (`/auth/login`, `/auth/register`, `/auth/forgot-password`).

### AuthContext

El `AuthContext` expone las siguientes propiedades y metodos:

| Propiedad/Metodo | Tipo | Descripcion |
|---|---|---|
| `user` | `User \| null` | Usuario autenticado actual |
| `isAuthenticated` | `boolean` | Indica si hay sesion activa |
| `isLoading` | `boolean` | Estado de carga |
| `error` | `string \| null` | Mensaje de error |
| `login(email, password)` | `Promise<User>` | Inicia sesion |
| `logout()` | `void` | Cierra sesion y limpia localStorage |
| `updateProfile(data)` | `Promise<void>` | Actualiza perfil del usuario |
| `validate()` | `Promise<boolean>` | Valida el token actual contra el backend |

### Interfaces

```typescript
interface User {
  id: string;
  nombre: string;
  email: string;
  rol: UserRole; // ADMINISTRATOR | TEACHER | GUARDIAN | STUDENT
  activo: boolean;
  createdAt: string;
  subject?: string;
  phone?: string;
  bio?: string;
}

type UserRole = 'ADMINISTRATOR' | 'TEACHER' | 'GUARDIAN' | 'STUDENT';

interface LoginRequest {
  email: string;
  password: string;
}

interface LoginResponse {
  token: string;
  user: User;
}
```

---

## Conexion con el backend

El frontend se comunica exclusivamente con el **BFF** a traves del **API Gateway**. Nunca consume directamente los microservicios de dominio.

```
Frontend (Axios) -> API Gateway (/api/bff/...) -> BFF -> Microservicios
```

### ApiService

Clase singleton que encapsula una instancia de Axios con:

- **Base URL:** Configurable via `VITE_API_BASE_URL` (default: `/api`).
- **Timeout:** 30 segundos.
- **Interceptor de request:** Agrega token JWT en el header `Authorization` (excepto en endpoints publicos).
- **Interceptor de response:** Normaliza errores HTTP al formato `ApiError`.

---

## Paginas y funcionalidad por rol

### LoginPage (`/login`)

Formulario de inicio de sesion con campos de email y contrasena. Consume `POST /api/auth/login` a traves del gateway. En caso de exito, redirige al dashboard correspondiente segun el rol del usuario.

### AdminDashboard (`/dashboard/admin`)

Panel administrativo completo con las siguientes secciones:

| Seccion | Descripcion |
|---|---|
| **Stats** | Tarjetas con metricas: total usuarios, cursos activos, asistencias del dia, alertas activas |
| **Usuarios** | Tabla con listado de usuarios, roles, estado y ultimo acceso |
| **Asistencia por curso** | Grafico de barras con porcentaje de asistencia por curso |
| **Actividad reciente** | Timeline con acciones recientes del sistema |
| **Alertas** | Lista de alertas con severidad (baja, media, alta) |

Consume datos del BFF mediante el hook `useDashboardData()`.

### TeacherAccountPage (`/dashboard/teacher`)

Portal del docente con acceso a:

- **Cursos asignados** (vinculados al docente)
- **Registro de asistencia** por curso y fecha
- **Anotaciones** de estudiantes (positivas y negativas)
- **Calificaciones** por evaluacion

### StudentDashboardPage (`/dashboard/student`)

Panel del estudiante que muestra:

- **Calificaciones** filtradas por su propio ID de estudiante
- **Asistencias** personales
- **Evaluaciones proximas** (filtradas por el grado del estudiante)
- **Anuncios** del curso

### GuardianDashboardPage (`/dashboard/guardian`)

Panel del apoderado con informacion consolidada de los estudiantes a su cargo (`guardianId`), incluyendo calificaciones, asistencias y anotaciones.

---

## Hooks personalizados

| Hook | Descripcion |
|---|---|
| `useDashboardData()` | Obtiene y procesa los datos del dashboard administrativo desde el BFF. Retorna stats, usuarios, asistencias, actividad y alertas con estados de carga y error. |
| `useAsync(fn, deps)` | Envuelve una operacion asincrona con estados `loading`, `error` y `value`. |
| `useFetch(url)` | Hook generico para peticiones GET con estados de carga y error. |
| `useForm(initialValues, validate, onSubmit)` | Maneja estados de formulario, validacion y envio. |

---

## Servicios

| Servicio | Metodos principales | Endpoints del backend |
|---|---|---|
| `authService` | `login()`, `register()`, `validateToken()`, `logout()` | `/auth/login`, `/auth/register`, `/auth/validate` |
| `userService` | `getUsers()`, `getUserById()`, `createUser()`, `updateUser()`, `deleteUser()`, `updateProfile()`, `changePassword()` | `/auth/users/...` |
| `dashboardService` | `getDashboard(userId)` | `/bff/dashboard/{userId}` |
| `courseService` | CRUD de cursos | A traves del BFF |
| `mediaService` | Subida de archivos multimedia | - |

---

## Estilos

- CSS plano con nombres de clases modulares.
- Prefijos por portal para evitar conflictos:
  - `tp-` para el portal del docente (teacher).
  - `sp-` para el portal del estudiante (student).
  - `admin-` para el dashboard administrativo.
- Diseno responsivo.
- Archivo principal: `src/styles/index.css`.

---

## Testing

| Herramienta | Proposito |
|---|---|
| Vitest | Test runner |
| Testing Library (React) | Renderizado e interaccion de componentes |
| jsdom | Entorno DOM simulado para tests |

Comandos disponibles:

```bash
npm test          # Ejecutar tests (vitest run)
npm run test:watch   # Modo watch
npm run test:coverage # Cobertura de codigo
```

---

## Scripts disponibles

| Comando | Descripcion |
|---|---|
| `npm run dev` | Inicia servidor de desarrollo (puerto 3000) |
| `npm run build` | Compila para produccion |
| `npm run preview` | Previsualiza la build de produccion |
| `npm run lint` | Ejecuta ESLint |
| `npm run lint:fix` | Corrige errores de lint automaticamente |
| `npm run format` | Formatea codigo con Prettier |
| `npm run type-check` | Verifica tipos con TypeScript (`tsc --noEmit`) |

---

## Variables de entorno

| Variable | Default | Descripcion |
|---|---|---|
| `VITE_API_BASE_URL` | `/api` | URL base de la API (gateway en produccion, proxy en desarrollo) |
| `VITE_APP_NAME` | `ClassFlow` | Nombre de la aplicacion |
| `VITE_APP_VERSION` | `0.1.0` | Version de la aplicacion |
