package com.dripgrow.api.dto;

public class LoginResponse {

    private String token;
    private long expiresIn;
    private String username;
    private String role;
    private java.util.List<String> permissions;

    public LoginResponse(String token, long expiresIn, String username, String role, java.util.List<String> permissions) {
        this.token = token;
        this.expiresIn = expiresIn;
        this.username = username;
        this.role = role;
        this.permissions = permissions;
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
    public String getRole() { return role; }
    public java.util.List<String> getPermissions() { return permissions; }
}
