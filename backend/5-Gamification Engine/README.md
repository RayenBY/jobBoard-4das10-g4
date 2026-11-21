# Gamification Service (MS5)

Points, niveaux, badges et avatars des etudiants. Route Gateway : `/api/gamification/**` — port `8085`.

Swagger : http://localhost:8085/swagger-ui/index.html

## Lancer en local

```bash
./mvnw spring-boot:run        # Linux / Mac
mvnw.cmd spring-boot:run      # Windows
```

Le service demarre sur http://localhost:8085 avec une base **H2 en memoire** (rien a installer).
Les donnees disparaissent a l'arret ; les niveaux, badges et avatars par defaut sont recrees a chaque demarrage.

## Endpoints

| Methode | URL | Description |
|---------|-----|-------------|
| GET | `/api/gamification/hello` | Test : renvoie "Hello gamification service" |
| POST | `/api/gamification/points` | Ajouter des points `{studentId, points, reason, eventId}` |
| GET | `/api/gamification/students/{studentId}` | Points, niveau, badges, avatar |
| GET | `/api/gamification/students/{studentId}/transactions` | Historique des points |
| PUT | `/api/gamification/students/{studentId}/avatar/{avatarId}` | Choisir un avatar (si assez de points) |
| GET | `/api/gamification/leaderboard` | Top 10 |
| GET | `/api/gamification/levels` · `/badges` · `/avatars` | Donnees de reference |

```bash
curl -X POST http://localhost:8085/api/gamification/points \
  -H "Content-Type: application/json" \
  -d '{"studentId": 1, "points": 120, "reason": "COURSE_COMPLETED", "eventId": "evt-1"}'

curl http://localhost:8085/api/gamification/students/1
```

## Regles

- Niveau = le plus haut `Level` dont `requiredPoints` <= total de points.
- Badge : condition au format `POINTS>=N`, attribue automatiquement.
- `eventId` : un meme evenement n'est applique qu'une seule fois (utile pour RabbitMQ).

## Reste a faire

- Listener RabbitMQ pour l'evenement `CourseCompleted` (queue `gamification.queue`, cle `course.completed`) :
  il devra appeler `GamificationService.addPoints(...)` avec l'`eventId` de l'evenement.
