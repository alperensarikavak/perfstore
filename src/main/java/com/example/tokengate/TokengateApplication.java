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
											 SitePolicyRepository sitePolicyRepository) {
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
		};
	}

	private void createSiteIfNotExists(SitePolicyRepository repo,
									   String domain,
									   String category,
									   boolean blockedByDefault) {
		repo.findByDomain(domain).ifPresentOrElse(
				existing -> {},
				() -> {
					SitePolicy policy = new SitePolicy();
					policy.setDomain(domain);
					policy.setCategory(category);
					policy.setBlockedByDefault(blockedByDefault);
					repo.save(policy);
					System.out.println("Demo SitePolicy created: " + domain + " -> " + category);
				}
		);
	}
}
