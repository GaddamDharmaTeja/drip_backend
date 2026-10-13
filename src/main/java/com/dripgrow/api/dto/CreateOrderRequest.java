package com.dripgrow.api.dto;

import com.dripgrow.api.model.Address;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public class CreateOrderRequest {

    @NotEmpty
    private List<OrderLineRequest> items;

    @Valid
    @NotNull
    private Address address;

    @NotBlank
    private String delivery;

    public List<OrderLineRequest> getItems() {
        return items;
    }

    public void setItems(List<OrderLineRequest> items) {
        this.items = items;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getDelivery() {
        return delivery;
    }

    public void setDelivery(String delivery) {
        this.delivery = delivery;
    }

    public static class OrderLineRequest {
        @NotBlank
        private String productId;
        private int quantity = 1;
        private String farmSize;
        private String cropType;

        public String getProductId() {
            return productId;
        }

        public void setProductId(String productId) {
            this.productId = productId;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        public String getFarmSize() {
            return farmSize;
        }

        public void setFarmSize(String farmSize) {
            this.farmSize = farmSize;
        }

        public String getCropType() {
            return cropType;
        }

        public void setCropType(String cropType) {
            this.cropType = cropType;
        }
    }
}
