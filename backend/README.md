# SAMS backend

Spring Boot 3.3 (Java 17) backend for the Student Attendance Management System. The database schema is managed by Flyway (`src/main/resources/db/migration`) against MySQL 8.

## Package structure

```
edu.pesu.sams
├── config       application configuration beans (e.g. the Asia/Kolkata clock)
└── exception    API error type and global JSON error handler
```

Entities and repositories are added by #3; controllers, services and DTOs by the feature issues that follow (#4 onwards).

## Prerequisites

- JDK 17 or newer
- Maven 3.9+
- MySQL 8 with an empty `sams` database and a user that can create tables in it

## Configuration

Machine-specific values and secrets come from environment variables; nothing secret is committed. Copy `../.env.example` to `.env` and fill in your values.

| Variable | Required | Default | Purpose |
|---|---|---|---|
| `SAMS_DB_URL` | no | `jdbc:mysql://localhost:3306/sams?...` | JDBC URL |
| `SAMS_DB_USERNAME` | no | `sams` | Database user |
| `SAMS_DB_PASSWORD` | yes | (empty) | Database password |
| `SERVER_PORT` | no | `8080` | HTTP port |

## Build and test

```bash
cd backend
mvn verify
```

The tests use an in-memory H2 database, so they do not need MySQL.

## Run locally

```bash
cd backend
mvn spring-boot:run
```

Flyway applies any migrations in `src/main/resources/db/migration` on start-up. Hibernate only validates (`ddl-auto: validate`) that entities match the schema; it never changes the database itself.

## Health endpoint

```bash
curl http://localhost:8080/actuator/health
```

Expected response (HTTP 200):

```json
{"status":"UP", "groups":["liveness","readiness"]}
```
