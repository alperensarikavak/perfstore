package com.example.tokengate.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class AccessLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String maskedToken;    // loglarda tam token saklamayacağız

    private String requestedDomain;

    private String decision;       // GRANTED / DENIED

    private String reason;         // TOKEN_EXPIRED, CATEGORY_MISMATCH, vs.

    private String clientIp;

    private LocalDateTime createdAt = LocalDateTime.now();

    // getter / setter

    public Long getId() {
        return id;
    }

    public String getMaskedToken() {
        return maskedToken;
    }

    public void setMaskedToken(String maskedToken) {
        this.maskedToken = maskedToken;
    }

    public String getRequestedDomain() {
        return requestedDomain;
    }

    public void setRequestedDomain(String requestedDomain) {
        this.requestedDomain = requestedDomain;
    }

    public String getDecision() {
        return decision;
    }

    public void setDecision(String decision) {
        this.decision = decision;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getClientIp() {
        return clientIp;
    }

    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
