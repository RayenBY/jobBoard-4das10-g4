# MS2 · user-service (User / Profiling)

Empty Spring Boot skeleton for the user / profiling microservice. Put this folder in `backend/2-User-Profiling/`.

| | |
|---|---|
| Eureka name | `USER-SERVICE` (`spring.application.name: user-service`) |
| Gateway routes | `/api/users/**`, `/api/profiles/**` |
| Database | MySQL `user_db` |
| Default port | `8082` (`SERVER_PORT`) |
| Stack | Java 17+, Spring Boot 4.1.1, Spring Cloud 2025.1.3, Spring Data JPA, Eureka client, Config client, OpenFeign |

All services of the project must use the same Spring Cloud release train (2025.1.x with Spring Boot 4.x).

## Run

You need JDK 17+, Maven and a MySQL server (the `user_db` database is created if it does not exist).

```bash
mvn spring-boot:run
```

Without Eureka or the Config Server the service still starts (the Config import is `optional:`), it only logs connection warnings.
Check: http://localhost:8082/actuator/health

### Environment variables

| Variable | Default |
|---|---|
| `SERVER_PORT` | `8082` |
| `DB_HOST` / `DB_PORT` / `DB_NAME` | `localhost` / `3306` / `user_db` |
| `DB_USER` / `DB_PASSWORD` | `root` / *(empty)* |
| `EUREKA_URI` | `http://localhost:8761/eureka` |
| `CONFIG_SERVER_URL` | `http://localhost:8888` |

In Docker Compose, set `DB_HOST`, `EUREKA_URI` and `CONFIG_SERVER_URL` to the service names of the compose network.

## Docker

```bash
docker build -t user-service .
```

## Structure

```
src/main/java/tn/esprit/jobboard/userservice/
├── UserServiceApplication.java
├── config/       controller/   dto/   entity/   repository/
├── service/      client/       event/ exception/
```

Each package only contains a `package-info.java` describing what goes inside (from the class diagram).

## Not included on purpose

- **RabbitMQ**: MS2 has no event in the team's design yet. If `ProfileCreated` is agreed, add `spring-boot-starter-amqp`.
- **Entities / endpoints / Feign client**: to write from the class diagram.
- **Tests**: a default `@SpringBootTest` would fail without MySQL.
