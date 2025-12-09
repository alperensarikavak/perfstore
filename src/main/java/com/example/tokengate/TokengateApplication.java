package com.example.tokengate;

import com.example.tokengate.domain.AccessToken;
import com.example.tokengate.repository.AccessTokenRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import com.example.tokengate.domain.SitePolicy;
import com.example.tokengate.repository.SitePolicyRepository;
import java.time.LocalDateTime;

@SpringBootApplication
public class TokengateApplication {

	public static void main(String[] args) {
		SpringApplication.run(TokengateApplication.class, args);
	}

	@Bean
	public CommandLineRunner dataInitializer(AccessTokenRepository accessTokenRepository,
			SitePolicyRepository sitePolicyRepository,
			com.example.tokengate.repository.ProductRepository productRepository) {
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

			// --- Demo Products ---
			if (productRepository.count() == 0) {
				productRepository.save(new com.example.tokengate.domain.Product(
						"High Performance Laptop",
						"The ultimate machine for professionals.",
						new java.math.BigDecimal("25000.00"),
						"https://images.unsplash.com/photo-1496181133206-80ce9b88a853?auto=format&fit=crop&w=500&q=60"));
				productRepository.save(new com.example.tokengate.domain.Product(
						"Wireless Gaming Mouse",
						"Precision and speed for gamers.",
						new java.math.BigDecimal("800.00"),
						"https://images.unsplash.com/photo-1527864550417-7fd91fc51a46?auto=format&fit=crop&w=500&q=60"));
				productRepository.save(new com.example.tokengate.domain.Product(
						"Mechanical Keyboard",
						"Tactile feedback for the best typing experience.",
						new java.math.BigDecimal("1500.00"),
						"https://images.unsplash.com/photo-1511467687858-23d96c32e4ae?auto=format&fit=crop&w=500&q=60"));
				System.out.println("Demo Products created.");
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
