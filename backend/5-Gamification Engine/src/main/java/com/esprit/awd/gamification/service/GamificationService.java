package com.esprit.awd.gamification.service;

import com.esprit.awd.gamification.dto.AddPointsRequest;
import com.esprit.awd.gamification.dto.LeaderboardEntry;
import com.esprit.awd.gamification.dto.StudentProfileResponse;
import com.esprit.awd.gamification.model.Avatar;
import com.esprit.awd.gamification.model.Badge;
import com.esprit.awd.gamification.model.Level;
import com.esprit.awd.gamification.model.PointTransaction;
import com.esprit.awd.gamification.model.StudentBadge;
import com.esprit.awd.gamification.model.StudentGamification;
import com.esprit.awd.gamification.repository.AvatarRepository;
import com.esprit.awd.gamification.repository.BadgeRepository;
import com.esprit.awd.gamification.repository.LevelRepository;
import com.esprit.awd.gamification.repository.PointTransactionRepository;
import com.esprit.awd.gamification.repository.StudentBadgeRepository;
import com.esprit.awd.gamification.repository.StudentGamificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class GamificationService {

    /** Format de condition supporte pour un badge : "POINTS>=50". */
    private static final Pattern POINTS_CONDITION = Pattern.compile("^\\s*POINTS\\s*>=\\s*(\\d+)\\s*$");

    private final StudentGamificationRepository studentRepository;
    private final PointTransactionRepository transactionRepository;
    private final LevelRepository levelRepository;
    private final BadgeRepository badgeRepository;
    private final StudentBadgeRepository studentBadgeRepository;
    private final AvatarRepository avatarRepository;

    public GamificationService(StudentGamificationRepository studentRepository,
                               PointTransactionRepository transactionRepository,
                               LevelRepository levelRepository,
                               BadgeRepository badgeRepository,
                               StudentBadgeRepository studentBadgeRepository,
                               AvatarRepository avatarRepository) {
        this.studentRepository = studentRepository;
        this.transactionRepository = transactionRepository;
        this.levelRepository = levelRepository;
        this.badgeRepository = badgeRepository;
        this.studentBadgeRepository = studentBadgeRepository;
        this.avatarRepository = avatarRepository;
    }

    /**
     * Ajoute des points a un etudiant : enregistre la transaction, met a jour le total et le niveau,
     * puis attribue les badges dont la condition est maintenant remplie.
     * Si un eventId deja traite est fourni, rien n'est ajoute (idempotence pour RabbitMQ).
     */
    @Transactional
    public StudentProfileResponse addPoints(AddPointsRequest request) {
        String eventId = request.eventId();
        if (eventId != null && !eventId.isBlank() && transactionRepository.existsByEventId(eventId)) {
            return getProfile(request.studentId());
        }

        StudentGamification student = studentRepository.findByStudentId(request.studentId())
                .orElseGet(() -> new StudentGamification(request.studentId()));

        transactionRepository.save(new PointTransaction(
                request.studentId(), request.points(), request.reason(), eventId));

        student.setTotalPoints(student.getTotalPoints() + request.points());
        levelRepository.findFirstByRequiredPointsLessThanEqualOrderByRequiredPointsDesc(student.getTotalPoints())
                .ifPresent(level -> student.setCurrentLevelId(level.getId()));
        studentRepository.save(student);

        awardBadges(student);

        return toProfile(student);
    }

    @Transactional(readOnly = true)
    public StudentProfileResponse getProfile(Long studentId) {
        return toProfile(findStudent(studentId));
    }

    @Transactional(readOnly = true)
    public List<PointTransaction> getTransactions(Long studentId) {
        return transactionRepository.findByStudentIdOrderByCreatedAtDesc(studentId);
    }

    @Transactional(readOnly = true)
    public List<LeaderboardEntry> getLeaderboard() {
        return studentRepository.findTop10ByOrderByTotalPointsDesc().stream()
                .map(s -> new LeaderboardEntry(s.getStudentId(), s.getTotalPoints(), s.getCurrentLevelId()))
                .toList();
    }

    /** Choisir un avatar : possible uniquement si l'etudiant a assez de points. */
    @Transactional
    public StudentProfileResponse selectAvatar(Long studentId, Long avatarId) {
        StudentGamification student = findStudent(studentId);
        Avatar avatar = avatarRepository.findById(avatarId)
                .orElseThrow(() -> new NoSuchElementException("Avatar introuvable : " + avatarId));

        if (student.getTotalPoints() < avatar.getRequiredPoints()) {
            throw new IllegalStateException("Points insuffisants : il faut " + avatar.getRequiredPoints()
                    + " points pour cet avatar (actuel : " + student.getTotalPoints() + ")");
        }
        student.setSelectedAvatarId(avatarId);
        return toProfile(studentRepository.save(student));
    }

    // ---------------------------------------------------------------- helpers

    private StudentGamification findStudent(Long studentId) {
        return studentRepository.findByStudentId(studentId)
                .orElseThrow(() -> new NoSuchElementException("Aucun profil de gamification pour studentId=" + studentId));
    }

    private void awardBadges(StudentGamification student) {
        for (Badge badge : badgeRepository.findAll()) {
            Integer threshold = parsePointsThreshold(badge.getCondition());
            if (threshold != null
                    && student.getTotalPoints() >= threshold
                    && !studentBadgeRepository.existsByStudentIdAndBadgeId(student.getStudentId(), badge.getId())) {
                studentBadgeRepository.save(new StudentBadge(student.getStudentId(), badge.getId()));
            }
        }
    }

    private Integer parsePointsThreshold(String condition) {
        if (condition == null) {
            return null;
        }
        Matcher m = POINTS_CONDITION.matcher(condition);
        return m.matches() ? Integer.valueOf(m.group(1)) : null;
    }

    private StudentProfileResponse toProfile(StudentGamification student) {
        Level level = student.getCurrentLevelId() == null ? null
                : levelRepository.findById(student.getCurrentLevelId()).orElse(null);

        List<Long> badgeIds = studentBadgeRepository.findByStudentId(student.getStudentId()).stream()
                .map(StudentBadge::getBadgeId)
                .toList();
        List<Badge> badges = badgeRepository.findAllById(badgeIds);

        return new StudentProfileResponse(student.getStudentId(), student.getTotalPoints(),
                level, badges, student.getSelectedAvatarId());
    }
}
