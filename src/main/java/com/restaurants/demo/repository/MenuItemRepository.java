package com.restaurants.demo.repository;

import com.restaurants.demo.entity.MenuItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


/*
    MenuItemRepository is a Spring Data JPA repository interface for managing MenuItem entities.
    It extends JpaRepository, providing basic CRUD operations, and includes a custom method to find menu items
    based on their availability status with pagination support.
*/

public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {
    Page<MenuItem> findByAvailable(Boolean available, Pageable pageable);
}
