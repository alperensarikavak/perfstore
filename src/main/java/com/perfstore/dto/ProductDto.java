package com.perfstore.dto;

import java.math.BigDecimal;
import java.util.UUID;

public class ProductDto {

    public static class CreateRequest {
        private String name;
        private String description;
        private BigDecimal price;
        private Integer stockQuantity;
        private String sku;
        private String imageUrl;
        private UUID categoryId;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public BigDecimal getPrice() {
            return price;
        }

        public void setPrice(BigDecimal price) {
            this.price = price;
        }

        public Integer getStockQuantity() {
            return stockQuantity;
        }

        public void setStockQuantity(Integer stockQuantity) {
            this.stockQuantity = stockQuantity;
        }

        public String getSku() {
            return sku;
        }

        public void setSku(String sku) {
            this.sku = sku;
        }
        public String getImageUrl() { return imageUrl; }
        public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

        public UUID getCategoryId() {
            return categoryId;
        }

        public void setCategoryId(UUID categoryId) {
            this.categoryId = categoryId;
        }
    }

    public static class UpdateRequest {
        private String name;
        private String description;
        private BigDecimal price;
        private Integer stockQuantity;
        private String imageUrl;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public BigDecimal getPrice() {
            return price;
        }

        public void setPrice(BigDecimal price) {
            this.price = price;
        }

        public Integer getStockQuantity() {
            return stockQuantity;
        }

        public void setStockQuantity(Integer stockQuantity) {
            this.stockQuantity = stockQuantity;
        }
        public String getImageUrl() { // Bunu ekle
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) { // Bunu ekle
            this.imageUrl = imageUrl;
        }
    }

    public static class Response {
        private UUID id;
        private String name;
        private String description;
        private BigDecimal price;
        private Integer stockQuantity;
        private String sku;
        private String imageUrl;
        private String categoryName;
        private UUID categoryId;

        public Response(UUID id, String name, String description, BigDecimal price, Integer stockQuantity, String sku,String imageUrl,
                String categoryName, UUID categoryId) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.price = price;
            this.stockQuantity = stockQuantity;
            this.sku = sku;
            this.categoryName = categoryName;
            this.imageUrl = imageUrl;
            this.categoryId = categoryId;
        }

        public UUID getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        public BigDecimal getPrice() {
            return price;
        }

        public Integer getStockQuantity() {
            return stockQuantity;
        }

        public String getSku() {
            return sku;
        }
        
        public String getImageUrl() {
            return imageUrl;
        }

        public String getCategoryName() {
            return categoryName;
        }

        public UUID getCategoryId() {
            return categoryId;
        }
    }
}
