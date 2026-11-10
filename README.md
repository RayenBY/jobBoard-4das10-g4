# Plateforme d’apprentissage basée sur des microservices

Une plateforme d’apprentissage construite autour de **7 microservices Spring Boot / Python**, derrière une **API Gateway Spring Cloud**, avec un système de découverte des services (Eureka), une configuration centralisée (Spring Cloud Config), une authentification via **Keycloak** et une communication asynchrone à travers **RabbitMQ**. L’ensemble de l’infrastructure est exécuté avec **Docker Compose**.

> Chaque microservice possède sa propre base de données (*un bounded context = un microservice = une base de données*). Les services ne partagent jamais leurs tables : ils se référencent uniquement par **identifiant (ID)**, en passant par l’API du service propriétaire (OpenFeign) ou par un événement (RabbitMQ).

---

## Table des matières

- [Architecture](#architecture)
- [Microservices](#microservices)
- [Infrastructure](#infrastructure)
- [Événements asynchrones](#evenements-asynchrones)
- [Stack technologique](#stack-technologique)
- [Démarrage du projet](#demarrage-du-projet)
- [Routes API](#routes-api)
- [Authentification](#authentification)
- [Structure du projet](#structure-du-projet)
- [Diagrammes UML](#diagrammes-uml)

---

## Architecture

```text
            ┌─────────────┐   login → JWT   ┌──────────────────┐
 Client ───▶│ Application │ ──────────────▶│ Keycloak :8180   │
 (Angular,  │ Web         │                 └───────┬──────────┘
  React...) └──────┬──────┘                         │ Clés publiques
                   │ Bearer JWT                     ▼
                   ▼
          ┌───────────────────────────────────────────────────────┐
          │       API Gateway (Spring Cloud Gateway) :8080        │
          └──────────────────────┬────────────────────────────────┘
                                 │ lb://SERVICE-NAME (Eureka)
   ┌──────────┬──────────┬───────┼───────┬──────────┬──────────┐
   ▼          ▼          ▼       ▼       ▼          ▼          ▼
  MS1        MS2        MS3     MS4     MS5        MS6        MS7
 Auth     User/Prof.  Course/    ML   Gamifi-   Analytics  Scheduling
                      Group  Clustering cation
   │          │          │       │       │          │          │
Postgres    MySQL      MySQL  MongoDB  MySQL    Postgres     MySQL

        MS3 ──CourseCompleted──▶ RabbitMQ ──▶ MS5
        MS7 ──SessionScheduled─▶ RabbitMQ ──▶ MS6

 Eureka :8761 (registre des services)
 Config Server :8888 ◀── config-repo (Git)
```

Toutes les requêtes du client passent par l'API Gateway et contiennent un **Bearer JWT** délivré par Keycloak.

---

## Microservices

| # | Service | Responsabilité | Technologie | Base de données | Route Gateway |
|---|---------|----------------|-------------|-----------------|---------------|
| MS1 | **Service d'authentification (Auth Service)** | Gestion des comptes utilisateurs, des rôles et intégration avec Keycloak | Spring Boot | PostgreSQL (`auth_db`) | `/api/auth/**` |
| MS2 | **Service Utilisateur / Profilage** | Gestion des profils, intérêts, compétences et préférences d'apprentissage | Spring Boot | MySQL (`user_db`) | `/api/users/**`, `/api/profiles/**` |
| MS3 | **Service Cours / Groupes** | Gestion des cours, inscriptions, groupes d'étude et membres | Spring Boot | MySQL (`course_db`) | `/api/courses/**`, `/api/groups/**` |
| MS4 | **Moteur de clustering ML** | Regroupement des apprenants en clusters à partir des caractéristiques de leurs profils | Spring Boot / Python | MongoDB (`clustering_db`) | `/api/ml-clustering/**` |
| MS5 | **Moteur de gamification** | Gestion des points, niveaux et badges | Spring Boot | MySQL (`gamification_db`) | `/api/gamification/**` |
| MS6 | **Service d'analyse (Analytics)** | Statistiques d'activité et indicateurs quotidiens | Spring Boot | PostgreSQL (`analytics_db`) | `/api/analytics/**` |
| MS7 | **Service de planification (Scheduling)** | Gestion des sessions, participants et disponibilités | Spring Boot | MySQL (`scheduling_db`) | `/api/scheduling/**` |

---

## Infrastructure

| Composant | Rôle | Port |
|-----------|------|------|
| **API Gateway** (Spring Cloud Gateway) | Point d'entrée unique, routage (`lb://NAME`) et validation JWT | `8080` |
| **Keycloak** | Fournisseur d'identité, utilise le realm `jobboard` et délivre les JWT | `8180` |
| **Eureka Server** | Registre des services (enregistrement et heartbeat toutes les 30 secondes) | `8761` |
| **Config Server** (Spring Cloud Config) | Gestion centralisée des configurations, chargées au démarrage de chaque service | `8888` |
| **config-repo** (GitHub) | Dépôt Git contenant les fichiers de configuration | — |
| **RabbitMQ** | Broker de messages, avec un topic exchange nommé `platform.events` | `5672` (interface Web : `15672`) |

Tous les services métier fonctionnent sur un même réseau Docker Compose : `jobboard-net`.

---

## Événements asynchrones

L'échange de messages est assuré par un topic exchange nommé **`platform.events`**.

| Événement | Émetteur (Publisher) | Routing Key | File d'attente (Queue) | Consommateur (Consumer) | Effet |
|-----------|---------------------|-------------|------------------------|--------------------------|-------|
| `CourseCompleted` | MS3 Course / Group | `course.completed` | `gamification.queue` | MS5 Gamification | Attribue des points et des badges |
| `SessionScheduled` | MS7 Scheduling | `session.scheduled` | `analytics.queue` | MS6 Analytics | Met à jour les statistiques |

Les consommateurs sont **idempotents** : chaque événement contient un `eventId` (UUID). Si un même événement est reçu plusieurs fois, il est traité une seule fois. Les messages déjà traités sont donc ignorés.

---

## Stack technologique

- **Backend :** Java, Spring Boot, Spring Cloud (Gateway, Netflix Eureka, Config, OpenFeign), Spring Data JPA / MongoDB, Spring AMQP.
- **Machine Learning :** Python (moteur de clustering).
- **Sécurité :** Keycloak, OAuth2 / JWT.
- **Messagerie :** RabbitMQ.
- **Bases de données :** PostgreSQL, MySQL, MongoDB.
- **Frontend :** application Web (Angular / React).
- **DevOps :** Docker, Docker Compose, Git / GitHub.

---

## Démarrage du projet

### Prérequis

- Docker et Docker Compose.
- JDK 17+ et Maven (pour compiler les services localement).
- Python 3.10+ (pour le moteur de clustering ML, si exécuté en dehors de Docker).
- Node.js (pour l'application Web).

### Lancer l'ensemble du projet

```bash
git clone <repository-url>
cd <repository-folder>

# Construire et démarrer toute l'infrastructure
docker compose up -d --build

# Consulter les logs
docker compose logs -f
```

### Ordre de démarrage

Docker Compose gère automatiquement cet ordre. Toutefois, si les services sont démarrés manuellement :

1. Bases de données, RabbitMQ et Keycloak.
2. Config Server (`:8888`).
3. Eureka Server (`:8761`).
4. Microservices métier (MS1 → MS7).
5. API Gateway (`:8080`).
6. Application Web.

### URLs utiles

| Composant | URL |
|-----------|-----|
| API Gateway | [http://localhost:8080](http://localhost:8080) |
| Tableau de bord Eureka | [http://localhost:8761](http://localhost:8761) |
| Config Server | [http://localhost:8888](http://localhost:8888) |
| Console d'administration Keycloak | [http://localhost:8180](http://localhost:8180) |
| Interface de gestion RabbitMQ | [http://localhost:15672](http://localhost:15672) |

### Arrêter le projet

```bash
docker compose down       # Arrêter les conteneurs
docker compose down -v    # Arrêter les conteneurs et supprimer les volumes (bases de données)
```

---

## Routes API

Toutes les requêtes passent par l'API Gateway : `http://localhost:8080`.

| Route | Microservice |
|-------|--------------|
| `/api/auth/**` | MS1 · Service d'authentification |
| `/api/users/**`, `/api/profiles/**` | MS2 · Service Utilisateur / Profilage |
| `/api/courses/**`, `/api/groups/**` | MS3 · Service Cours / Groupes |
| `/api/ml-clustering/**` | MS4 · Moteur de clustering ML |
| `/api/gamification/**` | MS5 · Moteur de gamification |
| `/api/analytics/**` | MS6 · Service d'analyse |
| `/api/scheduling/**` | MS7 · Service de planification |

---

## Authentification

1. Le client se connecte via **Keycloak** (realm `jobboard`) et reçoit un **JWT**.
2. Chaque requête envoyée à l'API Gateway contient le header `Authorization: Bearer <token>`.
3. L'API Gateway valide le token à l'aide des clés publiques de Keycloak avant de transmettre la requête au microservice concerné.

Les rôles disponibles sont :

- `STUDENT` : Étudiant.
- `TUTOR` : Tuteur.
- `ADMIN` : Administrateur.

---

## Structure du projet

```text
.
├── api-gateway/
├── eureka-server/
├── config-server/
├── auth-service/             # MS1
├── user-service/             # MS2
├── course-service/           # MS3
├── ml-clustering/            # MS4
├── gamification-service/     # MS5
├── analytics-service/        # MS6
├── scheduling-service/       # MS7
├── frontend/
├── docs/
│   ├── architecture.png
│   ├── diagramme_classes_domaine_global.drawio
│   └── diagramme_classes_par_domaine.drawio
├── docker-compose.yml
└── README.md
```

> Adaptez les noms des dossiers ci-dessus à la structure réelle de votre dépôt.

---

## Diagrammes UML

Les diagrammes sont disponibles dans le dossier [`docs/`](docs/) et peuvent être ouverts avec [draw.io](https://app.diagrams.net).

Ils comprennent :

- **Diagramme d'architecture des microservices :** présentation de l'API Gateway, des microservices, des bases de données, de RabbitMQ, d'Eureka, de Config Server et de Keycloak.
- **Diagramme de classes global du domaine :** représentation conceptuelle de l'ensemble du modèle métier de la plateforme, avec des couleurs différentes selon le microservice propriétaire.
- **Diagrammes de classes par domaine :** représentation des classes de chaque microservice, des identifiants partagés entre services (OpenFeign) et des événements échangés via RabbitMQ.
