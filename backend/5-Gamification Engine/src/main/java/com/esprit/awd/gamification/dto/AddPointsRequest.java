package com.esprit.awd.gamification.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * @param studentId id de l'etudiant (vient du User Service)
 * @param points    points a ajouter (> 0)
 * @param reason    raison, ex. "COURSE_COMPLETED"
 * @param eventId   (optionnel) id de l'evenement RabbitMQ : un meme eventId n'est applique qu'une fois
 */
public record AddPointsRequest(
        @NotNull Long studentId,
        @Min(1) int points,
        String reason,
        String eventId) {
}
