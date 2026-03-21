# AGENTS.md — Codebase Guide for AI Coding Agents

## Project Overview

Full-stack monorepo with:
- **`order-api/`** — Spring Boot 4.0.1 REST API (Java 25, PostgreSQL, JWT auth)
- **`order-ui/`** — React 19 SPA (JavaScript, Vite 6, Axios, Mantine)

Authentication is stateless JWT (10-minute expiry, no refresh tokens). The backend uses domain-grouped packaging; the frontend uses feature-grouped folders.

---

## Build & Run Commands

### Backend (`order-api/`)

```bash
# Start the application (requires Postgres — see docker-compose.yml)
docker compose up -d               # start Postgres 18.0
./mvnw clean spring-boot:run       # run the API on :8080

# Build
./mvnw clean package               # build JAR with tests
./mvnw clean package -DskipTests   # build JAR without tests

# Run all tests
./mvnw test

# Run a single test class
./mvnw test -Dtest=OrderApiApplicationTests

# Run a single test method
./mvnw test -Dtest=OrderApiApplicationTests#contextLoads

# Run tests matching a pattern
./mvnw test -Dtest="*Controller*"
```

> **Note:** `OrderApiApplicationTests` uses `@MockitoBean` to mock all infrastructure and runs without a live Postgres. New tests should use `@WebMvcTest` for controllers, `@DataJpaTest` for repositories, and `@ExtendWith(MockitoExtension.class)` for service unit tests.

### Frontend (`order-ui/`)

```bash
# Install dependencies
npm install

# Start dev server on :3000
npm start

# Production build
npm run build

# Run all tests (CI mode — runs once and exits)
npm test

# Run a single test file by path pattern
npm test -- src/components/home/Login

# Run tests matching a name pattern
npm test -- -t "renders login"
```

> **Note:** Test files (`ComponentName.test.js` / `ComponentName.test.jsx`) are co-located with every component. New tests should use `@testing-library/react` + `@testing-library/user-event` (both already installed). `setupTests.js` registers `@testing-library/jest-dom` matchers via `expect.extend()` and mocks `matchMedia` and `localStorage` for Mantine compatibility.

### Integration Tests

```bash
# Manual integration test script (requires both services running)
./order-api/test-endpoints.sh      # zsh script; hits all endpoints with curl
```

---

## Architecture & Key Patterns

### Backend

- **Domain-grouped packages**, not layered: `order/`, `user/`, `security/`, `rest/`, `config/`, `runner/`
- **DTOs are Java records**: `record LoginRequest(String username, String password) {}`
- **Entities use Lombok**: `@Data`, `@NoArgsConstructor` on JPA entities — no manual getters/setters
- **`@RequiredArgsConstructor`** on all Spring beans instead of `@Autowired`
- **`@ResponseStatus` on exceptions** instead of `@ControllerAdvice`:
  ```java
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public class UserNotFoundException extends RuntimeException { ... }
  ```
- **Service layer** always has an interface + `ServiceImpl` implementation (`UserService` / `UserServiceImpl`)
- **Optional<T>** used for nullable service lookups; `validateAndGet{Entity}By{Key}()` methods throw on empty
- **Ordered repository queries**: use Spring Data derived query methods for deterministic ordering — e.g., `findAllByOrderByUsernameAsc()` in `UserRepository`, `findAllByOrderByCreatedAtDesc()` in `OrderRepository`. Do not rely on `findAll()` where order matters.
- **JWT errors**: each exception type caught individually, logged with `@Slf4j`, returns `Optional.empty()`

### Frontend

- **Function components with hooks only** — no class components anywhere
- **Centralized API layer**: all Axios calls live in `src/components/misc/OrderApi.js`
- **Auth state** managed via React Context in `src/components/context/AuthContext.js` (use `useAuth()` hook)
- **Error handling**: always call `handleLogError(error)` from `src/components/misc/Helpers.js` in catch blocks; set `isError` state for UI feedback
- **Token expiry** checked client-side in `AuthContext.userIsAuthenticated()` and in the Axios interceptor before each request

---

## Code Style

### Java

**Naming conventions:**
- Classes: `PascalCase` — `OrderServiceImpl`, `TokenAuthenticationFilter`
- Methods/fields: `camelCase` — `validateAndGetUserByUsername`, `jwtExpirationMinutes`
- Constants: `UPPER_SNAKE_CASE` — `TOKEN_TYPE`, `BEARER_KEY_SECURITY_SCHEME`
- Packages: all lowercase — `com.ivanfranchin.orderapi.order`
- Exception classes: `{Domain}NotFoundException`, `Duplicated{Domain}Exception`
- Validate-or-throw: `validateAndGet{Entity}By{Key}` naming pattern

