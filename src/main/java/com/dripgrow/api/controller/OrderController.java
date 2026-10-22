package com.dripgrow.api.controller;

import com.dripgrow.api.dto.CreateOrderRequest;
import com.dripgrow.api.dto.OrderResponse;
import com.dripgrow.api.service.OrderService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse create(@Valid @RequestBody CreateOrderRequest request, Authentication authentication) {
        String customerId = authentication != null && authentication.getName().startsWith("customer:")
                ? authentication.getName().substring("customer:".length()) : null;
        return orderService.createCodOrder(request, customerId);
    }

    @GetMapping
    public List<OrderResponse> list(@RequestParam(required = false) String phone) {
        return orderService.listOrders(phone);
    }

    @GetMapping("/{orderNumber}")
    public OrderResponse get(@PathVariable String orderNumber) {
        return orderService.getByOrderNumber(orderNumber);
    }
}
