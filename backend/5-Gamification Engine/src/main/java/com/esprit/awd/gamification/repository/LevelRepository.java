package com.esprit.awd.gamification.repository;

import com.esprit.awd.gamification.model.Level;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LevelRepository extends JpaRepository<Level, Long> {

    /** Le niveau le plus eleve dont le seuil est atteint avec ce nombre de points. */
    Optional<Level> findFirstByRequiredPointsLessThanEqualOrderByRequiredPointsDesc(int points);
}
