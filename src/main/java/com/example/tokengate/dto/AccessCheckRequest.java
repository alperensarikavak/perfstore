package com.example.tokengate.dto;

import jakarta.validation.constraints.NotBlank;

public class AccessCheckRequest {

    @NotBlank
    private String token;

    @NotBlank
    private String url;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
