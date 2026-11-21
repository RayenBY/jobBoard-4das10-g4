package com.esprit.awd.gamification.repository;

import com.esprit.awd.gamification.model.PointTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PointTransactionRepository extends JpaRepository<PointTransaction, Long> {

    List<PointTransaction> findByStudentIdOrderByCreatedAtDesc(Long studentId);

    boolean existsByEventId(String eventId);
}