**Formatting:**
- 4-space indentation
- Opening braces on the same line
- One blank line between methods
- Always include `@Override`
- Chained builder/stream calls: one method per line

**Imports (order by convention — no enforced tool):**
1. Internal project imports (`com.ivanfranchin.*`)
2. Third-party imports (Jakarta, JJWT, Lombok, Springdoc)
3. Spring Framework (`org.springframework.*`)
4. Java standard library (`java.*`)

**Types:**
- Use `Long` (boxed) for entity IDs; `String` for `Order.id` (UUID)
- Return `List<T>`, not `Collection<T>` or `Iterable<T>`
- Use Java records for all request/response DTOs
- Use `Optional<T>` for nullable lookups in service methods

**Bean Validation:** Use `@NotBlank`, `@Email`, `@Valid` on controller parameters. No custom validators exist yet; add new ones following Jakarta Validation conventions.

**Logging:** Use `@Slf4j` (Lombok). Log JWT/auth errors with `log.error(...)`. Do not use `System.out.println`.

### JavaScript / JSX

**Naming conventions:**
- Component files and functions: `PascalCase` — `AdminPage.js`, `function AdminPage() {}`
- Non-component utility files: `PascalCase` — `Helpers.js`, `OrderApi.js`, `Constants.js`
- Hooks: `camelCase` with `use` prefix — `useAuth()`
- Event handlers: `handle{Action}` — `handleSubmit`, `handleDeleteUser`, `handleInputChange`
- State variables: descriptive `camelCase` — `isLoading`, `isError`, `orderDescription`

**Formatting:**
- 2-space indentation
- Single quotes for all string literals (including JSX attributes)
- Arrow functions for handlers; named `function` declarations for components
- No Prettier is configured — match the style of the file being edited

**Imports (order by convention):**
1. React (`import React from 'react'`)
2. React ecosystem (`react-router-dom`, context hooks)
3. Mantine / Tabler Icons components
4. Local utilities (`OrderApi`, `Helpers`)
5. CSS files last

**No TypeScript, no PropTypes.** The project is plain JavaScript. Do not introduce TypeScript without explicit instruction.

---

## Error Handling

### Backend
- Throw domain-specific `RuntimeException` subclasses annotated with `@ResponseStatus`
- Never use `@ControllerAdvice` — the existing `@ResponseStatus` approach is intentional
- In JWT/filter code, catch each exception type individually and log; do not propagate as HTTP errors
- Wrap filter auth logic in `try/catch(Exception e)` with logging

### Frontend
- Always call `handleLogError(error)` (from `Helpers.js`) inside every `catch` block
- Set `isError(true)` state for user-facing error display
- Check `error.response.status` to differentiate 409 (conflict) vs 400 (validation) vs other errors
- Never `console.log` directly — use `handleLogError`

---

## Testing Guidelines

### Backend (JUnit 5 + Spring Test)
- Use `@WebMvcTest` for controller tests with `MockMvc` (no full context)
- Use `@DataJpaTest` for repository tests (embedded/test-container DB)
- Use `@SpringBootTest` only for true integration tests (requires Postgres)
- Mock dependencies with `@MockitoBean` (Spring Boot 4+ replacement for `@MockBean`)
- Use `@WithMockUser` from Spring Security Test for authenticated endpoint tests

### Frontend (Vitest + React Testing Library)
- Place test files as `ComponentName.test.js` co-located with the component
- Use `@testing-library/user-event` for simulating user interactions
- Use `@testing-library/jest-dom` matchers (`toBeInTheDocument`, `toHaveValue`, etc.)
- Mock `OrderApi.js` calls with `vi.mock('../misc/OrderApi')`
- Run a single test: `npm test -- src/components/path/ComponentName`

---

## Configuration

- **API base URL**: `order-ui/src/Constants.js` — `config.url.API_BASE_URL`
- **JWT secret & expiry**: `order-api/src/main/resources/application.yml` — `app.jwt.*`
- **Postgres connection**: `application.yml` — `spring.datasource.*`; matches the `docker-compose.yml` service
- **CORS**: `order-api/.../security/CorsConfig.java` — add new allowed origins here
- **Swagger UI**: available at `http://localhost:8080/swagger-ui.html` when running locally
- **Security routes**: `SecurityConfig.java` — add new public/protected endpoint matchers here
- **Role constants**: `SecurityConfig.ADMIN` and `SecurityConfig.USER` — reuse these strings; do not hardcode `"ADMIN"` or `"USER"` inline
