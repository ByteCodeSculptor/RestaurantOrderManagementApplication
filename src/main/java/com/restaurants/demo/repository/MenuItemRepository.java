package com.restaurants.demo.repository;

import com.restaurants.demo.entity.MenuItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {
    Page<MenuItem> findByAvailable(Boolean available, Pageable pageable);
}
