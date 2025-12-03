package com.example.tokengate.domain;

import jakarta.persistence.*;

@Entity
public class SitePolicy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Örn: "adult-site.com"
    @Column(nullable = false, unique = true)
    private String domain;

    // Örn: "GENERAL" / "ADULT"
    @Column(nullable = false)
    private String category;

    private boolean blockedByDefault = true;

    // getter / setter

    public Long getId() {
        return id;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public boolean isBlockedByDefault() {
        return blockedByDefault;
    }

    public void setBlockedByDefault(boolean blockedByDefault) {
        this.blockedByDefault = blockedByDefault;
    }
}
