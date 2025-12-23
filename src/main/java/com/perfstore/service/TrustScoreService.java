package com.perfstore.service;

import com.perfstore.repository.UserTrustRepository;
import com.perfstore.trust.TrustLevel;
import com.perfstore.trust.UserTrust;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class TrustScoreService {

    private final UserTrustRepository repo;

    public TrustScoreService(UserTrustRepository repo) {
        this.repo = repo;
    }

    @Transactional
    public void createProfileIfMissing(Long userId) {
        repo.findById(userId).orElseGet(() -> {
            UserTrust ut = new UserTrust();
            ut.setUserId(userId);
            ut.setTrustScore(60);
            ut.setTrustLevel(calculateLevel(60));
            return repo.save(ut);
        });
    }

    @Transactional
    public TrustLevel onLoginSuccess(Long userId) {
        UserTrust ut = repo.findById(userId).orElseGet(() -> {
            UserTrust x = new UserTrust();
            x.setUserId(userId);
            return x;
        });

        ut.setSuccessfulLoginCount(ut.getSuccessfulLoginCount() + 1);
        ut.setLastLoginAt(LocalDateTime.now());

        // +2 puan (basit)
        ut.setTrustScore(clamp(ut.getTrustScore() + 2));
        ut.setTrustLevel(calculateLevel(ut.getTrustScore()));

        repo.save(ut);
        return ut.getTrustLevel();
    }

    @Transactional
    public TrustLevel onLoginFail(Long userId) {
        UserTrust ut = repo.findById(userId).orElseGet(() -> {
            UserTrust x = new UserTrust();
            x.setUserId(userId);
            return x;
        });

        ut.setLastFailedLoginAt(LocalDateTime.now());

        // -3 puan (basit)
        ut.setTrustScore(clamp(ut.getTrustScore() - 3));
        ut.setTrustLevel(calculateLevel(ut.getTrustScore()));

        repo.save(ut);
        return ut.getTrustLevel();
    }

    @Transactional
    public TrustLevel onSuspiciousAttempt(Long userId) {
        UserTrust ut = repo.findById(userId).orElseGet(() -> {
            UserTrust x = new UserTrust();
            x.setUserId(userId);
            return x;
        });

        ut.setSuspiciousAttemptCount(ut.getSuspiciousAttemptCount() + 1);

        // -15 puan (basit)
        ut.setTrustScore(clamp(ut.getTrustScore() - 15));
        ut.setTrustLevel(calculateLevel(ut.getTrustScore()));

        repo.save(ut);
        return ut.getTrustLevel();
    }

    private int clamp(int score) {
        if (score < 0) return 0;
        if (score > 100) return 100;
        return score;
    }

    private TrustLevel calculateLevel(int score) {
        if (score >= 80) return TrustLevel.TRUSTED;
        if (score >= 50) return TrustLevel.NORMAL;
        return TrustLevel.RESTRICTED;
    }
}