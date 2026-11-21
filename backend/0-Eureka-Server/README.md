# Eureka Server (serveur de decouverte)

Annuaire des microservices de la plateforme : chaque service s'y enregistre au demarrage sous son nom logique
(`spring.application.name`), et les autres services le retrouvent par ce nom, sans adresse IP ecrite en dur.

> Eureka est un **annuaire, pas un proxy** : aucune requete metier ne passe par lui.

Port : `8761` (reserve a Eureka).

## Liens utiles (serveur lance en local)

| Quoi | Lien |
|------|------|
| **Dashboard Eureka** (liste des services enregistres et leur statut) | http://localhost:8761 |
| Registre au format XML (API REST d'Eureka) | http://localhost:8761/eureka/apps |
| Sante du serveur (Actuator) | http://localhost:8761/actuator/health |

Dans le dashboard, la zone **Instances currently registered with Eureka** liste un nom par service
(ex. `GAMIFICATION-SERVICE`) avec son statut : `UP`, `DOWN`, `STARTING` ou `OUT_OF_SERVICE`.
Le dashboard ne se met pas a jour tout seul : **recharger la page** apres le demarrage d'un service
(compter environ 30 secondes).

## Comment ce serveur a ete cree

1. Generation du projet sur https://start.spring.io :
   Maven, Java, Spring Boot 4.1.3, Java 17, packaging Jar, configuration Properties,
   Group `com.esprit.awd`, Artifact `eureka-server`, package `com.esprit.awd.eureka`,
   dependance **Eureka Server** (`spring-cloud-starter-netflix-eureka-server`).
   La version de Spring Cloud est definie automatiquement dans le `pom.xml`.
2. Ajout de l'annotation `@EnableEurekaServer` sur la classe principale :

```java
@SpringBootApplication
@EnableEurekaServer
public class EurekaServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(EurekaServerApplication.class, args);
    }
}
```

3. Configuration de `src/main/resources/application.properties` :

```properties
spring.application.name=eureka
server.port=8761
eureka.client.register-with-eureka=false
eureka.client.fetch-registry=false
```

- `server.port=8761` : port par defaut d'Eureka.
- `eureka.client.register-with-eureka=false` : le serveur ne s'enregistre pas lui-meme comme client.
- `eureka.client.fetch-registry=false` : le serveur ne met pas le registre en cache local, puisque c'est lui qui le tient.

4. Lancement : la page http://localhost:8761 s'affiche avec "No instances available"
   tant qu'aucun microservice n'est enregistre.

## Lancer le serveur

Depuis ce dossier :

```bash
./mvnw spring-boot:run        # Linux / Mac
mvnw.cmd spring-boot:run      # Windows
```

Ou, dans IntelliJ, ouvrir ce dossier **dans sa propre fenetre** et lancer `EurekaServerApplication`.
Le serveur doit etre lance **avant** les microservices, et rester ouvert pendant les tests.

## Brancher un microservice Spring Boot (pour l'equipe)

Pour chaque service, 3 modifications (exemple deja fait : `backend/5-Gamification Engine`).

### 1. `pom.xml`

Dans `<properties>` (a l'interieur du bloc, pas en dehors) :

```xml
<spring-cloud.version>2025.1.3</spring-cloud.version>
```

Dans `<dependencies>` :

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

Apres `</dependencies>` :

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-dependencies</artifactId>
            <version>${spring-cloud.version}</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

`2025.1.3` correspond a Spring Boot 4.0.x / 4.1.x (ne pas utiliser `2025.0.0`, qui est pour Spring Boot 3.5).
Puis recharger Maven.

### 2. Configuration

Si le service utilise `application.properties` :

```properties
# Eureka
eureka.client.service-url.defaultZone=http://localhost:8761/eureka
eureka.client.register-with-eureka=true
eureka.client.fetch-registry=true
eureka.instance.prefer-ip-address=true
eureka.client.healthcheck.enabled=true

# Actuator
management.endpoints.web.exposure.include=health,info,metrics
management.endpoint.health.show-details=always
management.info.env.enabled=true
```

Si le service utilise `application.yml` (blocs `eureka:` et `management:` au **premier niveau**, pas sous `spring:`) :

```yaml
eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka
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
        include: health,info,metrics
  endpoint:
    health:
      show-details: always
  info:
    env:
      enabled: true
```

Chaque service garde son propre `spring.application.name` et son propre `server.port` (le port 8761 est reserve a Eureka).

### 3. Verifier

1. Lancer le serveur Eureka, puis le service.
2. Attendre environ 30 secondes et **recharger** http://localhost:8761 : le service apparait avec le statut **UP**.
3. Ouvrir `http://localhost:<port du service>/actuator/health` : `"status":"UP"`.

Pour tester la montee en charge, lancer une 2e instance du meme service sur un autre port
(IntelliJ : option VM `-Dserver.port=<autre port>`) : le dashboard affiche 2 instances.

## Depannage

| Symptome | Cause probable | Correction |
|----------|----------------|------------|
| Le service n'apparait pas dans le dashboard | Eureka n'est pas lance, ou mauvaise URL | Lancer Eureka d'abord ; verifier `eureka.client.service-url.defaultZone` ; attendre 30 s et recharger la page |
| Erreur Maven sur `${spring-cloud.version}` | La propriete est en dehors du bloc `<properties>` | La deplacer a l'interieur de `<properties>` |
| `Port already in use` | Un autre programme utilise le port | Changer `server.port` |
| `/actuator/info` renvoie 404 | L'endpoint n'est pas expose | `management.endpoints.web.exposure.include=health,info,metrics` |
| Le service reste `UP` alors qu'un composant est en panne | Le statut Actuator n'est pas transmis a Eureka | `eureka.client.healthcheck.enabled=true` |

## Equipe

Membres : Snoussi Yessmine 
          Ghrib Meryem 
          Ben Charrada Nedra
          Souidi Sarra
          Rebai Youssef
          Ben Yahia Rayen
