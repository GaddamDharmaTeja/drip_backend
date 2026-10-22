package com.dripgrow.api.service;

import com.dripgrow.api.dto.StaffUserRequest;
import com.dripgrow.api.dto.StaffUserResponse;
import com.dripgrow.api.model.AdminUser;
import com.dripgrow.api.repository.AdminUserRepository;
import com.dripgrow.api.security.StaffRole;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class StaffUserService {
    private final AdminUserRepository users;
    private final PasswordEncoder passwordEncoder;
    public StaffUserService(AdminUserRepository users, PasswordEncoder passwordEncoder) { this.users = users; this.passwordEncoder = passwordEncoder; }
    public List<StaffUserResponse> list() { return users.findAll().stream().map(StaffUserResponse::from).toList(); }
    public StaffUserResponse create(StaffUserRequest request) {
        if (users.existsByUsername(request.getUsername())) throw new ResponseStatusException(HttpStatus.CONFLICT, "That username is already in use");
        StaffRole role = StaffRole.from(request.getRole());
        if (!role.name().equalsIgnoreCase(request.getRole())) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Choose Owner, Admin, Maintenance, or Service");
        AdminUser user = new AdminUser();
        user.setUsername(request.getUsername().trim()); user.setDisplayName(request.getDisplayName().trim()); user.setEmail(request.getEmail().trim().toLowerCase()); user.setPasswordHash(passwordEncoder.encode(request.getPassword())); user.setRole(role.name()); user.setActive(true);
        return StaffUserResponse.from(users.save(user));
    }
    public StaffUserResponse setActive(String id, boolean active) {
        AdminUser user = users.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Staff account not found"));
        user.setActive(active); return StaffUserResponse.from(users.save(user));
    }
}
