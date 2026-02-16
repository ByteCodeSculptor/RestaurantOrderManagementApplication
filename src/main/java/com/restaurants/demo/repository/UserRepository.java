package com.restaurants.demo.repository;

import com.restaurants.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;



/*
    UserRepository is a Spring Data JPA repository interface for managing User entities. 
    It extends JpaRepository, providing basic CRUD operations, and includes custom methods to find a user by email and check if an email already exists in the database.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Boolean existsByEmail(String email);
}