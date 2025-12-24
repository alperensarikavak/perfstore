package com.perfstore.config;

import com.perfstore.domain.AccessToken;
import com.perfstore.repository.AccessTokenRepository;
import com.perfstore.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

@Component
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private final AccessTokenRepository accessTokenRepository;
    private final UserRepository userRepository;

    public TokenAuthenticationFilter(AccessTokenRepository accessTokenRepository, UserRepository userRepository) {
        this.accessTokenRepository = accessTokenRepository;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);

            accessTokenRepository.findByTokenValue(token).ifPresent(accessToken -> {
                // Check expiry if needed (omitted for now as per previous logic)

                String username = accessToken.getOwnerName();
                userRepository.findByUsername(username).ifPresent(user -> {
                    // For now, no roles/authorities defined in User, so empty list
                    UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                            user.getUsername(),
                            user.getPassword(),
                            new ArrayList<>());

                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                });
            });
        }

        filterChain.doFilter(request, response);
    }
}
