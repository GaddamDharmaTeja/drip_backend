package com.dripgrow.api.repository;

import com.dripgrow.api.model.Customer;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CustomerRepository extends MongoRepository<Customer, String> {
    Optional<Customer> findByPhone(String phone);
    Optional<Customer> findByEmailIgnoreCase(String email);
}
