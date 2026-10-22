package com.dripgrow.api.security;

import java.util.List;
import java.util.Locale;

public enum StaffRole {
    OWNER(List.of("dashboard", "orders", "catalog", "plans", "projects", "requests", "quotations", "settings", "languages", "staff", "audit")),
    ADMIN(List.of("dashboard", "orders", "catalog", "plans", "projects", "requests", "quotations")),
    MAINTENANCE(List.of("dashboard", "orders", "requests")),
    SERVICE(List.of("dashboard", "requests", "quotations"));

    private final List<String> permissions;
    StaffRole(List<String> permissions) { this.permissions = permissions; }
    public List<String> permissions() { return permissions; }
    public static StaffRole from(String value) {
        try { return valueOf(value == null ? "ADMIN" : value.toUpperCase(Locale.ROOT)); }
        catch (IllegalArgumentException ex) { return ADMIN; }
    }
}
