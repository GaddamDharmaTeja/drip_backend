package com.dripgrow.api.dto;

public class LoginResponse {

    private String token;
    private long expiresIn;
    private String username;

    public LoginResponse(String token, long expiresIn, String username) {
        this.token = token;
        this.expiresIn = expiresIn;
        this.username = username;
    }

    public String getToken() {
        return token;
    }

    public long getExpiresIn() {
        return expiresIn;
    }

    public String getUsername() {
        return username;
    }
}
