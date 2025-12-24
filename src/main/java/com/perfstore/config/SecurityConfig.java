package com.perfstore.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

        private final TokenAuthenticationFilter tokenAuthenticationFilter;

        public SecurityConfig(TokenAuthenticationFilter tokenAuthenticationFilter) {
                this.tokenAuthenticationFilter = tokenAuthenticationFilter;
        }

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                                .csrf(csrf -> csrf.disable())
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .authorizeHttpRequests(auth -> auth
                                                // UI sayfamız
                                                .requestMatchers("/", "/index.html", "/css/**", "/js/**").permitAll()
                                                // API endpoint
                                                .requestMatchers("/api/access/check").permitAll()
                                                .requestMatchers("/api/products").permitAll()
                                                .requestMatchers("/api/categories").permitAll() // Added
                                                .requestMatchers("/api/auth/**").permitAll()
                                                // H2 console (dev için)
                                                .requestMatchers("/h2-console/**").permitAll()
                                                // Diğer her şey kimlik doğrulama gerektirir
                                                .anyRequest().authenticated())
                                .addFilterBefore(tokenAuthenticationFilter,
                                                org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class)
                                .headers(headers -> headers.frameOptions(frame -> frame.disable()))
                                .formLogin(form -> form.disable())
                                .httpBasic(basic -> basic.disable());

                return http.build();
        }

        @Bean
        public org.springframework.security.crypto.password.PasswordEncoder passwordEncoder() {
                return new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();
        }

}
