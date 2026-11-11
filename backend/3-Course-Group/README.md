# MS3 — Course / Group Service

Projet de démarrage Spring Boot pour le microservice MS3.

## Technologies
- Java 17
- Spring Boot 3.4.5
- Spring Web, Spring Data JPA, MySQL Driver

## Installation
1. Installer Java 17 et démarrer MySQL.
2. Vérifier/adapter les identifiants dans `src/main/resources/application.properties`.
3. Ouvrir le dossier `3-Course-Group` dans IntelliJ IDEA comme projet Maven.
4. Recharger Maven puis lancer `Ms3CourseGroupApplication`.

## Endpoints actuellement disponibles
- POST `/api/courses`
- GET `/api/courses`
- GET `/api/courses/{id}`
- PUT `/api/courses/{id}`
- DELETE `/api/courses/{id}`

Les entités et repositories de StudyGroup, GroupMember et Enrollment sont initialisés.
Les autres endpoints, les appels MS2/MS4, les événements MS5 et la sécurité restent à développer.

Ne jamais publier un vrai mot de passe MySQL dans Git. Remplacer `CHANGE_ME` localement.
