# ML Clustering Engine (MS4)

Spring Boot microservice that builds balanced student groups by clustering student profiles
(K-Means / K-Prototypes). Database: **MongoDB** (`clustering_db`) · Frontend: **Streamlit** · Port: **8084**

## Stack
Java 17 · Spring Boot 3.3.5 · Maven · Spring Data MongoDB
(later: Python ML engine, Eureka, Config Server, OpenFeign, RabbitMQ, Keycloak)

## Run
```bash
mvn spring-boot:run
```
The service starts even without MongoDB (lazy connection).

## Run with MongoDB
```bash
docker compose up -d            # MongoDB on localhost:27017
mvn spring-boot:run
```

## Check
- http://localhost:8084/api/clustering/hello
- http://localhost:8084/actuator/health

## Tests
```bash
mvn test
```
