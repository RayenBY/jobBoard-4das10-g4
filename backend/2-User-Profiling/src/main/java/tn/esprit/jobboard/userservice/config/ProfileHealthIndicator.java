package tn.esprit.jobboard.userservice.config;

import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.stereotype.Component;

/**
 * Custom health component, shown as "profile" in /actuator/health.
 * Spring Boot 4: Health and HealthIndicator moved to org.springframework.boot.health.contributor
 * (in Boot 3 they were in org.springframework.boot.actuate.health).
 * The MySQL check is already done by Spring ("db" component); this one describes the service.
 */
@Component
public class ProfileHealthIndicator implements HealthIndicator {

    @Override
    public Health health() {
        return Health.up()
                .withDetail("service", "User / Profiling")
                .withDetail("message", "Service de profils operationnel")
                .build();
    }
}
