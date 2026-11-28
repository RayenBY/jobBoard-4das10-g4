# API Gateway

Point d'entrée unique des microservices du projet (Spring Cloud Gateway, WebFlux).
Les services sont découverts via **Eureka** et appelés par leur nom logique (`lb://nom-du-service`).

| Élément | Valeur |
|---|---|
| Port | `8080` (modifiable via `server.port`) |
| Nom Eureka | `gateway` |
| Portail Swagger unifié | http://localhost:8080/swagger-ui.html |
| Eureka | http://localhost:8761 |

## Lancer

Ordre : **Eureka Server** → microservices → **Gateway**.

```bash
./mvnw spring-boot:run        # Windows : mvnw.cmd spring-boot:run
```

> Si le port 8080 est déjà occupé sur votre machine (ex. Oracle), changez `server.port` dans `application.properties`.

## Routes actuelles

| Id | Chemin | Service (Eureka) |
|---|---|---|
| `GAMIFICATION` | `/api/gamification/**` | `gamification-service` |
| `GAMIFICATION-DOCS` | `/gamification/v3/api-docs` | `gamification-service` (doc OpenAPI) |

## Ajouter la route de votre service

Dans `src/main/resources/application.properties`, ajoutez **à la suite** (prenez l'index libre suivant, ne modifiez pas les routes des autres) :

```properties
# Votre service
spring.cloud.gateway.server.webflux.routes[N].id=MON-SERVICE
spring.cloud.gateway.server.webflux.routes[N].uri=lb://nom-eureka-de-votre-service
spring.cloud.gateway.server.webflux.routes[N].predicates[0]=Path=/api/mon-service/**

# Sa documentation OpenAPI
spring.cloud.gateway.server.webflux.routes[N+1].id=MON-SERVICE-DOCS
spring.cloud.gateway.server.webflux.routes[N+1].uri=lb://nom-eureka-de-votre-service
spring.cloud.gateway.server.webflux.routes[N+1].predicates[0]=Path=/mon-service/v3/api-docs
spring.cloud.gateway.server.webflux.routes[N+1].filters[0]=RewritePath=/mon-service/v3/api-docs, /v3/api-docs
spring.cloud.gateway.server.webflux.routes[N+1].filters[1]=PreserveHostHeader

# Entrée dans le portail Swagger
springdoc.swagger-ui.urls[M].name=Mon service
springdoc.swagger-ui.urls[M].url=/mon-service/v3/api-docs
```

> Avec Spring Cloud 2025.x, le préfixe des propriétés est `spring.cloud.gateway.server.webflux.` (et non `spring.cloud.gateway.`).

## Load balancing

- Par défaut : **Round Robin** (alternance stricte entre instances).
- `gamification-service` utilise **Random** (`RandomLoadBalancerConfig` + `@LoadBalancerClient` dans `GatewayApplication`).

## Tests

Collection Postman : `docs/JobBoard-ApiGateway.postman_collection.json` (toutes les requêtes passent par `http://localhost:8080`).
