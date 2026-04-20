# AGENTS.md

## Build & Run

```bash
# Backend (port 8080)
cd backend
./mvnw spring-boot:run     # dev mode
./mvnw package             # build JAR
./mvnw test                # run tests

# Frontend (port 4200)
cd frontend
npm start                  # dev mode
npm run build             # production build
```

## Tech Stack

- Java 21, Spring Boot 3.2.5, Maven
- PostgreSQL (prod) / H2 (dev)
- Angular 17+ (frontend at `localhost:4200`)
- JWT authentication with jjwt 0.12.3
- MapStruct for DTO mapping
- SpringDoc OpenAPI (Swagger UI)

## Dev URLs

- Swagger UI: `http://localhost:8080/swagger-ui`
- H2 Console: `http://localhost:8080/h2-console` (dev only)

## Critical: Lombok + MapStruct

The `pom.xml` annotation processor order is required:
1. Lombok first
2. MapStruct second

If MapStruct fails to see getters/setters, check the order in `maven-compiler-plugin` config.

## Entry Point

`backend/src/main/java/com/shopflow/BackendApplication.java`

## Packages

- `com.shopflow.auth` — login/register, JWT
- `com.shopflow.product` — products, categories
- `com.shopflow.cart` — shopping cart, coupons
- `com.shopflow.order` — orders, addresses
- `com.shopflow.dashboard` — role-based dashboards (ADMIN/SELLER/CUSTOMER)
- `com.shopflow.review` — product reviews, moderation
- `com.shopflow.shared` — security, entities, config, exceptions

## Notes

- Fullstack: Angular 17+ frontend + Spring Boot backend
- PostgreSQL must be running locally on port 5432 (or update `application.properties`)
- DB credentials in `application.properties` are for local dev only
- CORS configured in SecurityConfig.java to allow `http://localhost:4200`
