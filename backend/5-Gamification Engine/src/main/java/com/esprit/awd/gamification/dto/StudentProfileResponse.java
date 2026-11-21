package com.esprit.awd.gamification.dto;

import com.esprit.awd.gamification.model.Badge;
import com.esprit.awd.gamification.model.Level;

import java.util.List;

public record StudentProfileResponse(
        Long studentId,
        int totalPoints,
        Level level,
        List<Badge> badges,
        Long selectedAvatarId) {
}
