package com.example.tokengate.repository;

import com.example.tokengate.domain.AccessToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccessTokenRepository extends JpaRepository<AccessToken, Long> {

    Optional<AccessToken> findByTokenValue(String tokenValue);
}
