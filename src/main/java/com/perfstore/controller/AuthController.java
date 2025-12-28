package com.perfstore.controller;

import com.perfstore.domain.AccessToken;
import com.perfstore.domain.User;
import com.perfstore.dto.AuthDto;
import com.perfstore.repository.UserRepository;
import com.perfstore.trust.TrustLevel;
import com.perfstore.service.TrustScoreService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

        private final UserRepository userRepository;
        private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;
        private final TrustScoreService trustScoreService; // ✅ EKLE

        public AuthController(UserRepository userRepository,
                        org.springframework.security.crypto.password.PasswordEncoder passwordEncoder,
                        TrustScoreService trustScoreService) { // ✅ EKLE
                this.userRepository = userRepository;
                this.passwordEncoder = passwordEncoder;
                this.trustScoreService = trustScoreService; // ✅ EKLE
        }

        @PostMapping("/register")
        public ResponseEntity<AuthDto.AuthResponse> register(@RequestBody AuthDto.RegisterRequest request) {
                if (userRepository.existsByUsername(request.getUsername())) {
                        return ResponseEntity.badRequest()
                                        .body(new AuthDto.AuthResponse(null, null, "Username already exists"));
                }

                User user = new User(request.getUsername(), passwordEncoder.encode(request.getPassword()),
                                request.getEmail());

                if (request.getPin() == null || request.getPin().length() < 4) {
                        return ResponseEntity.badRequest()
                                        .body(new AuthDto.AuthResponse(null, null, "PIN must be at least 4 digits"));
                }

                // PIN hash kaydet
                user.setPinHash(passwordEncoder.encode(request.getPin()));

                AccessToken token = new AccessToken();
                token.setTokenValue(UUID.randomUUID().toString().toUpperCase());
                token.setOwnerName(user.getUsername());
                token.setAllowedCategory("GENERAL");
                token.setExpiresAt(LocalDateTime.now().plusDays(30));
                token.setMaxUsageCount(1000);
                token.setUsedCount(0);

                user.setAccessToken(token);

                User savedUser = userRepository.save(user); // ✅ burada savedUser al
                trustScoreService.createProfileIfMissing(savedUser.getId()); // ✅ TAM BURAYA

                return ResponseEntity.ok(
                                new AuthDto.AuthResponse(token.getTokenValue(), user.getUsername(),
                                                "Registration successful"));
        }

        @PostMapping("/login")
        public ResponseEntity<AuthDto.AuthResponse> login(@RequestBody AuthDto.LoginRequest request) {

                return userRepository.findByUsername(request.getUsername())
                                .map(u -> {

                                        trustScoreService.createProfileIfMissing(u.getId());

                                        // 1️⃣ Şifre kontrolü
                                        if (passwordEncoder.matches(request.getPassword(), u.getPassword())) {

                                                // 2️⃣ Trust score hesapla
                                                TrustLevel level = trustScoreService.onLoginSuccess(u.getId());

                                                // 3️⃣ 🔐 RESTRICTED ise → TOKEN DÖNME
                                                if (level == TrustLevel.RESTRICTED) {
                                                        return ResponseEntity.ok(
                                                                        new AuthDto.AuthResponse(
                                                                                        null, // ❌ token yok
                                                                                        u.getUsername(),
                                                                                        "Restricted user. Please enter your PIN.",
                                                                                        level.name(),
                                                                                        "REQUIRES_PIN", // 🔴 ÖNEMLİ
                                                                                        null));
                                                }

                                                // 4️⃣ ✅ NORMAL kullanıcı → TOKEN DÖN
                                                return ResponseEntity.ok(
                                                                new AuthDto.AuthResponse(
                                                                                u.getAccessToken().getTokenValue(),
                                                                                u.getUsername(),
                                                                                "Login successful",
                                                                                level.name(),
                                                                                "OK",
                                                                                null));
                                        }

                                        // 5️⃣ ❌ Şifre yanlışsa
                                        trustScoreService.onLoginFail(u.getId());
                                        return ResponseEntity.status(401)
                                                        .body(new AuthDto.AuthResponse(null, null,
                                                                        "Invalid credentials"));
                                })
                                .orElse(ResponseEntity.status(401)
                                                .body(new AuthDto.AuthResponse(null, null, "Invalid credentials")));
        }

        @PostMapping("/verify-pin")
        public ResponseEntity<AuthDto.AuthResponse> verifyPin(@RequestBody AuthDto.VerifyPinRequest request) {

                return userRepository.findByUsername(request.getUsername())
                                .map(u -> {

                                        if (u.getPinHash() == null) {
                                                return ResponseEntity.status(400)
                                                                .body(new AuthDto.AuthResponse(null, null,
                                                                                "PIN not set"));
                                        }

                                        if (!passwordEncoder.matches(request.getPin(), u.getPinHash())) {
                                                trustScoreService.onLoginFail(u.getId()); // istersen puanı düşür
                                                return ResponseEntity.status(401)
                                                                .body(new AuthDto.AuthResponse(null, null,
                                                                                "Invalid PIN"));
                                        }

                                        // ✅ PIN doğru → token ver
                                        return ResponseEntity.ok(
                                                        new AuthDto.AuthResponse(
                                                                        u.getAccessToken().getTokenValue(),
                                                                        u.getUsername(),
                                                                        "PIN verified. Login successful.",
                                                                        TrustLevel.NORMAL.name(), // istersen level'ı
                                                                                                  // tekrar hesaplatırız
                                                                        "OK",
                                                                        null));
                                })
                                .orElse(ResponseEntity.status(401)
                                                .body(new AuthDto.AuthResponse(null, null, "Invalid credentials")));
        }

}