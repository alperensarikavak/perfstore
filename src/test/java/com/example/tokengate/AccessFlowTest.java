package com.example.tokengate;

import com.example.tokengate.domain.AccessToken;
import com.example.tokengate.dto.AccessCheckRequest;
import com.example.tokengate.repository.AccessTokenRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AccessFlowTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccessTokenRepository accessTokenRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testAccessFlow() throws Exception {
        // 1. Setup: Create a valid token in DB
        AccessToken token = new AccessToken();
        token.setTokenValue("valid-token-123");
        token.setOwnerName("Test User");
        token.setAllowedCategory("GENERAL");
        token.setExpiresAt(LocalDateTime.now().plusDays(1));
        token.setMaxUsageCount(100);
        token.setUsedCount(0);
        accessTokenRepository.save(token);

        // 2. Test: Check access with valid token
        AccessCheckRequest validRequest = new AccessCheckRequest();
        validRequest.setToken("valid-token-123");
        validRequest.setUrl("https://example.com/some-page");

        mockMvc.perform(post("/api/access/check")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.decision").value("GRANTED"));

        // 3. Test: Check access with invalid token
        AccessCheckRequest invalidRequest = new AccessCheckRequest();
        invalidRequest.setToken("invalid-token-999");
        invalidRequest.setUrl("https://example.com/some-page");

        mockMvc.perform(post("/api/access/check")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.decision").value("DENIED"));
    }
}
