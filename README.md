# SeedCraft-Backend

Spring Boot service that exposes a small REST API for **categories** and **products**, backed by PostgreSQL with Flyway migrations and OpenAPI (Swagger UI).

## Requirements

- **Java 17**
- **PostgreSQL** reachable at the URL configured in `src/main/resources/application.properties`
- **Maven** (or use the included Maven Wrapper: `./mvnw`)

The project depends on a **Spring Boot snapshot** parent; Maven resolves it from the `spring-snapshots` repository defined in `pom.xml`.

## Database

Create a database and user that match your settings. The app reads DB connection values from environment variables (`DB_*`, with fallback to `SPRING_DATASOURCE_*`) so credentials are not hardcoded.

1. Copy the example file:

```bash
cp .env.example .env
```

2. Edit `.env` with your credentials:

- `DB_URL` (default: `jdbc:postgresql://localhost:5432/mocks`)
- `DB_USERNAME` (default: `mocks_admin`)
- `DB_PASSWORD` (default: `password`)

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

## Docker

Build the image from the project root:

```bash
docker build -t seedcraft-backend .
```

Run the container and publish the API on port 8080 using your `.env` file:

```bash
docker run --rm -p 8080:8080 --env-file .env seedcraft-backend
```

Or pass standard Spring variables directly (useful on managed deploy platforms):

```bash
docker run --rm -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/mocks \
  -e SPRING_DATASOURCE_USERNAME=mocks_admin \
  -e SPRING_DATASOURCE_PASSWORD=password \
  seedcraft-backend
```

## API

Official base path for controllers is **`/api/v1`**.

| Method | Path | Description |
|--------|------|-------------|
| GET | `/api/v1/health` | Liveness check (`{"status":"ok","service":"seedcraft-api","version":"v1"}`) |
| GET/POST | `/api/v1/products` | List or create a product |
| POST | `/api/v1/products/batch` | Create many products |
| GET | `/api/v1/products/{id}` | Product by id |
| GET | `/api/v1/products/{id}/with-category` | Product with category |
| GET/POST | `/api/v1/categories` | List or create a category |
| POST | `/api/v1/categories/batch` | Create many categories |
| GET | `/api/v1/categories/{id}` | Category by id |
| GET | `/api/v1/categories/{id}/with-products` | Category with products |
| GET | `/api/v1/categories/{id}/tree` | Category subtree |
| GET | `/api/v1/categories/tree` | Full category tree (roots with nested children) |

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
