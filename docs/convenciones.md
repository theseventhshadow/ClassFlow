# Convenciones y Normas

## Tabla de Contenidos

- [Idioma](#idioma)
- [Backend - Java / Spring Boot](#backend---java--spring-boot)
- [Frontend - TypeScript / React](#frontend---typescript--react)
- [Base de Datos](#base-de-datos)
- [Git](#git)
- [Testing](#testing)
- [Documentacion](#documentacion)
- [Herramientas](#herramientas)

---

## Idioma

| Elemento | Idioma | Ejemplo |
|---|---|---|
| Codigo fuente (clases, metodos, variables) | Ingles | `UserService`, `findByEmail()`, `courseName` |
| Comentarios en el codigo | Espanol | `// Retorna el usuario por email` |
| Mensajes de commit | Espanol | `feat: agrega endpoint de login` |
| Documentacion (README, docs/) | Espanol | ClassFlow, docs/ |
| Nombres de tablas y columnas | Ingles | `users`, `courses`, `first_name` |
| DTOs y mensajes al usuario | Espanol | `"El email es obligatorio"` |

---

## Backend - Java / Spring Boot

### Estructura de paquetes

```
com.ohiggins.classflow.{artefacto}/
  controller/       # @RestController
  service/          # @Service
  repository/       # JpaRepository
  entity/           # @Entity
  dto/              # DTOs de entrada/salida
  config/           # @Configuration (Swagger, WebClient, etc.)
  exception/        # GlobalExceptionHandler, ErrorResponse
  security/         # (solo ms-auth) Seguridad JWT
  enums/            # (opcional) Enumeraciones
```

### Artefactos y package base

| Modulo | Package base |
|---|---|
| `ms-auth` | `com.ohiggins.classflow.auth` |
| `ms-academic` | `com.ohiggins.classflow.academic` |
| `ms-assistance` | `com.ohiggins.classflow.assistance` |
| `ms-message` | `com.ohiggins.classflow.message` |
| `ms-notification` | `com.ohiggins.classflow.notification` |
| `bff` | `com.ohiggins.classflow.bff` |
| `api-gateway` | `com.ohiggins.classflow.gateway` |

### Naming conventions

| Elemento | Convencion | Ejemplos |
|---|---|---|
| Clases | `PascalCase` | `UserService`, `JwtTokenProvider`, `CourseController` |
| Metodos | `camelCase` | `findByEmail()`, `generateToken()`, `registerUser()` |
| Variables | `camelCase` | `userService`, `jwtSecret`, `courseList` |
| Constantes | `UPPER_SNAKE_CASE` | `ROLE_ADMIN`, `JWT_EXPIRATION`, `API_BASE_URL` |
| Enums | `PascalCase` (tipo), `UPPER_SNAKE_CASE` (valores) | `Role.ADMINISTRATOR`, `NotificationType.EMAIL` |
| Archivos Java | `PascalCase.java` | `AuthController.java`, `GlobalExceptionHandler.java` |

### Anotaciones y orden

Las anotaciones de Spring Boot se colocan en este orden dentro de cada clase:

```java
@RestController
@RequestMapping("/api/recurso")
@RequiredArgsConstructor
@Tag(name = "Recurso", description = "Descripcion")
public class RecursoController {

    private final RecursoService recursoService;

    @GetMapping
    @Operation(summary = "Listar todos")
    public ResponseEntity<List<RecursoDTO>> getAll() {
        return ResponseEntity.ok(recursoService.findAll());
    }
}
```

### Lombok

Se utiliza Lombok para reducir codigo boilerplate:

| Anotacion | Uso |
|---|---|
| `@RequiredArgsConstructor` | Constructor con campos `final` (inyeccion de dependencias) |
| `@Builder` | Patron Builder en DTOs y entidades |
| `@Data` | `@Getter` + `@Setter` + `@ToString` + `@EqualsAndHashCode` en DTOs |
| `@Slf4j` | Logger estandar |

### Dependencias de uso comun

| Dependencia | Proposito |
|---|---|
| `spring-boot-starter-web` | REST API (servicios MVC) |
| `spring-boot-starter-webflux` | REST API reactiva (BFF, Gateway) |
| `spring-boot-starter-data-jpa` | Acceso a base de datos |
| `springdoc-openapi-starter-webmvc-ui` | Swagger UI (MVC) |
| `springdoc-openapi-starter-webflux-ui` | Swagger UI (WebFlux) |
| `flyway-core` + `flyway-database-postgresql` | Migraciones de base de datos |
| `lombok` | Codigo boilerplate |
| `postgresql` | Driver PostgreSQL |
| `h2` | Base de datos en memoria (desarrollo) |

### Endpoints REST

- Usar nombres de recursos en plural: `/api/courses`, `/api/users`, `/api/messages`
- Verbos HTTP semanticos: `GET` (listar/obtener), `POST` (crear), `PUT` (actualizar), `DELETE` (eliminar)
- Path params para recursos especificos: `/api/courses/{id}`
- Query params para filtros: `/api/grades/student/{studentId}`
- Respuestas con `ResponseEntity` para control explicito de codigos HTTP

### Manejo de errores

- Usar `@RestControllerAdvice` con `GlobalExceptionHandler`
- `RuntimeException` para errores de negocio validados
- Retornar codigos HTTP semanticos (400, 401, 404, 409, 500)
- Formato de respuesta JSON estandarizado

### Perfiles de Spring

| Perfil | Base de datos | Uso |
|---|---|---|
| `default` | H2 en memoria | Desarrollo local sin Docker |
| `docker` | PostgreSQL 15 | Contenedores Docker |

---

## Frontend - TypeScript / React

### Naming conventions

| Elemento | Convencion | Ejemplos |
|---|---|---|
| Componentes | `PascalCase.tsx` | `AdminDashboard.tsx`, `ProtectedRoute.tsx` |
| Hooks | `camelCase` con prefijo `use` | `useAuth()`, `useDashboardData()`, `useForm()` |
| Servicios | `camelCase` | `authService`, `userService` |
| Interfaces/Types | `PascalCase` | `User`, `LoginRequest`, `ApiResponse<T>` |
| Archivos de servicio | `kebab-case.service.ts` | `auth.service.ts`, `user.service.ts` |
| Archivos de hook | `camelCase.ts` | `useDashboardData.ts` |
| Archivos de utilidad | `kebab-case.ts` | `formatters.ts`, `validators.ts` |
| Constantes | `UPPER_SNAKE_CASE` | `API_BASE_URL`, `HTTP_STATUS.OK` |
| CSS clases | `kebab-case` con prefijo de portal | `admin-card`, `tp-dashboard`, `sp-grade` |

### TypeScript estricto

`tsconfig.json` tiene habilitado el modo estricto:

```json
{
  "compilerOptions": {
    "strict": true,
    "noUnusedLocals": true,
    "noUnusedParameters": true,
    "noFallthroughCasesInSwitch": true
  }
}
```

### Alias de importacion

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

### Estructura de componentes

```tsx
import React from 'react';
import { /* dependencias */ } from '@services';
import { /* componentes */ } from '@components';

interface ComponentProps {
  /* props tipadas */
}

export const ComponentName: React.FC<ComponentProps> = ({ prop1, prop2 }) => {
  // Logica del componente
  return (
    <div className="prefijo-componente">
      {/* JSX */}
    </div>
  );
};

export default ComponentName;
```

### Estilos

- CSS plano (sin preprocesadores ni CSS-in-JS)
- Nombres de clases con prefijo por portal:
  - `admin-` para el dashboard administrativo
  - `tp-` para el portal del docente (teacher)
  - `sp-` para el portal del estudiante (student)
  - `gp-` para el portal del apoderado (guardian)
- Diseno responsivo
- Archivo principal: `src/styles/index.css`

### ESLint + Prettier

Se utiliza ESLint con configuracion compartida y Prettier para formateo:

**ESLint:** `eslint:recommended`, `@typescript-eslint/recommended`, `plugin:react-hooks/recommended`, `prettier`

**Prettier:**
```json
{
  "semi": true,
  "trailingComma": "es5",
  "singleQuote": true,
  "printWidth": 100,
  "tabWidth": 2,
  "useTabs": false
}
```

### Testing (Vitest)

- Archivos de prueba: `*.test.tsx` en carpeta `__tests__/` junto al componente
- Usar `describe` / `it` / `expect` de Vitest
- Preferir `@testing-library/react` para tests de componentes
- Importar `@testing-library/jest-dom` en `test-setup.ts` para matchers DOM

### Git ignore

El archivo `.gitignore` del frontend excluye:

```
node_modules/
dist/
.env.local
.env.production
```

---

## Base de Datos

### Naming

| Elemento | Convencion | Ejemplos |
|---|---|---|
| Tablas | plural, minusculas, snake_case | `users`, `courses`, `evaluations`, `grades` |
| Columnas | singular, minusculas, snake_case | `first_name`, `last_name`, `created_at` |
| Primary keys | `id` (serial/BIGINT) | `id BIGSERIAL PRIMARY KEY` |
| Foreign keys | `{tabla}_id` | `course_id`, `student_id`, `evaluation_id` |
| Indices | `idx_{tabla}_{columna}` | `idx_users_email`, `idx_grades_student` |

### Migraciones Flyway

| Formato | Ejemplo |
|---|---|
| `V{version}__{descripcion}.sql` | `V1__initial_schema.sql` |
| Descripcion en snake_case, ingles | `V2__seed_users.sql` |
| Versiones secuenciales | `V1`, `V2`, ..., `V17` |

Cada microservicio gestiona sus propias migraciones en `src/main/resources/db/migration/`.

### Reglas

- No existen claves foraneas entre diferentes bases de datos.
- Cada microservicio solo accede a su propia base de datos.
- `ddl-auto: validate` en produccion: Flyway gestiona el esquema, JPA solo valida.
- En desarrollo con H2, Flyway tambien aplica las migraciones.

---

## Git

### Ramas

| Rama | Proposito |
|---|---|
| `main` | Produccion, codigo estable |
| `develop` | Integracion de caracteristicas |
| `feature/*` | Nuevas funcionalidades (ej: `feature/ms-academic`) |
| `fix/*` | Correccion de errores |
| `docs/*` | Cambios en documentacion |

### Mensajes de commit

Formato recomendado en espanol:

```
{ambito}: {mensaje en espanol, imperativo, sin punto final}
```

Ejemplos:
```
feat: agrega endpoint de registro de asistencia
fix: corrige validacion de RUT duplicado
docs: actualiza README con diagrama de arquitectura
refactor: extrae logica de JWT a JwtTokenProvider
test: agrega tests para AuthController
chore: actualiza dependencias de Spring Boot a 3.5.14
```

---

## Testing

### Convenciones para nombres de tests

```java
// Estructura: {metodo}_{escenario}_returns{resultadoEsperado}
void login_withValidCredentials_returnsToken()
void register_withDuplicateEmail_throwsException()
void findById_withNonExistentId_throwsNotFoundException()
void deleteUser_withValidId_setsActiveToFalse()
```

### Capas de prueba

| Tipo de test | Anotacion | Stack |
|---|---|---|
| Controller | `@WebMvcTest` | MockMvc, `@MockitoBean` |
| Service | `@ExtendWith(MockitoExtension.class)` | `@Mock`, `@InjectMocks` |
| Repository | `@DataJpaTest` | H2 en memoria |
| Config/Exception | Test unitario simple | JUnit 5 |

---

## Documentacion

- El README principal es una vision general del proyecto.
- Cada archivo en `docs/` profundiza en un tema especifico.
- Cada microservicio backend tiene su propio `README.md` con instrucciones de ejecucion local.
- El codigo fuente incluye comentarios JSDoc (frontend) y JavaDoc (backend) en metodos publicos.
- Las APIs se documentan con Swagger/OpenAPI (`@Operation`, `@ApiResponse`).

---

## Herramientas

| Herramienta | Version | Proposito |
|---|---|---|
| Java | 25 | Lenguaje backend |
| Spring Boot | 3.5.14 | Framework backend |
| Maven | 3.9.11 | Build backend |
| Node.js | 22 | Runtime frontend |
| TypeScript | 5.3 | Lenguaje frontend |
| Vite | 8.0 | Bundler frontend |
| Vitest | 4.1 | Test runner frontend |
| ESLint | 8.54 | Linter frontend |
| Prettier | 3.1 | Formateador frontend |
| Docker | latest | Contenerizacion |
| Kubernetes | latest | Orquestacion |
| Traefik | v2/v3 | Ingress Controller |
| PostgreSQL | 15 | Base de datos |
| Flyway | (via Spring Boot) | Migraciones DB |

### IDE

Se recomienda VS Code con las siguientes extensiones:

- **Extension Pack for Java** (backend)
- **Spring Boot Extension Pack**
- **ESLint** (frontend)
- **Prettier** (frontend)
- **GitLens** (control de versiones)
- **Docker** (gestion de contenedores)
- **YAML** (manifiestos Kubernetes)
