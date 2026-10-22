package com.dripgrow.api.dto;

import com.dripgrow.api.model.AdminUser;
import com.dripgrow.api.model.Customer;
import com.dripgrow.api.security.StaffRole;
import java.util.List;

public record AccountResponse(String id, String type, String displayName, String username, String email, String phone, String role, boolean active, List<String> permissions) {
    public static AccountResponse from(AdminUser user) {
        StaffRole role = StaffRole.from(user.getRole());
        return new AccountResponse(user.getId(), "STAFF", user.getDisplayName(), user.getUsername(), user.getEmail(), null, role.name(), user.isActive(), role.permissions());
    }
    public static AccountResponse from(Customer customer) {
        return new AccountResponse(customer.getId(), "CUSTOMER", customer.getFullName(), null, customer.getEmail(), customer.getPhone(), "CUSTOMER", customer.isActive(), List.of("shop", "orders", "saved plans", "addresses"));
    }
}
