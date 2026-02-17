package com.restaurants.demo.repository;

import com.restaurants.demo.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;


/*
    OrderRepository is a Spring Data JPA repository interface for managing Order entities.
    It extends JpaRepository, providing basic CRUD operations, and JpaSpecificationExecutor for advanced querying capabilities.
 */
public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {
    @Query("SELECT COALESCE(SUM(o.totalAmountInCents), 0) FROM Order o WHERE o.createdAt BETWEEN :start AND :end")
    Long sumTotalAmountBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    Long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
}