package com.esprit.awd.gamification.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "student_gamification")
public class StudentGamification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Logical reference to the student in the User Service (not a foreign key).
    @Column(nullable = false, unique = true)
    private Long studentId;

    private int totalPoints;

    // Refers to Level.id
    private Long currentLevelId;

    // Refers to Avatar.id (0..1: the student may not have chosen one yet)
    private Long selectedAvatarId;

    public StudentGamification() {
    }

    public StudentGamification(Long studentId) {
        this.studentId = studentId;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }

    public int getTotalPoints() { return totalPoints; }
    public void setTotalPoints(int totalPoints) { this.totalPoints = totalPoints; }

    public Long getCurrentLevelId() { return currentLevelId; }
    public void setCurrentLevelId(Long currentLevelId) { this.currentLevelId = currentLevelId; }

    public Long getSelectedAvatarId() { return selectedAvatarId; }
    public void setSelectedAvatarId(Long selectedAvatarId) { this.selectedAvatarId = selectedAvatarId; }
}
