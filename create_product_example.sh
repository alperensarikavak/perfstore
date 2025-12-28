curl -X POST http://localhost:8080/api/products \
-H "Content-Type: application/json" \
-H "Authorization: Bearer PERFECT-TOKEN-DB" \
-d "{ \"name\": \"Sony XM5 Headphones\", \"description\": \"Best noise cancelling\", \"price\": 12000.00, \"stockQuantity\": 25, \"sku\": \"SNY-XM5\", \"categoryId\": \"f65e7e7f-64fc-41a8-b1a1-639289e3188e\" }"
