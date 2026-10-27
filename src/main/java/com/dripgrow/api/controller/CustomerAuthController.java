package com.dripgrow.api.controller;

import com.dripgrow.api.dto.CustomerAuthRequest;
import com.dripgrow.api.dto.CustomerLoginResponse;
import com.dripgrow.api.dto.CustomerProfileResponse;
import com.dripgrow.api.dto.CustomerProfileUpdateRequest;
import com.dripgrow.api.dto.CustomerAddressRequest;
import com.dripgrow.api.model.Customer;
import com.dripgrow.api.model.CustomerAddress;
import com.dripgrow.api.repository.CustomerRepository;
import com.dripgrow.api.security.JwtService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
        if (!customer.isActive()) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "This customer account has been suspended");
        return response(customer);
    }
    @GetMapping("/me")
    public CustomerProfileResponse me(Authentication authentication) { return CustomerProfileResponse.from(customer(authentication)); }

    @PutMapping("/me")
    public CustomerProfileResponse updateProfile(Authentication authentication, @Valid @RequestBody CustomerProfileUpdateRequest request) {
        Customer customer = customer(authentication);
        String phone = request.getPhone().replaceAll("\\s", "");
        if (!phone.equals(customer.getPhone()) && customers.findByPhone(phone).isPresent()) throw new ResponseStatusException(HttpStatus.CONFLICT, "That mobile number is already in use");
        if (!request.getEmail().equalsIgnoreCase(customer.getEmail()) && customers.findByEmailIgnoreCase(request.getEmail()).isPresent()) throw new ResponseStatusException(HttpStatus.CONFLICT, "That email address is already in use");
        customer.setFullName(request.getFullName().trim()); customer.setPhone(phone); customer.setEmail(request.getEmail().trim().toLowerCase());
        return CustomerProfileResponse.from(customers.save(customer));
    }

    @PostMapping("/addresses")
    public CustomerProfileResponse addAddress(Authentication authentication, @Valid @RequestBody CustomerAddressRequest request) {
        Customer customer = customer(authentication);
        CustomerAddress address = apply(new CustomerAddress(), request);
        customer.getAddresses().add(address);
        if (customer.getDefaultAddressId() == null) customer.setDefaultAddressId(address.getId());
        return CustomerProfileResponse.from(customers.save(customer));
    }

    @PutMapping("/addresses/{id}")
    public CustomerProfileResponse updateAddress(Authentication authentication, @PathVariable String id, @Valid @RequestBody CustomerAddressRequest request) {
        Customer customer = customer(authentication);
        CustomerAddress address = address(customer, id);
        apply(address, request);
        return CustomerProfileResponse.from(customers.save(customer));
    }

    @PostMapping("/addresses/{id}/default")
    public CustomerProfileResponse setDefaultAddress(Authentication authentication, @PathVariable String id) {
        Customer customer = customer(authentication); address(customer, id); customer.setDefaultAddressId(id);
        return CustomerProfileResponse.from(customers.save(customer));
    }

    @DeleteMapping("/addresses/{id}")
    public CustomerProfileResponse deleteAddress(Authentication authentication, @PathVariable String id) {
        Customer customer = customer(authentication);
        if (id.equals(customer.getDefaultAddressId())) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Choose another default address before deleting this one");
        if (!customer.getAddresses().removeIf(address -> id.equals(address.getId()))) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Address not found");
        return CustomerProfileResponse.from(customers.save(customer));
    }
    private Customer customer(Authentication authentication) { return customers.findById(authentication.getName().substring("customer:".length())).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Customer account not found")); }
    private CustomerAddress address(Customer customer, String id) { return customer.getAddresses().stream().filter(address -> id.equals(address.getId())).findFirst().orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Address not found")); }
    private CustomerAddress apply(CustomerAddress address, CustomerAddressRequest request) {
        address.setLabel(request.getLabel() == null || request.getLabel().isBlank() ? "Farm address" : request.getLabel().trim());
        address.setFullName(request.getFullName().trim()); address.setPhone(indianMobile(request.getPhone())); address.setEmail(request.getEmail() == null ? "" : request.getEmail().trim());
        address.setLine1(request.getLine1().trim()); address.setLine2(request.getLine2() == null ? "" : request.getLine2().trim()); address.setCity(request.getCity().trim()); address.setState(request.getState().trim()); address.setPincode(request.getPincode().trim());
        return address;
    }
    private String indianMobile(String phone) {
        String digits = phone.replaceAll("\\D", "");
        return digits.length() == 12 && digits.startsWith("91") ? digits.substring(2) : digits;
    }
    private CustomerLoginResponse response(Customer customer) { return new CustomerLoginResponse(jwtService.generateToken("customer:" + customer.getId()), jwtService.getExpirationMs(), CustomerProfileResponse.from(customer)); }
}
