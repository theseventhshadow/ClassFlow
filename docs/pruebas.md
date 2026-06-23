# Pruebas

## Resumen

El proyecto incluye **54 clases de prueba**: 51 en el backend (JUnit 5 + Mockito) y 3 en el frontend (Vitest + Testing Library). Cada servicio backend tiene su propia configuracion de test con `application-test.properties` y una prueba de humo (`*ApplicationTests.java`) que verifica que el contexto de Spring se carga correctamente.

---

## Backend

### Tecnologias

| Herramienta | Proposito |
|---|---|
| JUnit 5 | Framework de pruebas unitarias |
| Mockito | Simulacion de dependencias |
| Spring Boot Test | `@WebMvcTest`, `@DataJpaTest`, `@ExtendWith(MockitoExtension.class)` |
| H2 (en memoria) | Base de datos para tests de repositorios |
| MockMvc | Pruebas de controladores REST |

### Cobertura por servicio

| Servicio | Tests | Capas cubiertas |
|---|---|---|
| `ms-auth` | 9 | Controller, Service (2), Security (JWT Provider + Filter), Repository, ExceptionHandler, SwaggerConfig, Smoke |
| `ms-academic` | 10 | Controller (4), Service (4), Repository (1), Smoke |
| `ms-assistance` | 9 | Controller (2), Service (2), Repository (2), ExceptionHandler, SwaggerConfig, Smoke |
| `ms-message` | 9 | Controller (2), Service (2), Repository (2), ExceptionHandler, SwaggerConfig, Smoke |
| `ms-notification` | 7 | Controller, Service, Repository, EmailService, ExceptionHandler, SwaggerConfig, Smoke |
| `api-gateway` | 4 | CorsConfig, GlobalExceptionHandler, SwaggerConfig, Smoke |
| `bff` | 3 | DashboardController, DashboardService, Smoke |
| **Total** | **51** | |

### Detalle de clases de prueba

#### ms-auth (9 tests)

| Clase | Tipo de test | Tecnica |
|---|---|---|
| `AuthServiceApplicationTests` | Smoke | `@SpringBootTest`, verifica que el contexto carga |
| `AuthControllerTest` | Controller | `@WebMvcTest`, `@MockitoBean` para servicios. Valida login (200/401), register (201), CRUD de usuarios |
| `AuthServiceTest` | Service | `@ExtendWith(MockitoExtension.class)`, mock de repositorios. Valida login, registro con validaciones de unicidad |
| `UserServiceTest` | Service | Mock de `UserRepository`. CRUD, soft-delete, transformacion entidad-DTO |
| `JwtTokenProviderTest` | Security | Verifica generacion y validacion de tokens JWT, extraccion de email, manejo de tokens invalidos/expirados |
| `JwtAuthenticationFilterTest` | Security | Simula peticiones con y sin token. Verifica que el filtro extrae y valida el JWT correctamente |
| `UserRepositoryTest` | Repository | `@DataJpaTest` con H2. Consultas derivadas: `findByEmail()`, `findByIdNumber()`, etc. |
| `GlobalExceptionHandlerTest` | Exception | Verifica que excepciones se traduzcan a JSON estandarizado (400/500) |
| `SwaggerConfigTest` | Config | Verifica que OpenAPI se inicializa sin errores |

#### ms-academic (10 tests)

| Clase | Tipo |
|---|---|
| `AcademicServiceApplicationTests` | Smoke |
| `CourseControllerTest` | Controller |
| `CourseServiceTest` | Service |
| `CourseRepositoryTest` | Repository |
| `SubjectControllerTest` | Controller |
| `SubjectServiceTest` | Service |
| `EvaluationControllerTest` | Controller |
| `EvaluationServiceTest` | Service |
| `GradeControllerTest` | Controller |
| `GradeServiceTest` | Service |

Cada controller test utiliza `@WebMvcTest` con MockMvc, simulando servicios con `@MockitoBean`. Cada service test utiliza `@ExtendWith(MockitoExtension.class)`. El repository test utiliza `@DataJpaTest` con H2.

#### ms-assistance (9 tests)

| Clase | Tipo |
|---|---|
| `AssistanceServiceApplicationTests` | Smoke |
| `AttendanceControllerTest` | Controller |
| `AttendanceServiceTest` | Service |
| `AttendanceRepositoryTest` | Repository |
| `AnnotationControllerTest` | Controller |
| `AnnotationServiceTest` | Service |
| `AnnotationRepositoryTest` | Repository |
| `GlobalExceptionHandlerTest` | Exception |
| `SwaggerConfigTest` | Config |

#### ms-message (9 tests)

| Clase | Tipo |
|---|---|
| `MessageServiceApplicationTests` | Smoke |
| `MessageControllerTest` | Controller |
| `MessageServiceTest` | Service |
| `MessageRepositoryTest` | Repository |
| `AnnouncementControllerTest` | Controller |
| `AnnouncementServiceTest` | Service |
| `AnnouncementRepositoryTest` | Repository |
| `GlobalExceptionHandlerTest` | Exception |
| `SwaggerConfigTest` | Config |

#### ms-notification (7 tests)

| Clase | Tipo |
|---|---|
| `NotificationServiceApplicationTests` | Smoke |
| `NotificationControllerTest` | Controller |
| `NotificationServiceTest` | Service |
| `NotificationRepositoryTest` | Repository |
| `EmailServiceTest` | Service |
| `GlobalExceptionHandlerTest` | Exception |
| `SwaggerConfigTest` | Config |

