package com.perfstore.service;

import com.perfstore.domain.Category;
import com.perfstore.domain.Product;
import com.perfstore.dto.ProductDto;
import com.perfstore.repository.CategoryRepository;
import com.perfstore.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    public List<ProductDto.Response> getAllProducts() {
        return productRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public ProductDto.Response getProduct(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        return mapToResponse(product);
    }

    public ProductDto.Response createProduct(ProductDto.CreateRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        // GÜNCELLEME: Constructor'a request.getImageUrl() eklendi
        Product product = new Product(
                request.getName(),
                request.getDescription(),
                request.getPrice(),
                request.getStockQuantity(),
                request.getSku(),
                request.getImageUrl(), // <-- YENİ: Resim URL'i buraya
                category);

        Product saved = productRepository.save(product);
        return mapToResponse(saved);
    }

    public ProductDto.Response updateProduct(UUID id, ProductDto.UpdateRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStockQuantity(request.getStockQuantity());
        
        // GÜNCELLEME: Eğer request'te resim linki varsa güncelle
        if (request.getImageUrl() != null && !request.getImageUrl().isEmpty()) {
            product.setImageUrl(request.getImageUrl());
        }

        Product saved = productRepository.save(product);
        return mapToResponse(saved);
    }

    public void deleteProduct(UUID id) {
        productRepository.deleteById(id);
    }

    private ProductDto.Response mapToResponse(Product product) {
        // GÜNCELLEME: Response içine imageUrl eklendi
        return new ProductDto.Response(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStockQuantity(),
                product.getSku(),
                product.getImageUrl(), // <-- YENİ: Frontend'e resim linkini dönüyoruz
                product.getCategory().getName(),
                product.getCategory().getId());
    }
}