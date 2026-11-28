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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Gamification", description = "Points, niveaux, badges, avatars et classement des étudiants")
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
    @Operation(summary = "Ajouter des points à un étudiant")
    @ApiResponse(responseCode = "200", description = "Points ajoutés, profil mis à jour")
    @ApiResponse(responseCode = "400", description = "Requête invalide")
    @PostMapping("/points")
    public StudentProfileResponse addPoints(@Valid @RequestBody AddPointsRequest request) {
        return service.addPoints(request);
    }

    /** Profil de gamification : points, niveau, badges, avatar. */
    @Operation(summary = "Récupérer le profil de gamification d'un étudiant")
    @ApiResponse(responseCode = "200", description = "Profil trouvé")
    @ApiResponse(responseCode = "404", description = "Étudiant introuvable")
    @GetMapping("/students/{studentId}")
    public StudentProfileResponse getProfile(@PathVariable Long studentId) {
        return service.getProfile(studentId);
    }

    /** Historique des points d'un etudiant. */
    @Operation(summary = "Historique des points d'un étudiant")
    @ApiResponse(responseCode = "200", description = "Liste des transactions")
    @GetMapping("/students/{studentId}/transactions")
    public List<PointTransaction> getTransactions(@PathVariable Long studentId) {
        return service.getTransactions(studentId);
    }

    /** Choisir un avatar (si assez de points). */
    @Operation(summary = "Choisir un avatar (si l'étudiant a assez de points)")
    @ApiResponse(responseCode = "200", description = "Avatar sélectionné")
    @ApiResponse(responseCode = "404", description = "Étudiant ou avatar introuvable")
    @ApiResponse(responseCode = "409", description = "Points insuffisants")
    @PutMapping("/students/{studentId}/avatar/{avatarId}")
    public StudentProfileResponse selectAvatar(@PathVariable Long studentId, @PathVariable Long avatarId) {
        return service.selectAvatar(studentId, avatarId);
    }

    /** Top 10 des etudiants. */
    @Operation(summary = "Classement : top 10 des étudiants")
    @ApiResponse(responseCode = "200", description = "Classement")
    @GetMapping("/leaderboard")
    public List<LeaderboardEntry> leaderboard() {
        return service.getLeaderboard();
    }

    @Operation(summary = "Lister les niveaux")
    @ApiResponse(responseCode = "200", description = "Liste des niveaux")
    @GetMapping("/levels")
    public List<Level> levels() {
        return levelRepository.findAll();
    }

    @Operation(summary = "Lister les badges")
    @ApiResponse(responseCode = "200", description = "Liste des badges")
    @GetMapping("/badges")
    public List<Badge> badges() {
        return badgeRepository.findAll();
    }

    @Operation(summary = "Lister les avatars")
    @ApiResponse(responseCode = "200", description = "Liste des avatars")
    @GetMapping("/avatars")
    public List<Avatar> avatars() {
        return avatarRepository.findAll();
    }
}
