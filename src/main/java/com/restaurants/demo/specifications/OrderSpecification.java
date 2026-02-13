package com.restaurants.demo.specifications;

import com.restaurants.demo.entity.Order;
import com.restaurants.demo.util.OrderStatus;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

// Class with methods to return where clauses for each of the dynamic filters applied
public class OrderSpecification {

    // returns WHERE status = ?
    public static Specification<Order> hasStatus(OrderStatus status) {
        return (root, query, cb) ->
                status == null ? null : cb.equal(root.get("status"), status);
    }

    // returns WHERE tableNumber = ?
    public static Specification<Order> hasTableNumber(Integer tableNumber) {
        return (root, query, cb) ->
                tableNumber == null ? null : cb.equal(root.get("tableNumber"), tableNumber);
    }

    // returns WHERE startDate = ?
    public static Specification<Order> createdAfter(LocalDateTime startDate) {
        return (root, query, cb) ->
                startDate == null ? null : cb.greaterThanOrEqualTo(root.get("createdAt"), startDate);
    }

    // returns WHERE endDate = ?
    public static Specification<Order> createdBefore(LocalDateTime endDate) {
        return (root, query, cb) ->
                endDate == null ? null : cb.lessThanOrEqualTo(root.get("createdAt"), endDate);
    }
}