#### api-gateway (4 tests)

| Clase | Tipo |
|---|---|
| `GatewayServiceApplicationTests` | Smoke |
| `CorsConfigTest` | Config |
| `GlobalExceptionHandlerTest` | Exception |
| `SwaggerConfigTest` | Config |

#### bff (3 tests)

| Clase | Tipo |
|---|---|
| `BffServiceApplicationTests` | Smoke |
| `DashboardControllerTest` | Controller (con seguridad JWT simulada) |
| `DashboardServiceTest` | Service |

### Estrategia de pruebas

#### Pruebas de controladores (`@WebMvcTest`)

Se prueban con `@WebMvcTest(ControllerClass.class)` y `@MockitoBean` para simular los servicios, validando:

- Codigos de estado HTTP (200, 201, 400, 401, 404, etc.)
- Estructura del JSON de respuesta
- Manejo de errores (campos invalidos, recursos no encontrados)
- Headers de respuesta

**Ejemplo:** `AuthControllerTest` verifica:
- `POST /api/auth/login` con credenciales validas -> 200 + token JWT
- `POST /api/auth/login` con credenciales invalidas -> 401
- `POST /api/auth/register` con datos validos -> 201
- `POST /api/auth/register` con email duplicado -> 409

#### Pruebas de servicios (`@ExtendWith(MockitoExtension.class)`)

Se inyectan mocks con `@Mock` e `@InjectMocks`, validando:

- Reglas de negocio (unicidad de email, validacion de score <= maxScore, soft-delete)
- Transformaciones entidad-DTO
- Excepciones esperadas (`RuntimeException`, `EntityNotFoundException`)
- Casos limite (valores nulos, listas vacias)

#### Pruebas de repositorios (`@DataJpaTest`)

Se ejecutan sobre H2 en memoria con el esquema generado por Flyway, verificando:

- Consultas derivadas del nombre del metodo (`findByEmail()`, `findByStudentId()`, etc.)
- Consultas JPQL personalizadas (`findAllWithDetails()`, `findByCourseIdOrCourseIdIsNull()`)
- Operaciones CRUD basicas

#### Pruebas de seguridad

- `JwtTokenProviderTest`: Verifica generacion HS256, expiracion, validacion de firma, manejo de tokens malformados.
- `JwtAuthenticationFilterTest`: Simula peticiones HTTP con `MockHttpServletRequest`, verifica que el filtro extrae el token del header `Authorization` y establece el `SecurityContext`.

#### Pruebas de componentes transversales

- `GlobalExceptionHandlerTest`: Verifica que cada tipo de excepcion produzca la respuesta JSON estandarizada correcta.
- `SwaggerConfigTest`: Confirma que la configuracion de OpenAPI se inicializa sin errores.
- `CorsConfigTest`: Verifica que los headers CORS se configuren correctamente.

---

## Frontend

### Tecnologias

| Herramienta | Proposito |
|---|---|
| Vitest | Test runner (integrado con Vite) |
| Testing Library (React) | Renderizado e interaccion de componentes |
| jsdom | Entorno DOM simulado |
| user-event | Simulacion de eventos de usuario |

### Archivos de prueba

| Archivo | Componente | Verifica |
|---|---|---|
| `Button.test.tsx` | `Button` | Renderizado con diferentes variantes, clic, estado deshabilitado |
| `Loading.test.tsx` | `Loading` | Renderizado del spinner de carga |
| `Error.test.tsx` | `Error` | Renderizado del mensaje de error, clic en boton de reintento |

### Configuracion

`vite.config.ts` incluye la configuracion de Vitest:

```typescript
test: {
  globals: true,
  environment: 'jsdom',
  setupFiles: ['./src/test-setup.ts'],
  css: true,
}
```

El archivo `src/test-setup.ts` importa `@testing-library/jest-dom` para ampliar los matchers de Vitest con aserciones DOM (`.toBeInTheDocument()`, `.toHaveClass()`, etc.).

---

## Comandos

### Backend (Maven)

```bash
# Ejecutar todos los tests
cd backend
./mvnw test

# Ejecutar tests de un servicio especifico
cd backend/ms-auth
./mvnw test

# Ejecutar una clase de prueba especifica
cd backend/ms-auth
./mvnw test -Dtest=AuthControllerTest

# Ejecutar tests con reporte de cobertura (requiere JaCoCo)
./mvnw test -Pcoverage
```

### Frontend (npm)

```bash
# Ejecutar tests (una sola ejecucion)
cd frontend
npm test

# Modo watch (se re-ejecutan al cambiar archivos)
npm run test:watch

# Reporte de cobertura
npm run test:coverage
```

---

## Mejores practicas aplicadas

| Practica | Aplicacion |
|---|---|
| **Pruebas aisladas** | Cada test usa mocks o bases de datos en memoria. No dependen de servicios externos. |
| **Naming descriptivo** | Los metodos de prueba describen el escenario: `login_withValidCredentials_returnsToken()` |
| **Triple A (Arrange-Act-Assert)** | Estructura clara en cada metodo de prueba. |
| **Una asercion por concepto** | Cada test verifica un comportamiento especifico. |
| **Pruebas de humo** | Cada servicio tiene un `*ApplicationTests` que verifica que el contexto de Spring Boot se carga. |
| **Cobertura de casos borde** | Se prueban errores de validacion, recursos no encontrados, duplicados, y casos limite. |
