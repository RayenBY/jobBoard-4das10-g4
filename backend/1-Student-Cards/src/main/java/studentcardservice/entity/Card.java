package studentcardservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "card")
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    // Keycloak id of the person (student or teacher): one card per person
    @Column(nullable = false, unique = true, length = 64)
    private String userId;

    @Column(nullable = false, unique = true, length = 32)
    private String cardNumber;

    // Copies of the profile data, refreshed when the profile changes
    @Column(nullable = false)
    private String fullName;

    @Column(length = 500)
    private String photoUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CardStatus status;

    private LocalDate expiresAt;

    // Identity of the card in Apple Wallet (created automatically)
    @Column(nullable = false, unique = true, length = 36, updatable = false)
    @Setter(AccessLevel.NONE)
    private String serialNumber;

    // Secret the iPhone sends to prove it may ask for this card (created automatically)
    @Column(nullable = false, length = 64, updatable = false)
    @Setter(AccessLevel.NONE)
    private String authToken;

    // Refreshed automatically each time the card changes
    @Column(nullable = false)
    @Setter(AccessLevel.NONE)
    private LocalDateTime updatedAt;

    // Many cards share one program (design + class)
    @ManyToOne(optional = false)
    @JoinColumn(name = "program_id", nullable = false)
    private CardProgram program;

    @PrePersist
    void onCreate() {
        if (status == null) {
            status = CardStatus.ACTIVE;
        }
        if (serialNumber == null) {
            serialNumber = UUID.randomUUID().toString();
        }
        if (authToken == null) {
            authToken = UUID.randomUUID().toString().replace("-", "");
        }
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}