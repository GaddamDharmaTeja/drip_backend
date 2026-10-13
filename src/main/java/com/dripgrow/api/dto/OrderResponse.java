package com.dripgrow.api.dto;

import com.dripgrow.api.model.Address;
import com.dripgrow.api.model.Order;
import com.dripgrow.api.model.OrderItem;
import java.time.Instant;
import java.util.List;

/** Frontend-friendly shape (id = orderNumber). */
public class OrderResponse {

    private String id;
    private String mongoId;
    private Instant createdAt;
    private List<OrderItem> items;
    private Address address;
    private String delivery;
    private String paymentMethod;
    private double subtotal;
    private double deliveryFee;
    private double total;
    private String status;
    private String adminNotes;

    public static OrderResponse from(Order order) {
        OrderResponse r = new OrderResponse();
        r.id = order.getOrderNumber();
        r.mongoId = order.getId();
        r.createdAt = order.getCreatedAt();
        r.items = order.getItems();
        r.address = order.getAddress();
        r.delivery = order.getDelivery();
        r.paymentMethod = order.getPaymentMethod();
        r.subtotal = order.getSubtotal();
        r.deliveryFee = order.getDeliveryFee();
        r.total = order.getTotal();
        r.status = order.getStatus();
        r.adminNotes = order.getAdminNotes();
        return r;
    }

    public String getId() {
        return id;
    }

    public String getMongoId() {
        return mongoId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public Address getAddress() {
        return address;
    }

    public String getDelivery() {
        return delivery;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public double getDeliveryFee() {
        return deliveryFee;
    }

    public double getTotal() {
        return total;
    }

    public String getStatus() {
        return status;
    }

    public String getAdminNotes() {
        return adminNotes;
    }
}
