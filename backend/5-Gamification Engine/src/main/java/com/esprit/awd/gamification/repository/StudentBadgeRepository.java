package com.esprit.awd.gamification.repository;

import com.esprit.awd.gamification.model.StudentBadge;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudentBadgeRepository extends JpaRepository<StudentBadge, Long> {

    List<StudentBadge> findByStudentId(Long studentId);

    boolean existsByStudentIdAndBadgeId(Long studentId, Long badgeId);
}
