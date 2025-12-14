package com.example.tokengate.controller;

import com.example.tokengate.domain.AccessToken;
import com.example.tokengate.domain.User;
import com.example.tokengate.dto.AuthDto;
import com.example.tokengate.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;

    public AuthController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthDto.AuthResponse> register(@RequestBody AuthDto.RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            return ResponseEntity.badRequest()
                    .body(new AuthDto.AuthResponse(null, null, "Username already exists"));
        }

        // Create new User
        User user = new User(request.getUsername(), request.getPassword(), request.getEmail());

        // Create a unique AccessToken for this user
        AccessToken token = new AccessToken();
        token.setTokenValue(UUID.randomUUID().toString().toUpperCase()); // Random Token
        token.setOwnerName(user.getUsername());
        token.setAllowedCategory("GENERAL"); // Default category
        token.setExpiresAt(LocalDateTime.now().plusDays(30)); // 30 days valid
        token.setMaxUsageCount(1000); // Generous limit
        token.setUsedCount(0);

        user.setAccessToken(token);

        userRepository.save(user);

        return ResponseEntity.ok(
                new AuthDto.AuthResponse(token.getTokenValue(), user.getUsername(), "Registration successful"));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthDto.AuthResponse> login(@RequestBody AuthDto.LoginRequest request) {
        return userRepository.findByUsername(request.getUsername())
                .filter(u -> u.getPassword().equals(request.getPassword())) // Simple text password check (In prod use
                                                                            // hashing!)
                .map(u -> ResponseEntity.ok(
                        new AuthDto.AuthResponse(
                                u.getAccessToken().getTokenValue(),
                                u.getUsername(),
                                "Login successful")))
                .orElse(ResponseEntity.status(401)
                        .body(new AuthDto.AuthResponse(null, null, "Invalid credentials")));
    }
}
