# Scheduling Service (MS7)

Spring Boot microservice responsible for meetings / sessions, rooms, availabilities,
participants, reminders and deadlines. Database: MySQL (`scheduling_db`). Frontend: Nuxt.js.

## Run
```bash
mvn spring-boot:run
```
Then open: http://localhost:8087/api/scheduling/hello  
Health check: http://localhost:8087/actuator/health

## Stack
Java 17 · Spring Boot 3.3.5 · Maven · (later) MySQL, Eureka, Config Server, OpenFeign, RabbitMQ, Keycloak
