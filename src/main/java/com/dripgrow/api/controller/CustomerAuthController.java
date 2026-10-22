package com.dripgrow.api.controller;

import com.dripgrow.api.dto.CustomerAuthRequest;
import com.dripgrow.api.dto.CustomerLoginResponse;
import com.dripgrow.api.dto.CustomerProfileResponse;
import com.dripgrow.api.model.Customer;
import com.dripgrow.api.repository.CustomerRepository;
import com.dripgrow.api.security.JwtService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/customer")
public class CustomerAuthController {
    private final CustomerRepository customers;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    public CustomerAuthController(CustomerRepository customers, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.customers = customers; this.passwordEncoder = passwordEncoder; this.jwtService = jwtService;
    }
    @PostMapping("/auth/register")
    public CustomerLoginResponse register(@Valid @RequestBody CustomerAuthRequest request) {
        if (request.getFullName() == null || request.getFullName().isBlank() || request.getEmail() == null || request.getEmail().isBlank()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Full name and email are required");
        if (customers.findByPhone(request.getPhone()).isPresent() || customers.findByEmailIgnoreCase(request.getEmail()).isPresent()) throw new ResponseStatusException(HttpStatus.CONFLICT, "An account already exists with this mobile number or email");
        Customer customer = new Customer(); customer.setFullName(request.getFullName().trim()); customer.setPhone(request.getPhone()); customer.setEmail(request.getEmail().trim().toLowerCase()); customer.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        return response(customers.save(customer));
    }
    @PostMapping("/auth/login")
    public CustomerLoginResponse login(@Valid @RequestBody CustomerAuthRequest request) {
        Customer customer = customers.findByPhone(request.getPhone()).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid mobile number or password"));
        if (!passwordEncoder.matches(request.getPassword(), customer.getPasswordHash())) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid mobile number or password");
        return response(customer);
    }
    @GetMapping("/me")
    public CustomerProfileResponse me(Authentication authentication) { return CustomerProfileResponse.from(customer(authentication)); }
    private Customer customer(Authentication authentication) { return customers.findById(authentication.getName().substring("customer:".length())).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Customer account not found")); }
    private CustomerLoginResponse response(Customer customer) { return new CustomerLoginResponse(jwtService.generateToken("customer:" + customer.getId()), jwtService.getExpirationMs(), CustomerProfileResponse.from(customer)); }
}
