package com.esprit.awd.gamification;

import com.esprit.awd.gamification.dto.AddPointsRequest;
import com.esprit.awd.gamification.dto.StudentProfileResponse;
import com.esprit.awd.gamification.service.GamificationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

// Profil "h2" : les tests n'ont pas besoin de PostgreSQL.
@SpringBootTest
@ActiveProfiles("h2")
class GamificationServiceApplicationTests {

    @Autowired
    private GamificationService service;

    @Test
    void contextLoads() {
    }

    @Test
    void addPointsUpdatesLevelAndAwardsBadges() {
        StudentProfileResponse profile = service.addPoints(new AddPointsRequest(42L, 120, "TEST", null));

        assertEquals(120, profile.totalPoints());
        assertNotNull(profile.level());
        assertEquals("Apprentice", profile.level().getName());
        // First Steps (10), Explorer (50), Champion (100)
        assertEquals(3, profile.badges().size());
    }

    @Test
    void sameEventIdIsAppliedOnlyOnce() {
        service.addPoints(new AddPointsRequest(43L, 30, "COURSE_COMPLETED", "evt-1"));
        StudentProfileResponse profile = service.addPoints(new AddPointsRequest(43L, 30, "COURSE_COMPLETED", "evt-1"));

        assertEquals(30, profile.totalPoints());
    }
}
