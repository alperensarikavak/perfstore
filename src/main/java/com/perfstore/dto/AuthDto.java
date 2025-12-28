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

        // MANUEL GETTER & SETTER (Lombok çalışmazsa diye)
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }

        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RegisterRequest {
        private String username;
        private String password;
        private String email;
        private String pin;

        // MANUEL GETTER & SETTER
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }

        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getPin() { return pin; }
        public void setPin(String pin) { this.pin = pin; }
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

        // Constructor 1
        public AuthResponse(String token, String username, String message) {
            this.token = token;
            this.username = username;
            this.message = message;
        }

        // Constructor 2
        public AuthResponse(String token, String username, String message, String trustLevel) {
            this.token = token;
            this.username = username;
            this.message = message;
            this.trustLevel = trustLevel;
        }

        // MANUEL GETTER & SETTER
        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }

        public String getTrustLevel() { return trustLevel; }
        public void setTrustLevel(String trustLevel) { this.trustLevel = trustLevel; }

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }

        public String getChallengeId() { return challengeId; }
        public void setChallengeId(String challengeId) { this.challengeId = challengeId; }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VerifyPinRequest {
        private String username;
        private String pin;

        // MANUEL GETTER & SETTER
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }

        public String getPin() { return pin; }
        public void setPin(String pin) { this.pin = pin; }
    }
}