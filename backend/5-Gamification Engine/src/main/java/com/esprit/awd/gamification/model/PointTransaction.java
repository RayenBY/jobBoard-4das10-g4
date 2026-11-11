package com.esprit.awd.gamification.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "point_transaction")
public class PointTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Logical reference to the student in the User Service (not a foreign key).
    @Column(nullable = false)
    private Long studentId;

    private int points;

    private String reason;

    private String eventId;

    @Column(nullable = false)
    private Instant createdAt;
}
