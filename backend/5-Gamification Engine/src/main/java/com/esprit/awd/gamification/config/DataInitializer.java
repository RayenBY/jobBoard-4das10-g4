package com.esprit.awd.gamification.config;

import com.esprit.awd.gamification.model.Avatar;
import com.esprit.awd.gamification.model.Badge;
import com.esprit.awd.gamification.model.Level;
import com.esprit.awd.gamification.repository.AvatarRepository;
import com.esprit.awd.gamification.repository.BadgeRepository;
import com.esprit.awd.gamification.repository.LevelRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Insere les niveaux, badges et avatars par defaut au demarrage, uniquement si les tables sont vides. */
@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner seedReferenceData(LevelRepository levels, BadgeRepository badges, AvatarRepository avatars) {
        return args -> {
            if (levels.count() == 0) {
                levels.save(new Level("Beginner", 0));
                levels.save(new Level("Apprentice", 100));
                levels.save(new Level("Expert", 300));
                levels.save(new Level("Master", 600));
            }
            if (badges.count() == 0) {
                badges.save(new Badge("First Steps", "Atteindre 10 points", "POINTS>=10"));
                badges.save(new Badge("Explorer", "Atteindre 50 points", "POINTS>=50"));
                badges.save(new Badge("Champion", "Atteindre 100 points", "POINTS>=100"));
                badges.save(new Badge("Legend", "Atteindre 500 points", "POINTS>=500"));
            }
            if (avatars.count() == 0) {
                avatars.save(new Avatar("Default", "/avatars/default.png", 0));
                avatars.save(new Avatar("Rocket", "/avatars/rocket.png", 100));
                avatars.save(new Avatar("Crown", "/avatars/crown.png", 300));
            }
        };
    }
}
