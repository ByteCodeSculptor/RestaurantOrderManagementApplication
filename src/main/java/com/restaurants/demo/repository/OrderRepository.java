package com.restaurants.demo.repository;

import com.restaurants.demo.entity.Order;
import com.restaurants.demo.util.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    // For Daily Report
    List<Order> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    // For List Orders (Filtering)
    Page<Order> findByStatus(OrderStatus status, Pageable pageable);
    Page<Order> findByTableNumber(Integer tableNumber, Pageable pageable);
    Page<Order> findByStatusAndTableNumber(OrderStatus status, Integer tableNumber, Pageable pageable);
}