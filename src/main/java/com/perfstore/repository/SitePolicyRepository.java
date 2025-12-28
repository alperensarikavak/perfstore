package com.perfstore.repository;

import com.perfstore.domain.SitePolicy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SitePolicyRepository extends JpaRepository<SitePolicy, Long> {

    Optional<SitePolicy> findByDomain(String domain);
}
