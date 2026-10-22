package com.dripgrow.api.dto;

import com.dripgrow.api.model.Customer;

public record CustomerProfileResponse(String id, String fullName, String phone, String email) {
    public static CustomerProfileResponse from(Customer customer) {
        return new CustomerProfileResponse(customer.getId(), customer.getFullName(), customer.getPhone(), customer.getEmail());
    }
}
