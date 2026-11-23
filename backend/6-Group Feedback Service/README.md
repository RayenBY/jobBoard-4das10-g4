# Group Feedback Service (MS6)

Sondages anonymes sur le fonctionnement des groupes pendant le projet, etat de sante de chaque groupe
(sain, a surveiller, en difficulte) et suivi des groupes en difficulte par l'enseignant.
Port `8086` — route Gateway prevue : `/api/feedback/**`.

Responsable : Sarra Souidi — Equipe SKILLOM

Ce service est un **client Eureka** : il s'enregistre au demarrage dans le serveur de decouverte
(voir le dossier `backend/0-Eureka-Server`) sous le nom `GROUP-FEEDBACK-SERVICE`, et expose sa sante via **Spring Boot Actuator**.

## Liens utiles (service lance en local)

| Quoi | Lien |
|------|------|
| Test du service | http://localhost:8086/hello |
| Page d'accueil | http://localhost:8086 |
| **Dashboard Eureka** (le service doit apparaitre `UP`) | http://localhost:8761 |
| **Sante du service** (Actuator) | http://localhost:8086/actuator/health |
| Informations du service (Actuator) | http://localhost:8086/actuator/info |
| Metriques (Actuator) | http://localhost:8086/actuator/metrics |
| Requetes HTTP traitees (Actuator) | http://localhost:8086/actuator/metrics/http.server.requests |
| Niveaux de logs (Actuator) | http://localhost:8086/actuator/loggers |
| 2e instance (port 8096) : test | http://localhost:8096/hello |
| 2e instance (port 8096) : sante | http://localhost:8096/actuator/health |

## Lancer en local

Ordre important : **1) le serveur Eureka, 2) le service**.

1. Lancer le serveur Eureka (voir `backend/0-Eureka-Server/README.md`), disponible sur http://localhost:8761.
2. Lancer le service, depuis le dossier `backend/0-Eureka-Server` (on utilise son Maven Wrapper) :

```bash
$env:JAVA_HOME="C:\Program Files\Java\jdk-17"
.\mvnw.cmd -f "..\6-Group Feedback Service\pom.xml" spring-boot:run
```

> Si Eureka n'est pas lance, le service demarre quand meme mais affiche des erreurs de connexion
> a `localhost:8761` dans la console, et n'apparait pas dans le dashboard.

**Eureka sur un autre PC** : definir `EUREKA_URL` avant le lancement, sans modifier aucun fichier :

```bash
$env:EUREKA_URL="http://ADRESSE-IP:8761/eureka"
```

## Endpoints

| Methode | URL | Description |
|---------|-----|-------------|
| GET | `/hello` | Test : renvoie "Hello, je suis le microservice 6 : Group Feedback" |

## Enregistrement dans Eureka (ce qui a ete fait)

Le service est un **client Eureka** : il s'enregistre sous son nom logique
(`spring.application.name=group-feedback-service`, affiche `GROUP-FEEDBACK-SERVICE` dans Eureka),
envoie un heartbeat toutes les 30 secondes, et les autres services peuvent le retrouver par ce nom,
sans adresse IP ecrite en dur.

### 1. `pom.xml`

- Propriete `spring-cloud.version` = `2023.0.3` (version de Spring Cloud compatible avec Spring Boot 3.3.x).
- Dependances : `spring-cloud-starter-netflix-eureka-client` et `spring-boot-starter-actuator`.
- Bloc `dependencyManagement` important le BOM `spring-cloud-dependencies`.

### 2. Classe principale

Annotation `@EnableDiscoveryClient` sur `GroupFeedbackServiceApplication` : elle rend explicite le role de client Eureka.

### 3. `application.properties`

```properties
# Eureka (adresse configurable par la variable EUREKA_URL, localhost par defaut)
eureka.client.service-url.defaultZone=${EUREKA_URL:http://localhost:8761/eureka}
eureka.client.register-with-eureka=true
eureka.client.fetch-registry=true
eureka.instance.prefer-ip-address=true
eureka.client.healthcheck.enabled=true

# Actuator
management.endpoints.web.exposure.include=health,info,metrics,loggers
management.endpoint.health.show-details=always
management.info.env.enabled=true

# Informations publiees dans /actuator/info
info.app.name=Group Feedback
info.app.version=1.0.0
info.app.description=Microservice de sondages anonymes et de suivi des groupes
info.app.team=Equipe SKILLOM
```

- `eureka.client.healthcheck.enabled=true` : Eureka recoit le statut reel calcule par `/actuator/health`.
- `management.endpoint.health.show-details=always` : `/actuator/health` affiche le detail des composants.
- `management.info.env.enabled=true` : publie les proprietes `info.*` dans `/actuator/info`.

### 4. Lancer une 2e instance (montee en charge)

Depuis `backend/0-Eureka-Server` :

```bash
.\mvnw.cmd -f "..\6-Group Feedback Service\pom.xml" spring-boot:run "-Dspring-boot.run.arguments=--server.port=8096"
```

Resultat attendu dans le dashboard : `GROUP-FEEDBACK-SERVICE` avec **2 instances** (ports 8086 et 8096), toutes deux `UP`.

### 5. Verification

1. Lancer Eureka puis le service, attendre environ 30 secondes, **recharger** http://localhost:8761
   (la page ne se rafraichit pas toute seule) : `GROUP-FEEDBACK-SERVICE` apparait avec le statut **UP**.
2. Ouvrir http://localhost:8086/actuator/health : `"status":"UP"`.
3. Ouvrir http://localhost:8086/actuator/info : nom, version, description et equipe du service.

![Dashboard Eureka](docs/eureka-dashboard.png)

## Observabilite avec Actuator

- **HealthIndicator personnalise** : la classe `FeedbackHealthIndicator` ajoute un composant `feedback`
  dans `/actuator/health`. Plus tard, elle pourra verifier la base `feedback_db` et renvoyer `DOWN`.
- **`/actuator/info`** renvoie :

```json
{"app":{"name":"Group Feedback","version":"1.0.0",
"description":"Microservice de sondages anonymes et de suivi des groupes","team":"Equipe SKILLOM"}}
```

- **`/actuator/metrics/http.server.requests`** : apres plusieurs appels a `/hello`, la mesure `COUNT`
  donne le nombre de requetes traitees et `MAX` la duree de la plus lente.
  `?tag=status:200` ne compte que les requetes reussies.

## Reste a faire

- Sondages, reponses anonymes, etat de sante des groupes et suivis (base MySQL `feedback_db`).
- Appels REST vers MS3 Course/Group et MS2 Profiling (OpenFeign).
- RabbitMQ : consommer `session.scheduled` (MS7) et `account.deleted` (MS1),
  publier `feedback.survey.completed` (MS5) et `feedback.group.health` (MS4).