package com.example.tokengate.dto;

public class AccessCheckResponse {

    private String decision; // GRANTED / DENIED
    private String domain;
    private String reason;   // null olabilir (GRANTED ise)

    public AccessCheckResponse(String decision, String domain, String reason) {
        this.decision = decision;
        this.domain = domain;
        this.reason = reason;
    }

    public String getDecision() {
        return decision;
    }

    public String getDomain() {
        return domain;
    }

    public String getReason() {
        return reason;
    }
}
