package com.example.tokengate.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class AccessToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // DB'de hashlenmiş saklayacağız ama şimdilik düz string dursun,
    // sonra hash'e çeviririz.
    @Column(nullable = false, unique = true, length = 255)
    private String tokenValue;

    @Column(nullable = false)
    private String ownerName;

    // Örn: GENERAL, ADULT, INTERNAL
    @Column(nullable = false, length = 50)
    private String allowedCategory;

    private LocalDateTime expiresAt;

    private Integer maxUsageCount;

    private Integer usedCount = 0;

    // getter / setter / ctor

    public Long getId() {
        return id;
    }

    public String getTokenValue() {
        return tokenValue;
    }

    public void setTokenValue(String tokenValue) {
        this.tokenValue = tokenValue;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getAllowedCategory() {
        return allowedCategory;
    }

    public void setAllowedCategory(String allowedCategory) {
        this.allowedCategory = allowedCategory;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public Integer getMaxUsageCount() {
        return maxUsageCount;
    }

    public void setMaxUsageCount(Integer maxUsageCount) {
        this.maxUsageCount = maxUsageCount;
    }

    public Integer getUsedCount() {
        return usedCount;
    }

    public void setUsedCount(Integer usedCount) {
        this.usedCount = usedCount;
    }
}
