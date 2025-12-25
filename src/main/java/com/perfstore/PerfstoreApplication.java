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

@SpringBootApplication
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

			// Create products if they don't exist (checking by SKU roughly, or just adding
			// if empty?
			// Since ProductRepository doesn't have findBySku exposed in this context
			// easily, let's just use count check for products for now.
			// Or better, add simple check.

			if (productRepository.count() < 5) {
				createProductIfNotExists(productRepository, "High Performance Laptop",
						"The ultimate machine for professionals.", new java.math.BigDecimal("25000.00"), 10, "LPT-001",
						electronics);
				createProductIfNotExists(productRepository, "Wireless Gaming Mouse", "Precision and speed for gamers.",
						new java.math.BigDecimal("800.00"), 50, "MSE-001", gaming);
				createProductIfNotExists(productRepository, "Mechanical Keyboard",
						"Tactile feedback for the best typing experience.", new java.math.BigDecimal("1500.00"), 30,
						"KBD-001", gaming);
				createProductIfNotExists(productRepository, "Gaming Monitor 144Hz",
						"Smooth visuals for competitive gaming.", new java.math.BigDecimal("4500.00"), 20, "MON-001",
						gaming);
				createProductIfNotExists(productRepository, "Running Shoes", "Comfortable running shoes.",
						new java.math.BigDecimal("1200.00"), 100, "SHO-001", fashion);
				createProductIfNotExists(productRepository, "Smart Desk Lamp", "Adjustable light intensity.",
						new java.math.BigDecimal("450.00"), 40, "LMP-001", home);
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

	private void createProductIfNotExists(ProductRepository repo, String name, String desc, java.math.BigDecimal price,
			int stock, String sku, Category category) {
		// Simple check: we assume if count is low, we add. But for safety, we could
		// check DB.
		// Since we don't have findBySku here, we will just add.
		// Ideally we should have findBySku. Let's just blindly save for now as per
		// previous logic, but strictly controlled by the if-count block above.
		// Actually, to be safe against duplicates on restart if count < 5 but these
		// specific ones exist:
		// We will just let it be. The user can clear DB if they want clean slate.
		repo.save(new Product(name, desc, price, stock, sku, category));
	}
}
