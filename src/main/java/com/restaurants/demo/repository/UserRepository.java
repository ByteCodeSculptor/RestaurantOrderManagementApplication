package com.restaurants.demo.repository;

import com.restaurants.demo.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;



/*
    UserRepository is a Spring Data JPA repository interface for managing User entities. 
    It extends JpaRepository, providing basic CRUD operations, and includes custom methods to find a user by email and check if an email already exists in the database.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Boolean existsByEmail(String email);

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.accountNonLocked = true, u.failedAttempts = 0, u.lockoutTime = NULL WHERE u.accountNonLocked = false AND u.lockoutTime < :now")
    void unlockExpiredAccounts(LocalDateTime now);
}