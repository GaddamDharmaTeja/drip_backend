package com.dripgrow.api.controller;

import com.dripgrow.api.dto.OrderResponse;
import com.dripgrow.api.service.OrderService;
import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/customer/orders")
public class CustomerOrderController {
    private final OrderService orders;
    public CustomerOrderController(OrderService orders) { this.orders = orders; }
    @GetMapping
    public List<OrderResponse> list(Authentication authentication) {
        return orders.listCustomerOrders(authentication.getName().substring("customer:".length()));
    }
}
