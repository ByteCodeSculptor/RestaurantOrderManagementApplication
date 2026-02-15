package com.restaurants.demo.repository;

import com.restaurants.demo.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDateTime;
import java.util.List;


/*
    OrderRepository is a Spring Data JPA repository interface for managing Order entities. 
    It extends JpaRepository, providing basic CRUD operations, and JpaSpecificationExecutor for advanced querying capabilities.
 */
public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {
    List<Order> findByCreatedAtBetween (LocalDateTime startTime, LocalDateTime endTime);
}