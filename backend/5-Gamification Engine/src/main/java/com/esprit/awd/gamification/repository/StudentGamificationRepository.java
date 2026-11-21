package com.esprit.awd.gamification.repository;

import com.esprit.awd.gamification.model.StudentGamification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudentGamificationRepository extends JpaRepository<StudentGamification, Long> {

    Optional<StudentGamification> findByStudentId(Long studentId);

    List<StudentGamification> findTop10ByOrderByTotalPointsDesc();
}
