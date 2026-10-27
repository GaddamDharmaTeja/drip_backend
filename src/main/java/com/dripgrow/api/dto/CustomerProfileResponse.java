package com.dripgrow.api.dto;

import com.dripgrow.api.model.Customer;
import com.dripgrow.api.model.CustomerAddress;
import java.util.List;

public record CustomerProfileResponse(String id, String fullName, String phone, String email, List<CustomerAddress> addresses, String defaultAddressId) {
    public static CustomerProfileResponse from(Customer customer) {
        return new CustomerProfileResponse(customer.getId(), customer.getFullName(), customer.getPhone(), customer.getEmail(), customer.getAddresses(), customer.getDefaultAddressId());
    }
}
