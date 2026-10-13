package com.dripgrow.api.repository;

import com.dripgrow.api.model.Order;
import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface OrderRepository extends MongoRepository<Order, String> {

    Optional<Order> findByOrderNumber(String orderNumber);

    List<Order> findByAddressPhoneOrderByCreatedAtDesc(String phone);

    List<Order> findAllByOrderByCreatedAtDesc();
}
