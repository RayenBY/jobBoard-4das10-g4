package com.esprit.awd.gamification.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "badge")
public class Badge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    /**
     * Condition d'obtention du badge. Format supporte : "POINTS>=50"
     * (le badge est attribue des que le total de points atteint 50).
     * Colonne renommee "badge_condition" car CONDITION est un mot reserve SQL (MySQL notamment).
     */
    @Column(name = "badge_condition")
    private String condition;

    public Badge() {
    }

    public Badge(String name, String description, String condition) {
        this.name = name;
        this.description = description;
        this.condition = condition;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCondition() { return condition; }
    public void setCondition(String condition) { this.condition = condition; }
}
