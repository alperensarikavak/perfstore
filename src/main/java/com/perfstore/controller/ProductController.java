package com.perfstore.controller;

import com.perfstore.dto.ProductDto;
import com.perfstore.service.FileStorageService;
import com.perfstore.service.ProductService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "http://localhost:3000") // Frontend (Next.js) erişim izni
public class ProductController {

    private final ProductService productService;
    private final FileStorageService fileStorageService; // Yeni servis eklendi

    public ProductController(ProductService productService, FileStorageService fileStorageService) {
        this.productService = productService;
        this.fileStorageService = fileStorageService;
    }

    @GetMapping
    public ResponseEntity<List<ProductDto.Response>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDto.Response> getProduct(@PathVariable UUID id) {
        return ResponseEntity.ok(productService.getProduct(id));
    }

    // --- EN ÖNEMLİ DEĞİŞİKLİK BURADA ---
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductDto.Response> createProduct(
            @RequestPart("product") ProductDto.CreateRequest request, // JSON Verisi
            @RequestPart(value = "image", required = false) MultipartFile image // Resim Dosyası
    ) {
        // 1. Eğer resim gönderildiyse Cloudinary'e yükle
        if (image != null && !image.isEmpty()) {
            String cloudUrl = fileStorageService.saveFile(image);
            request.setImageUrl(cloudUrl); // Dönen URL'i DTO'ya ekle
        }

        // 2. Ürünü kaydet
        return ResponseEntity.ok(productService.createProduct(request));
    }
    // ------------------------------------

    @PutMapping("/{id}")
    public ResponseEntity<ProductDto.Response> updateProduct(@PathVariable UUID id,
            @RequestBody ProductDto.UpdateRequest request) {
        // Gelen sadece JSON ise önceki davranışı koru
        return ResponseEntity.ok(productService.updateProduct(id, request));
    }

    // Multipart ile resim güncellemeyi destekleyen PUT
    @PutMapping(value = "/{id}", consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductDto.Response> updateProductMultipart(@PathVariable UUID id,
            @RequestPart("product") ProductDto.UpdateRequest request,
            @RequestPart(value = "image", required = false) org.springframework.web.multipart.MultipartFile image) {

        if (image != null && !image.isEmpty()) {
            String cloudUrl = fileStorageService.saveFile(image);
            request.setImageUrl(cloudUrl);
        }

        return ResponseEntity.ok(productService.updateProduct(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable UUID id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}