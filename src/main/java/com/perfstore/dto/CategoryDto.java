package com.perfstore.dto;

import java.util.UUID;

public class CategoryDto {

    public static class CreateRequest {
        private String name;
        private String description;
        private UUID parentId;

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

        public UUID getParentId() {
            return parentId;
        }

        public void setParentId(UUID parentId) {
            this.parentId = parentId;
        }
    }

    public static class UpdateRequest {
        private String name;
        private String description;

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
    }

    public static class Response {
        private UUID id;
        private String name;
        private String description;
        private UUID parentId;

        public Response(UUID id, String name, String description, UUID parentId) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.parentId = parentId;
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

        public UUID getParentId() {
            return parentId;
        }
    }
}
