package com.perfstore.trust;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_trust")
public class UserTrust {

    @Id
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "trust_score", nullable = false)
    private int trustScore = 60;

    @Enumerated(EnumType.STRING)
    @Column(name = "trust_level", nullable = false)
    private TrustLevel trustLevel = TrustLevel.NORMAL;

    @Column(name = "successful_login_count", nullable = false)
    private int successfulLoginCount = 0;

    @Column(name = "suspicious_attempt_count", nullable = false)
    private int suspiciousAttemptCount = 0;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @Column(name = "last_failed_login_at")
    private LocalDateTime lastFailedLoginAt;

    // getters/setters

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public int getTrustScore() { return trustScore; }
    public void setTrustScore(int trustScore) { this.trustScore = trustScore; }

    public TrustLevel getTrustLevel() { return trustLevel; }
    public void setTrustLevel(TrustLevel trustLevel) { this.trustLevel = trustLevel; }

    public int getSuccessfulLoginCount() { return successfulLoginCount; }
    public void setSuccessfulLoginCount(int successfulLoginCount) { this.successfulLoginCount = successfulLoginCount; }

    public int getSuspiciousAttemptCount() { return suspiciousAttemptCount; }
    public void setSuspiciousAttemptCount(int suspiciousAttemptCount) { this.suspiciousAttemptCount = suspiciousAttemptCount; }

    public LocalDateTime getLastLoginAt() { return lastLoginAt; }
    public void setLastLoginAt(LocalDateTime lastLoginAt) { this.lastLoginAt = lastLoginAt; }

    public LocalDateTime getLastFailedLoginAt() { return lastFailedLoginAt; }
    public void setLastFailedLoginAt(LocalDateTime lastFailedLoginAt) { this.lastFailedLoginAt = lastFailedLoginAt; }
}