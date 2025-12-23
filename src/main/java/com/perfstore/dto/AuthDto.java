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
    }

    public static class AuthResponse {
        private String token;
        private String username;
        private String message;
        private String trustLevel; // ✅ eklendi

        // ✅ ESKİ constructor (KALSIN) - kırılma olmasın
        public AuthResponse(String token, String username, String message) {
            this.token = token;
            this.username = username;
            this.message = message;
        }

        // ✅ YENİ constructor
        public AuthResponse(String token, String username, String message, String trustLevel) {
            this.token = token;
            this.username = username;
            this.message = message;
            this.trustLevel = trustLevel;
        }

        // ✅ Getter/Setter (JSON için gerekli)
        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }

        public String getTrustLevel() { return trustLevel; }
        public void setTrustLevel(String trustLevel) { this.trustLevel = trustLevel; }
    }

}