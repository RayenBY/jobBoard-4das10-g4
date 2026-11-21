package com.esprit.awd.gamification.controller;

import com.esprit.awd.gamification.dto.AddPointsRequest;
import com.esprit.awd.gamification.dto.LeaderboardEntry;
import com.esprit.awd.gamification.dto.StudentProfileResponse;
import com.esprit.awd.gamification.model.Avatar;
import com.esprit.awd.gamification.model.Badge;
import com.esprit.awd.gamification.model.Level;
import com.esprit.awd.gamification.model.PointTransaction;
import com.esprit.awd.gamification.repository.AvatarRepository;
import com.esprit.awd.gamification.repository.BadgeRepository;
import com.esprit.awd.gamification.repository.LevelRepository;
import com.esprit.awd.gamification.service.GamificationService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Route exposee par l'API Gateway : /api/gamification/** */
@RestController
@RequestMapping("/api/gamification")
public class GamificationController {

    private final GamificationService service;
    private final LevelRepository levelRepository;
    private final BadgeRepository badgeRepository;
    private final AvatarRepository avatarRepository;

    public GamificationController(GamificationService service,
                                  LevelRepository levelRepository,
                                  BadgeRepository badgeRepository,
                                  AvatarRepository avatarRepository) {
        this.service = service;
        this.levelRepository = levelRepository;
        this.badgeRepository = badgeRepository;
        this.avatarRepository = avatarRepository;
    }

    /** Ajouter des points a un etudiant. */
    @PostMapping("/points")
    public StudentProfileResponse addPoints(@Valid @RequestBody AddPointsRequest request) {
        return service.addPoints(request);
    }

    /** Profil de gamification : points, niveau, badges, avatar. */
    @GetMapping("/students/{studentId}")
    public StudentProfileResponse getProfile(@PathVariable Long studentId) {
        return service.getProfile(studentId);
    }

    /** Historique des points d'un etudiant. */
    @GetMapping("/students/{studentId}/transactions")
    public List<PointTransaction> getTransactions(@PathVariable Long studentId) {
        return service.getTransactions(studentId);
    }

    /** Choisir un avatar (si assez de points). */
    @PutMapping("/students/{studentId}/avatar/{avatarId}")
    public StudentProfileResponse selectAvatar(@PathVariable Long studentId, @PathVariable Long avatarId) {
        return service.selectAvatar(studentId, avatarId);
    }

    /** Top 10 des etudiants. */
    @GetMapping("/leaderboard")
    public List<LeaderboardEntry> leaderboard() {
        return service.getLeaderboard();
    }

    @GetMapping("/levels")
    public List<Level> levels() {
        return levelRepository.findAll();
    }

    @GetMapping("/badges")
    public List<Badge> badges() {
        return badgeRepository.findAll();
    }

    @GetMapping("/avatars")
    public List<Avatar> avatars() {
        return avatarRepository.findAll();
    }
}
