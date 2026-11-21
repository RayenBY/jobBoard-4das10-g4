package com.esprit.awd.gamification.repository;

import com.esprit.awd.gamification.model.Avatar;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AvatarRepository extends JpaRepository<Avatar, Long> {
}
