package com.example.tokengate.controller;

import com.example.tokengate.dto.AccessCheckRequest;
import com.example.tokengate.dto.AccessCheckResponse;
import com.example.tokengate.service.AccessDecisionService;
import com.example.tokengate.service.AccessDecisionService.DecisionResult;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/access")
public class AccessController {

    private final AccessDecisionService accessDecisionService;

    public AccessController(AccessDecisionService accessDecisionService) {
        this.accessDecisionService = accessDecisionService;
    }

    @PostMapping("/check")
    public ResponseEntity<AccessCheckResponse> checkAccess(
            @Valid @RequestBody AccessCheckRequest request,
            HttpServletRequest httpRequest) {

        String domain = extractDomain(request.getUrl());
        String clientIp = httpRequest.getRemoteAddr(); // basit IP alma

        DecisionResult result =
                accessDecisionService.decide(request.getToken(), domain, clientIp);

        if (result.getDecision() == AccessDecisionService.Decision.GRANTED) {
            AccessCheckResponse response =
                    new AccessCheckResponse("GRANTED", domain, null);
            return ResponseEntity.ok(response);
        } else {
            AccessCheckResponse response =
                    new AccessCheckResponse("DENIED", domain, result.getReason());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }
    }

    private String extractDomain(String url) {
        String cleaned = url.replaceFirst("^https?://", "");
        int slashIndex = cleaned.indexOf('/');
        if (slashIndex != -1) {
            return cleaned.substring(0, slashIndex);
        }
        return cleaned;
    }
}
