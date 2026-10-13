package com.dripgrow.api.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "app_settings")
public class AppSettings {

    public static final String DEFAULT_ID = "main";

    @Id
    private String id = DEFAULT_ID;

    private String jwtSecret;
    private String adminUsername;
    /** Plain password only used once at bootstrap if admin user is missing. */
    private String adminBootstrapPassword;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getJwtSecret() {
        return jwtSecret;
    }

    public void setJwtSecret(String jwtSecret) {
        this.jwtSecret = jwtSecret;
    }

    public String getAdminUsername() {
        return adminUsername;
    }

    public void setAdminUsername(String adminUsername) {
        this.adminUsername = adminUsername;
    }

    public String getAdminBootstrapPassword() {
        return adminBootstrapPassword;
    }

    public void setAdminBootstrapPassword(String adminBootstrapPassword) {
        this.adminBootstrapPassword = adminBootstrapPassword;
    }
}
