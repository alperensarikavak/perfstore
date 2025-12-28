package com.perfstore.repository;

import com.perfstore.domain.Category;
import com.perfstore.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {
    List<Product> findAllByCategory(Category category);

    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.data.jpa.repository.Query("UPDATE Product p SET p.stockQuantity = p.stockQuantity - :quantity WHERE p.id = :id AND p.stockQuantity >= :quantity")
    int decreaseStock(@org.springframework.data.repository.query.Param("id") UUID id,
            @org.springframework.data.repository.query.Param("quantity") int quantity);

    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.data.jpa.repository.Query("UPDATE Product p SET p.stockQuantity = p.stockQuantity + :quantity WHERE p.id = :id")
    void increaseStock(@org.springframework.data.repository.query.Param("id") UUID id,
            @org.springframework.data.repository.query.Param("quantity") int quantity);
}
