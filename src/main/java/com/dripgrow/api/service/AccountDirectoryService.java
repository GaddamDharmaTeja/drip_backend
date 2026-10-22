package com.dripgrow.api.service;

import com.dripgrow.api.dto.AccountResponse;
import com.dripgrow.api.dto.AccountUpdateRequest;
import com.dripgrow.api.model.AdminUser;
import com.dripgrow.api.model.Customer;
import com.dripgrow.api.repository.AdminUserRepository;
import com.dripgrow.api.repository.CustomerRepository;
import com.dripgrow.api.security.StaffRole;
import java.util.Comparator;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AccountDirectoryService {
    private final AdminUserRepository staff;
    private final CustomerRepository customers;
    public AccountDirectoryService(AdminUserRepository staff, CustomerRepository customers) { this.staff = staff; this.customers = customers; }
    public List<AccountResponse> list() {
        return java.util.stream.Stream.concat(staff.findAll().stream().map(AccountResponse::from), customers.findAll().stream().map(AccountResponse::from))
                .sorted(Comparator.comparing(AccountResponse::type).thenComparing(account -> account.displayName() == null ? "" : account.displayName(), String.CASE_INSENSITIVE_ORDER)).toList();
    }
    public AccountResponse update(String type, String id, AccountUpdateRequest request) {
        if ("STAFF".equalsIgnoreCase(type)) return updateStaff(id, request);
        if ("CUSTOMER".equalsIgnoreCase(type)) return updateCustomer(id, request);
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Account type must be STAFF or CUSTOMER");
    }
    private AccountResponse updateStaff(String id, AccountUpdateRequest request) {
        AdminUser user = staff.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Staff account not found"));
        if (request.getDisplayName() != null && !request.getDisplayName().isBlank()) user.setDisplayName(request.getDisplayName().trim());
        if (request.getEmail() != null && !request.getEmail().isBlank()) user.setEmail(request.getEmail().trim().toLowerCase());
        if (request.getRole() != null) { StaffRole role = StaffRole.from(request.getRole()); if (!role.name().equalsIgnoreCase(request.getRole())) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Choose Owner, Admin, Maintenance, or Service"); user.setRole(role.name()); }
        if (request.getActive() != null) user.setActive(request.getActive());
        return AccountResponse.from(staff.save(user));
    }
    private AccountResponse updateCustomer(String id, AccountUpdateRequest request) {
        Customer customer = customers.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer account not found"));
        if (request.getDisplayName() != null && !request.getDisplayName().isBlank()) customer.setFullName(request.getDisplayName().trim());
        if (request.getEmail() != null && !request.getEmail().isBlank()) customer.setEmail(request.getEmail().trim().toLowerCase());
        if (request.getPhone() != null && request.getPhone().replaceAll("\\s", "").matches("\\d{10}")) customer.setPhone(request.getPhone().replaceAll("\\s", ""));
        if (request.getActive() != null) customer.setActive(request.getActive());
        return AccountResponse.from(customers.save(customer));
    }
}
