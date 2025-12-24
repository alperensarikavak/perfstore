package com.perfstore.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class OrderDto {

    public static class CreateRequest {
        private List<OrderItemRequest> items;

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
        private String status; // For now simplified, could be enum
        private List<OrderItemResponse> items;

        public Response(UUID id, BigDecimal totalAmount, LocalDateTime createdAt, List<OrderItemResponse> items) {
            this.id = id;
            this.totalAmount = totalAmount;
            this.createdAt = createdAt;
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
