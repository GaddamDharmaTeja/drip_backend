package com.dripgrow.api.dto;

import com.dripgrow.api.model.AdminUser;
import com.dripgrow.api.security.StaffRole;
import java.util.List;

public record StaffUserResponse(String id, String username, String displayName, String email, String role, boolean active, List<String> permissions) {
    public static StaffUserResponse from(AdminUser user) {
        StaffRole role = StaffRole.from(user.getRole());
        return new StaffUserResponse(user.getId(), user.getUsername(), user.getDisplayName(), user.getEmail(), role.name(), user.isActive(), role.permissions());
    }
}
