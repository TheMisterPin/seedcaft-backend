# SeedCraft Backend

SeedCraft Backend is a Spring Boot 3 service that provides:

- Product and category CRUD APIs.
- Hierarchical category tree APIs.
- Inventory-focused dashboard APIs that return frontend-ready chart/table payloads.
- PostgreSQL persistence with Flyway migrations.
- OpenAPI documentation via Swagger UI.

## Tech Stack

- Java 17
- Spring Boot
- Spring Data JPA
- PostgreSQL
- Flyway
- SpringDoc OpenAPI
- Maven Wrapper (`./mvnw`)

## Running Locally

### 1) Configure database settings

The app reads datasource values from Spring environment variables:

- `SPRING_DATASOURCE_URL` (default: `jdbc:postgresql://localhost:5432/mocks`)
- `SPRING_DATASOURCE_USERNAME` (default: `mocks_admin`)
- `SPRING_DATASOURCE_PASSWORD` (default: `password`)

Flyway migration is enabled on startup.

### 2) Start the application

```bash
./mvnw spring-boot:run
```

Default URL: `http://localhost:8080`

### 3) Open API docs

- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

## Docker

Build image:

```bash
docker build -t seedcraft-backend .
```

Run container:

```bash
docker run --rm -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/mocks \
  -e SPRING_DATASOURCE_USERNAME=mocks_admin \
  -e SPRING_DATASOURCE_PASSWORD=password \
  seedcraft-backend
```

## API Base Path

Primary REST APIs are served under:

- `/api/v1`

## Health Endpoints

- `GET /api/v1/health`
- `GET /api/health` (legacy alias)

## Products API

Base: `/api/v1/products`

- `POST /` create product
- `POST /batch` create products in batch
- `PUT /{id}` update product
- `DELETE /{id}` delete product
- `GET /` list products (supports filters and pagination)
- `GET /{id}` get by id
- `GET /{id}/with-category` get product with category info

Supported list filters:

- `q`, `categoryId`, `categoryCode`, `minPrice`, `maxPrice`, `currency`

## Categories API

Base: `/api/v1/categories`

- `POST /` create category
- `POST /batch` create categories in batch
- `PUT /{id}` update category
- `DELETE /{id}` delete category
- `GET /` list categories (supports filters and pagination)
- `GET /{id}` get by id
- `GET /{id}/with-products` get category with products
- `GET /{id}/tree` get subtree from node
- `GET /tree` get full category tree

Supported list filters:

- `q`, `parentId`, `parentCode`

## Inventory Dashboard API

Base: `/api/v1/inventory/dashboard`

These endpoints are designed for frontend dashboards and return chart/table-ready JSON.

- `GET /` full dashboard bundle
- `GET /kpis`
- `GET /category-donut`
- `GET /warehouse-fill-line`
- `GET /stock-composition`
- `GET /top-bins`
- `GET /bin-heatmap`
- `GET /low-stock`
- `GET /inventory-value-line`

Common query params:

- `range`: `7d` | `30d` | `90d` (where supported)
- `warehouseCode`
- `categoryCode`
- `limit` (for ranked/table endpoints)

## Security

Spring Security is enabled with CSRF disabled.

- Permitted without auth: `/api/**`, `/v3/api-docs/**`, `/swagger-ui/**`
- Other routes use HTTP Basic auth if configured.

## Testing

Run tests with:

```bash
./mvnw test
```
