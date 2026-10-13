package com.dripgrow.api.service;

import com.dripgrow.api.dto.CreateOrderRequest;
import com.dripgrow.api.dto.OrderResponse;
import com.dripgrow.api.model.Order;
import com.dripgrow.api.model.OrderItem;
import com.dripgrow.api.model.Product;
import com.dripgrow.api.repository.OrderRepository;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductService productService;

    public OrderService(OrderRepository orderRepository, ProductService productService) {
        this.orderRepository = orderRepository;
        this.productService = productService;
    }

    public OrderResponse createCodOrder(CreateOrderRequest request) {
        String delivery = request.getDelivery() == null
                ? "standard"
                : request.getDelivery().toLowerCase(Locale.ROOT);
        if (!delivery.equals("standard") && !delivery.equals("express")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Delivery must be standard or express");
        }

        String phone = request.getAddress().getPhone().replaceAll("\\s", "");
        if (!phone.matches("\\d{10}")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Enter a valid 10-digit phone number");
        }
        if (!request.getAddress().getPincode().matches("\\d{6}")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Enter a valid 6-digit pincode");
        }
        request.getAddress().setPhone(phone);

        List<OrderItem> items = new ArrayList<>();
        double subtotal = 0;

        for (CreateOrderRequest.OrderLineRequest line : request.getItems()) {
            if (line.getQuantity() < 1) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Quantity must be at least 1");
            }
            Product product = productService.findById(line.getProductId());
            if (!product.isInStock()) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        product.getName() + " is out of stock"
                );
            }
            OrderItem item = new OrderItem();
            item.setProductId(product.getId());
            item.setName(product.getName());
            item.setImage(product.getImage());
            item.setPrice(product.getPrice());
            item.setQuantity(line.getQuantity());
            item.setFarmSize(line.getFarmSize());
            item.setCropType(line.getCropType());
            items.add(item);
            subtotal += product.getPrice() * line.getQuantity();
        }

        double deliveryFee = delivery.equals("express") ? 399 : (subtotal >= 5000 ? 0 : 199);

        Order order = new Order();
        order.setOrderNumber("DG-" + Long.toString(System.currentTimeMillis(), 36).toUpperCase(Locale.ROOT));
        order.setCreatedAt(Instant.now());
        order.setItems(items);
        order.setAddress(request.getAddress());
        order.setDelivery(delivery);
        order.setPaymentMethod("cod");
        order.setSubtotal(subtotal);
        order.setDeliveryFee(deliveryFee);
        order.setTotal(subtotal + deliveryFee);
        order.setStatus("Confirmed");

        return OrderResponse.from(orderRepository.save(order));
    }

    public List<OrderResponse> listOrders(String phone) {
        List<Order> orders;
        if (phone != null && !phone.isBlank()) {
            orders = orderRepository.findByAddressPhoneOrderByCreatedAtDesc(phone.replaceAll("\\s", ""));
        } else {
            orders = orderRepository.findAllByOrderByCreatedAtDesc();
        }
        return orders.stream().map(OrderResponse::from).toList();
    }

    public OrderResponse getByOrderNumber(String orderNumber) {
        return orderRepository.findByOrderNumber(orderNumber)
                .map(OrderResponse::from)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));
    }

    public OrderResponse updateOrder(String orderNumber, String status, String adminNotes) {
        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));
        if (status != null && !status.isBlank()) {
            Set<String> allowed = Set.of("Confirmed", "Accepted", "Shipped", "Delivered", "Cancelled");
            if (!allowed.contains(status)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid order status");
            }
            order.setStatus(status);
        }
        if (adminNotes != null) {
            order.setAdminNotes(adminNotes);
        }
        return OrderResponse.from(orderRepository.save(order));
    }
}
