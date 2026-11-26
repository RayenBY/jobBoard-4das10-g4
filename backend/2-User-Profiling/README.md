# MS2 · User / Profiling Service (user-service)

Gestion des profils des apprenants : informations de profil, intérêts, compétences et style d'apprentissage.
Port `8082`. Routes Gateway prévues : `/api/users/**` et `/api/profiles/**`.

Ce service est un **client Eureka** : il s'enregistre au démarrage dans le serveur de découverte
(voir le dossier `backend/0-Eureka-Server`) sous le nom `USER-SERVICE`, et expose sa santé via **Spring Boot Actuator**.

| | |
|---|---|
| Nom Eureka | `USER-SERVICE` (`spring.application.name: user-service`) |
| Routes Gateway | `/api/users/**`, `/api/profiles/**` |
| Base de données | MySQL `user_db` (créée automatiquement si elle n'existe pas) |
| Port | `8082` (2e instance : `8092`) |
| Stack | Java 17+, Spring Boot 4.1.1, Spring Cloud 2025.1.3, Spring Data JPA, Eureka client, Config client, OpenFeign, springdoc (Swagger) |

## Liens utiles (service lancé en local)

| Quoi | Lien |
|------|------|
| Test du service | http://localhost:8082/api/users/hello |
| **Swagger UI** | http://localhost:8082/swagger-ui/index.html |
| **Dashboard Eureka** (le service doit apparaître `UP`) | http://localhost:8761 |
| **Santé du service** (Actuator) | http://localhost:8082/actuator/health |
| Informations du service (Actuator) | http://localhost:8082/actuator/info |
| Métriques (Actuator) | http://localhost:8082/actuator/metrics |
| Requêtes HTTP traitées (Actuator) | http://localhost:8082/actuator/metrics/http.server.requests |
| Niveaux de logs (Actuator) | http://localhost:8082/actuator/loggers |
| 2e instance (port 8092) : test | http://localhost:8092/api/users/hello |
| 2e instance (port 8092) : santé | http://localhost:8092/actuator/health |

## Lancer en local

Ordre important : **1) MySQL, 2) le serveur Eureka, 3) le service**.

1. Démarrer MySQL (sans MySQL, le service ne démarre pas).
2. Lancer le serveur Eureka (voir `backend/0-Eureka-Server/README.md`), disponible sur http://localhost:8761.
3. Lancer le service, soit depuis IntelliJ (bouton ▶ sur `UserServiceApplication`), soit depuis le dossier
   `backend/0-Eureka-Server` (on utilise son Maven Wrapper) :

```bash
.\mvnw.cmd -f "..\2-User-Profiling\pom.xml" spring-boot:run
```

> Si Eureka n'est pas lancé, le service démarre quand même mais affiche des erreurs de connexion
> à `localhost:8761` dans la console, et n'apparaît pas dans le dashboard.
> Même chose pour le Config Server (import `optional:`).

**Eureka sur un autre PC** : définir `EUREKA_URL` avant le lancement, sans modifier aucun fichier :

```bash
$env:EUREKA_URL="http://ADRESSE-IP:8761/eureka"
```

### Variables d'environnement

| Variable | Valeur par défaut |
|---|---|
| `SERVER_PORT` | `8082` |
| `DB_HOST` / `DB_PORT` / `DB_NAME` | `localhost` / `3306` / `user_db` |
| `DB_USER` / `DB_PASSWORD` | `root` / *(vide)* |
| `EUREKA_URL` | `http://localhost:8761/eureka` |
| `CONFIG_SERVER_URL` | `http://localhost:8888` |

## Endpoints

| Méthode | URL | Description |
|---------|-----|-------------|
| GET | `/api/users/hello` | Test : renvoie `hello microservice` |

## Enregistrement dans Eureka (ce qui a été fait)

Le service est un **client Eureka** : il s'enregistre sous son nom logique
(`spring.application.name=user-service`, affiché `USER-SERVICE` dans Eureka),
envoie un heartbeat toutes les 30 secondes, et les autres services (MS3, MS4…) peuvent le retrouver
par ce nom (`lb://USER-SERVICE`), sans adresse IP écrite en dur.

### 1. `pom.xml`

