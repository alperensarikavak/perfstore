package com.perfstore.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class OrderDto {

    public static class CreateRequest {
        private String shippingAddress;
        private String paymentMethod;
        private List<OrderItemRequest> items;

        public String getShippingAddress() {
            return shippingAddress;
        }

        public void setShippingAddress(String shippingAddress) {
            this.shippingAddress = shippingAddress;
        }

        public String getPaymentMethod() {
            return paymentMethod;
        }

        public void setPaymentMethod(String paymentMethod) {
            this.paymentMethod = paymentMethod;
        }

        public List<OrderItemRequest> getItems() {
            return items;
        }

        public void setItems(List<OrderItemRequest> items) {
            this.items = items;
        }
    }

    public static class OrderItemRequest {
        private UUID productId;
        private Integer quantity;

        public UUID getProductId() {
            return productId;
        }

        public void setProductId(UUID productId) {
            this.productId = productId;
        }

        public Integer getQuantity() {
            return quantity;
        }

        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }
    }

    public static class Response {
        private UUID id;
        private BigDecimal totalAmount;
        private LocalDateTime createdAt;
        private String status;
        private String shippingAddress;
        private String paymentMethod;
        private List<OrderItemResponse> items;

        public Response(UUID id, BigDecimal totalAmount, LocalDateTime createdAt, String shippingAddress,
                String paymentMethod, List<OrderItemResponse> items) {
            this.id = id;
            this.totalAmount = totalAmount;
            this.createdAt = createdAt;
            this.shippingAddress = shippingAddress;
            this.paymentMethod = paymentMethod;
            this.items = items;
        }

        public UUID getId() {
            return id;
        }

        public BigDecimal getTotalAmount() {
            return totalAmount;
        }

        public LocalDateTime getCreatedAt() {
            return createdAt;
        }

        public List<OrderItemResponse> getItems() {
            return items;
        }

        public String getShippingAddress() {
            return shippingAddress;
        }

        public String getPaymentMethod() {
            return paymentMethod;
        }
    }

    public static class OrderItemResponse {
        private String productName;
        private Integer quantity;
        private BigDecimal price;

        public OrderItemResponse(String productName, Integer quantity, BigDecimal price) {
            this.productName = productName;
            this.quantity = quantity;
            this.price = price;
        }

        public String getProductName() {
            return productName;
        }

        public Integer getQuantity() {
            return quantity;
        }

        public BigDecimal getPrice() {
            return price;
        }
    }
}
