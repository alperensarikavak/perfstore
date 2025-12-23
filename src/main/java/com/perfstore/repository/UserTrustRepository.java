package com.perfstore.repository;

import com.perfstore.trust.UserTrust;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserTrustRepository extends JpaRepository<UserTrust, Long> {
}
