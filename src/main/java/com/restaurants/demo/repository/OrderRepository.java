package com.restaurants.demo.repository;

import com.restaurants.demo.entity.Order;
import com.restaurants.demo.util.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {

    // For Daily Report
    List<Order> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
}