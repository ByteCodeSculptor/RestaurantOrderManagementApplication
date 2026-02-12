package com.restaurants.demo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Email
    @Column(unique = true, nullable = false)
    private String email;

    @NotBlank
    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING) // Saves 'ADMIN'/'STAFF' as strings in MySQL ENUM
    @Column(nullable = false)
    private ERole role;

    public User(String email, String password, ERole role) {
        this.email = email;
        this.password = password;
        this.role = role;
    }
}