- Propriété `spring-cloud.version` = `2025.1.3` (version de Spring Cloud compatible avec Spring Boot 4.1.x).
- Dépendances : `spring-cloud-starter-netflix-eureka-client` et `spring-boot-starter-actuator`.
- Bloc `dependencyManagement` important le BOM `spring-cloud-dependencies`.

### 2. Classe principale

Annotations `@EnableDiscoveryClient` (rôle de client Eureka) et `@EnableFeignClients` (appels OpenFeign vers MS1)
sur `UserServiceApplication`.

### 3. `application.yml`

```yaml
eureka:
  client:
    service-url:
      defaultZone: ${EUREKA_URL:http://localhost:8761/eureka}
    register-with-eureka: true
    fetch-registry: true
    healthcheck:
      enabled: true
  instance:
    prefer-ip-address: true

management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,loggers
  endpoint:
    health:
      show-details: always
  info:
    env:
      enabled: true

info:
  app:
    name: User / Profiling
    version: 1.0.0
    description: Microservice de gestion des profils, interets, competences et styles d'apprentissage
    team: The Skill Weavers
```

- `fetch-registry: true` : le service récupère la liste des services, nécessaire pour appeler `AUTH-SERVICE` avec OpenFeign.
- `healthcheck.enabled: true` : Eureka reçoit le statut réel calculé par `/actuator/health`
  (si MySQL tombe, le service passe `DOWN` dans Eureka et n'est plus proposé aux autres services).
- `show-details: always` : `/actuator/health` affiche le détail des composants (`db`, `diskSpace`, `profile`…).
- `info.env.enabled: true` : publie les propriétés `info.*` dans `/actuator/info`.

### 4. Lancer une 2e instance (montée en charge)

Depuis `backend/0-Eureka-Server` :

```bash
.\mvnw.cmd -f "..\2-User-Profiling\pom.xml" spring-boot:run "-Dspring-boot.run.arguments=--server.port=8092"
```

Ou dans IntelliJ : *Run → Edit Configurations* → dupliquer `UserServiceApplication`, puis dans
*Modify options → Program arguments* mettre `--server.port=8092`.

Résultat attendu dans le dashboard : `USER-SERVICE` avec **2 instances** (ports 8082 et 8092), toutes deux `UP`.

### 5. Vérification

1. Lancer Eureka puis le service, attendre environ 30 secondes, **recharger** http://localhost:8761
   (la page ne se rafraîchit pas toute seule) : `USER-SERVICE` apparaît avec le statut **UP**.
2. Ouvrir http://localhost:8082/actuator/health : `"status":"UP"`.
3. Ouvrir http://localhost:8082/actuator/info : nom, version, description et équipe du service.

## Observabilité avec Actuator

- **HealthIndicator personnalisé** : la classe `config/ProfileHealthIndicator` ajoute un composant `profile`
  dans `/actuator/health`. Avec Spring Boot 4, les classes `Health` et `HealthIndicator` sont dans le package
  `org.springframework.boot.health.contributor` (et non plus `org.springframework.boot.actuate.health`).
- **`/actuator/info`** renvoie :

```json
{"app":{"name":"User / Profiling","version":"1.0.0",
"description":"Microservice de gestion des profils, interets, competences et styles d'apprentissage","team":"The Skill Weavers"}}
```

- **`/actuator/metrics/http.server.requests`** : après plusieurs appels à `/api/users/hello`, la mesure `COUNT`
  donne le nombre de requêtes traitées et `MAX` la durée de la plus lente.
  `?tag=status:200` ne compte que les requêtes réussies.

## Docker

```bash
docker build -t user-service .
```

## Structure

```
src/main/java/tn/esprit/jobboard/userservice/
├── UserServiceApplication.java
├── config/       ProfileHealthIndicator
├── controller/   HelloController
├── dto/   entity/   repository/   service/   client/   event/   exception/
```

Les packages vides contiennent un `package-info.java` qui décrit ce qui y sera ajouté (d'après le diagramme de classes).

## Reste à faire

- Entités `UserProfile`, `Interest`, `Skill`, `ProfileSkill`, enum `LearningStyle` (MySQL `user_db`).
- Endpoints `/api/profiles` (création, consultation, mise à jour, liste par `ids`).
- Client OpenFeign vers MS1 : `GET /api/auth/users/{id}` (MS1 doit d'abord s'enregistrer dans Eureka).
- Pas de RabbitMQ : MS2 ne publie ni ne consomme aucun événement dans la conception de l'équipe.
