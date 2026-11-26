package studentcardservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "card_program")
public class CardProgram {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ProgramType type;

    // Class information: filled for STUDENT programs, empty (null) for the TEACHER program
    private String className;
    private String level;
    private String academicYear;


    // Design of the card
    private String backgroundColor;
    private String foregroundColor;
    private String logoUrl;
    private String heroImageUrl;



}