package com.esprit.awd.gamification.repository;

import com.esprit.awd.gamification.model.Badge;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BadgeRepository extends JpaRepository<Badge, Long> {
}
