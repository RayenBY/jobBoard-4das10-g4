package com.esprit.awd.gamification.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "student_badge")
public class StudentBadge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Logical reference to the student in the User Service (not a foreign key).
    @Column(nullable = false)
    private Long studentId;

    // Refers to Badge.id
    @Column(nullable = false)
    private Long badgeId;

    @Column(nullable = false)
    private Instant earnedAt;

    public StudentBadge() {
    }

    public StudentBadge(Long studentId, Long badgeId) {
        this.studentId = studentId;
        this.badgeId = badgeId;
        this.earnedAt = Instant.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }

    public Long getBadgeId() { return badgeId; }
    public void setBadgeId(Long badgeId) { this.badgeId = badgeId; }

    public Instant getEarnedAt() { return earnedAt; }
    public void setEarnedAt(Instant earnedAt) { this.earnedAt = earnedAt; }
}
