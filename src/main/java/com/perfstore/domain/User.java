package com.perfstore.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users") // 'user' is a reserved keyword in some DBs
@Getter
@Setter
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    private String password;

    private String email;

    // One User has One AccessToken (for this simple scenario)
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "access_token_id")
    private AccessToken accessToken;

    public User(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }
}
