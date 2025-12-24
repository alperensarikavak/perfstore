package com.perfstore.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class AuthDto {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginRequest {
        private String username;
        private String password;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RegisterRequest {
        private String username;
        private String password;
        private String email;
        private String pin;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AuthResponse {
        private String token;
        private String username;
        private String message;
        private String trustLevel;
        private String status;      // OK | REQUIRES_PIN
        private String challengeId; // PIN için null

        // ✅ eski kodlar kırılmasın
        public AuthResponse(String token, String username, String message) {
            this.token = token;
            this.username = username;
            this.message = message;
        }

        public AuthResponse(String token, String username, String message, String trustLevel) {
            this.token = token;
            this.username = username;
            this.message = message;
            this.trustLevel = trustLevel;
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VerifyPinRequest {
        private String username;
        private String pin;
    }
}
