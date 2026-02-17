package com.restaurants.demo.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;


/*
    User is an entity class that represents a user in the restaurant order management system. 
*/

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

    @Column(name = "failed_attempts", nullable = false)
    private int failedAttempts = 0;

    @Column(name = "account_non_locked", nullable = false)
    private boolean accountNonLocked = true;

    @Column(name = "lockout_time")
    private LocalDateTime lockoutTime;

    public User(String email, String password, ERole role , int failedAttempts, boolean accountNonLocked, LocalDateTime lockoutTime) {
        this.email = email;
        this.password = password;
        this.role = role;
        this.failedAttempts = failedAttempts;
        this.accountNonLocked = accountNonLocked;
        this.lockoutTime = lockoutTime;
    }
}