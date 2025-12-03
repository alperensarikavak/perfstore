package com.example.tokengate.service;

import com.example.tokengate.domain.AccessLog;
import com.example.tokengate.domain.AccessToken;
import com.example.tokengate.domain.SitePolicy;
import com.example.tokengate.repository.AccessLogRepository;
import com.example.tokengate.repository.AccessTokenRepository;
import com.example.tokengate.repository.SitePolicyRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AccessDecisionService {

    // ---- Karar tipi enum'u ----
    public enum Decision {
        GRANTED,
        DENIED
    }

    // ---- Karar sonucu DTO'su (service içi) ----
    public static class DecisionResult {
        private final Decision decision;
        private final String reason;

        public DecisionResult(Decision decision, String reason) {
            this.decision = decision;
            this.reason = reason;
        }

        public Decision getDecision() {
            return decision;
        }

        public String getReason() {
            return reason;
        }
    }

    private final AccessTokenRepository accessTokenRepository;
    private final SitePolicyRepository sitePolicyRepository;
    private final AccessLogRepository accessLogRepository;

    public AccessDecisionService(AccessTokenRepository accessTokenRepository,
                                 SitePolicyRepository sitePolicyRepository,
                                 AccessLogRepository accessLogRepository) {
        this.accessTokenRepository = accessTokenRepository;
        this.sitePolicyRepository = sitePolicyRepository;
        this.accessLogRepository = accessLogRepository;
    }

    // Client IP olmadan çağırmak için helper
    public DecisionResult decide(String tokenValue, String domain) {
        return decide(tokenValue, domain, null);
    }

    // Asıl karar metodu (clientIp dahil)
    public DecisionResult decide(String tokenValue, String domain, String clientIp) {

        DecisionResult result;

        // 1) Token boş mu?
        if (tokenValue == null || tokenValue.isBlank()) {
            result = new DecisionResult(Decision.DENIED, "TOKEN_MISSING");
            logAccess(tokenValue, domain, result, clientIp);
            return result;
        }

        // 2) Token DB'de var mı?
        Optional<AccessToken> optToken = accessTokenRepository.findByTokenValue(tokenValue);
        if (optToken.isEmpty()) {
            result = new DecisionResult(Decision.DENIED, "INVALID_TOKEN");
            logAccess(tokenValue, domain, result, clientIp);
            return result;
        }

        AccessToken token = optToken.get();

        // 3) Süresi dolmuş mu?
        if (token.getExpiresAt() != null &&
                token.getExpiresAt().isBefore(LocalDateTime.now())) {
            result = new DecisionResult(Decision.DENIED, "TOKEN_EXPIRED");
            logAccess(tokenValue, domain, result, clientIp);
            return result;
        }

        // 4) Kullanım hakkı dolmuş mu?
        if (token.getMaxUsageCount() != null) {
            int used = token.getUsedCount() != null ? token.getUsedCount() : 0;
            if (used >= token.getMaxUsageCount()) {
                result = new DecisionResult(Decision.DENIED, "USAGE_LIMIT_REACHED");
                logAccess(tokenValue, domain, result, clientIp);
                return result;
            }
        }

        // 5) Domain policy var mı? Varsa kategori kontrolü
        Optional<SitePolicy> optPolicy = sitePolicyRepository.findByDomain(domain);
        if (optPolicy.isPresent()) {
            SitePolicy policy = optPolicy.get();

            if (!isCategoryAllowed(token.getAllowedCategory(), policy.getCategory())) {
                result = new DecisionResult(Decision.DENIED, "INSUFFICIENT_CATEGORY");
                logAccess(tokenValue, domain, result, clientIp);
                return result;
            }
        }

        // 6) Buraya kadar geldiyse GRANTED → usage++ ve logla
        incrementUsageCount(token);

        result = new DecisionResult(Decision.GRANTED, null);
        logAccess(tokenValue, domain, result, clientIp);
        return result;
    }

    // Token kategorisi site kategorisini kapsıyor mu?
    private boolean isCategoryAllowed(String tokenCategory, String siteCategory) {
        if (tokenCategory == null || siteCategory == null) {
            return false;
        }

        // Basit kural:
        // GENERAL  -> sadece GENERAL
        // ADULT    -> GENERAL + ADULT
        // INTERNAL -> sadece INTERNAL (örnek)
        switch (tokenCategory.toUpperCase()) {
            case "ADULT":
                return siteCategory.equalsIgnoreCase("GENERAL")
                        || siteCategory.equalsIgnoreCase("ADULT");
            case "GENERAL":
                return siteCategory.equalsIgnoreCase("GENERAL");
            case "INTERNAL":
                return siteCategory.equalsIgnoreCase("INTERNAL");
            default:
                return false;
        }
    }

    private void incrementUsageCount(AccessToken token) {
        Integer used = token.getUsedCount();
        if (used == null) {
            used = 0;
        }
        token.setUsedCount(used + 1);
        accessTokenRepository.save(token);
    }

    private void logAccess(String tokenValue,
                           String domain,
                           DecisionResult result,
                           String clientIp) {
        AccessLog log = new AccessLog();
        log.setMaskedToken(maskToken(tokenValue));
        log.setRequestedDomain(domain);
        log.setDecision(result.getDecision().name());
        log.setReason(result.getReason());
        log.setClientIp(clientIp);
        accessLogRepository.save(log);
    }

    private String maskToken(String tokenValue) {
        if (tokenValue == null || tokenValue.isBlank()) {
            return null;
        }
        if (tokenValue.length() <= 4) {
            return "****";
        }
        return tokenValue.substring(0, 4) + "****";
    }
}
