# SeedCraft-Backend

Spring Boot service that exposes a small REST API for **categories** and **products**, backed by PostgreSQL with Flyway migrations and OpenAPI (Swagger UI).

## Requirements

- **Java 17**
- **PostgreSQL** reachable at the URL configured in `src/main/resources/application.properties`
- **Maven** (or use the included Maven Wrapper: `./mvnw`)

The project depends on a **Spring Boot snapshot** parent; Maven resolves it from the `spring-snapshots` repository defined in `pom.xml`.

## Database

Create a database and user that match your settings. Defaults in `application.properties` are:

- Database: `mocks`
- URL: `jdbc:postgresql://localhost:5432/mocks`
- Username: `mocks_admin`
- Password: `password`

Flyway runs on startup (`baseline-on-migrate` is enabled so existing databases can be baselined before numbered migrations).

## Run

```bash
./mvnw spring-boot:run
```

Or:

```bash
mvn spring-boot:run
```

The app listens on the default Spring Boot port (**8080**) unless you override `server.port`.

## API

Official base path for controllers is **`/api/v1`**.

| Method | Path | Description |
|--------|------|-------------|
| GET | `/api/v1/health` | Liveness check (`{"status":"ok"}`) |
| GET/POST | `/api/v1/products` | List or create a product |
| POST | `/api/v1/products/batch` | Create many products |
| GET | `/api/v1/products/{id}` | Product by id |
| GET | `/api/v1/products/{id}/with-category` | Product with category |
| GET/POST | `/api/v1/categories` | List or create a category |
| POST | `/api/v1/categories/batch` | Create many categories |
| GET | `/api/v1/categories/{id}` | Category by id |
| GET | `/api/v1/categories/{id}/with-products` | Category with products |
| GET | `/api/v1/categories/{id}/tree` | Category subtree |

## OpenAPI / Swagger UI

After the app is running:

- **Swagger UI:** http://localhost:8080/swagger-ui/index.html
- **OpenAPI JSON:** http://localhost:8080/v3/api-docs

## Security

Spring Security is enabled with **CSRF disabled**. Requests under `/api/**`, `/v3/api-docs/**`, and `/swagger-ui/**` are **permitted without authentication**. Other routes require HTTP Basic credentials (configure users if you rely on those routes).

## Tests

```bash
./mvnw test
```
