package com.perfstore;

import com.perfstore.domain.AccessToken;
import com.perfstore.domain.Category;
import com.perfstore.domain.Product;
import com.perfstore.domain.SitePolicy;
import com.perfstore.repository.AccessTokenRepository;
import com.perfstore.repository.CategoryRepository;
import com.perfstore.repository.ProductRepository;
import com.perfstore.repository.SitePolicyRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDateTime;

import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PerfstoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(PerfstoreApplication.class, args);
    }

    @Bean
    public CommandLineRunner dataInitializer(AccessTokenRepository accessTokenRepository,
            SitePolicyRepository sitePolicyRepository,
            ProductRepository productRepository,
            CategoryRepository categoryRepository) {
        return args -> {
            // --- Demo AccessToken ---
            String demoTokenValue = "PERFECT-TOKEN-DB";
            if (accessTokenRepository.findByTokenValue(demoTokenValue).isEmpty()) {
                AccessToken token = new AccessToken();
                token.setTokenValue(demoTokenValue);
                token.setOwnerName("DemoUser");
                token.setAllowedCategory("ADULT");
                token.setExpiresAt(LocalDateTime.now().plusDays(7));
                token.setMaxUsageCount(100);
                accessTokenRepository.save(token);
                System.out.println("Demo AccessToken created.");
            }

            // --- Demo SitePolicies ---
            createSiteIfNotExists(sitePolicyRepository, "google.com", "GENERAL", false);
            createSiteIfNotExists(sitePolicyRepository, "adult-site.com", "ADULT", true);
            createSiteIfNotExists(sitePolicyRepository, "internal.company.com", "INTERNAL", true);

            // --- Demo Categories & Products ---
            Category electronics = createCategoryIfNotExists(categoryRepository, "Electronics", "Gadgets and devices");
            Category gaming = createCategoryIfNotExists(categoryRepository, "Gaming", "Consoles and Accessories");
            Category fashion = createCategoryIfNotExists(categoryRepository, "Fashion", "Clothing and styles");
            Category home = createCategoryIfNotExists(categoryRepository, "Home", "Furniture and decor");

            if (productRepository.count() < 5) {
                // GÜNCELLEME: Araya 'null' (imageUrl) eklendi
                createProductIfNotExists(productRepository, "High Performance Laptop",
                        "The ultimate machine for professionals.", new java.math.BigDecimal("25000.00"), 10, "LPT-001",
                        null, electronics); // <-- null eklendi

                createProductIfNotExists(productRepository, "Wireless Gaming Mouse", "Precision and speed for gamers.",
                        new java.math.BigDecimal("800.00"), 50, "MSE-001", 
                        null, gaming); // <-- null eklendi

                createProductIfNotExists(productRepository, "Mechanical Keyboard",
                        "Tactile feedback for the best typing experience.", new java.math.BigDecimal("1500.00"), 30,
                        "KBD-001", 
                        null, gaming); // <-- null eklendi

                createProductIfNotExists(productRepository, "Gaming Monitor 144Hz",
                        "Smooth visuals for competitive gaming.", new java.math.BigDecimal("4500.00"), 20, "MON-001",
                        null, gaming); // <-- null eklendi

                createProductIfNotExists(productRepository, "Running Shoes", "Comfortable running shoes.",
                        new java.math.BigDecimal("1200.00"), 100, "SHO-001", 
                        null, fashion); // <-- null eklendi

                createProductIfNotExists(productRepository, "Smart Desk Lamp", "Adjustable light intensity.",
                        new java.math.BigDecimal("450.00"), 40, "LMP-001", 
                        null, home); // <-- null eklendi
            }
        };
    }

    private void createSiteIfNotExists(SitePolicyRepository repo, String domain, String category, boolean blocked) {
        if (repo.findByDomain(domain).isEmpty()) {
            SitePolicy policy = new SitePolicy();
            policy.setDomain(domain);
            policy.setCategory(category);
            policy.setBlockedByDefault(blocked);
            repo.save(policy);
        }
    }

    private Category createCategoryIfNotExists(CategoryRepository repo, String name, String desc) {
        return repo.findByName(name).orElseGet(() -> repo.save(new Category(name, desc)));
    }

    // GÜNCELLEME: Metod imzasına 'String imageUrl' eklendi
    private void createProductIfNotExists(ProductRepository repo, String name, String desc, java.math.BigDecimal price,
            int stock, String sku, String imageUrl, Category category) {
        
        // GÜNCELLEME: new Product(...) çağrısına 'imageUrl' eklendi
        repo.save(new Product(name, desc, price, stock, sku, imageUrl, category));
    }
}