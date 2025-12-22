package com.perfstore;

import com.perfstore.domain.AccessToken;
import com.perfstore.repository.AccessTokenRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import com.perfstore.domain.SitePolicy;
import com.perfstore.repository.SitePolicyRepository;
import java.time.LocalDateTime;

@SpringBootApplication
public class PerfstoreApplication {

	public static void main(String[] args) {
		SpringApplication.run(PerfstoreApplication.class, args);
	}

	@Bean
	public CommandLineRunner dataInitializer(AccessTokenRepository accessTokenRepository,
			SitePolicyRepository sitePolicyRepository,
			com.perfstore.repository.ProductRepository productRepository,
			com.perfstore.repository.CategoryRepository categoryRepository) {
		return args -> {
			// --- Demo AccessToken ---
			String demoTokenValue = "PERFECT-TOKEN-DB";

			boolean exists = accessTokenRepository
					.findByTokenValue(demoTokenValue)
					.isPresent();

			if (!exists) {
				AccessToken token = new AccessToken();
				token.setTokenValue(demoTokenValue);
				token.setOwnerName("DemoUser");
				token.setAllowedCategory("ADULT"); // ADULT her yeri görebilsin
				token.setExpiresAt(LocalDateTime.now().plusDays(7));
				token.setMaxUsageCount(100);
				token.setUsedCount(0);

				accessTokenRepository.save(token);
				System.out.println("Demo AccessToken created: " + demoTokenValue);
			}

			// --- Demo SitePolicy kayıtları ---

			// GENERAL site
			createSiteIfNotExists(sitePolicyRepository,
					"google.com", "GENERAL", false);

			// ADULT site (kısıtlı)
			createSiteIfNotExists(sitePolicyRepository,
					"adult-site.com", "ADULT", true);

			// INTERNAL site (örnek)
			createSiteIfNotExists(sitePolicyRepository,
					"internal.company.com", "INTERNAL", true);

			// --- Demo Products & Categories ---
			if (categoryRepository.count() == 0) {
				com.perfstore.domain.Category electronics = new com.perfstore.domain.Category("Electronics",
						"Gadgets and devices");
				categoryRepository.save(electronics);

				if (productRepository.count() == 0) {
					productRepository.save(new com.perfstore.domain.Product(
							"High Performance Laptop",
							"The ultimate machine for professionals.",
							new java.math.BigDecimal("25000.00"),
							10, "LPT-001", electronics));
					productRepository.save(new com.perfstore.domain.Product(
							"Wireless Gaming Mouse",
							"Precision and speed for gamers.",
							new java.math.BigDecimal("800.00"),
							50, "MSE-001", electronics));
					productRepository.save(new com.perfstore.domain.Product(
							"Mechanical Keyboard",
							"Tactile feedback for the best typing experience.",
							new java.math.BigDecimal("1500.00"),
							30, "KBD-001", electronics));
					System.out.println("Demo Products created.");
				}
			}
		};
	}

	private void createSiteIfNotExists(SitePolicyRepository repo,
			String domain,
			String category,
			boolean blockedByDefault) {
		repo.findByDomain(domain).ifPresentOrElse(
				existing -> {
				},
				() -> {
					SitePolicy policy = new SitePolicy();
					policy.setDomain(domain);
					policy.setCategory(category);
					policy.setBlockedByDefault(blockedByDefault);
					repo.save(policy);
					System.out.println("Demo SitePolicy created: " + domain + " -> " + category);
				});
	}
}
