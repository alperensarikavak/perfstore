package com.example.tokengate.controller;

import com.example.tokengate.domain.Product;
import com.example.tokengate.repository.ProductRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductRepository productRepository;

    public ProductController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @GetMapping
    public List<Product> getAllProducts(@RequestParam(required = false) String query) {
        if (query != null && !query.isBlank()) {
            return productRepository.findByNameContainingIgnoreCase(query);
        }
        return productRepository.findAll();
    }
}
