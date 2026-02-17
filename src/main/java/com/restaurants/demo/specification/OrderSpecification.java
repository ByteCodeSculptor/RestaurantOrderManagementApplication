package com.restaurants.demo.specification;

import com.restaurants.demo.entity.Order;
import com.restaurants.demo.util.OrderStatus;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
    This class defines JPA Specifications for building dynamic queries.
    Specifications to add optional WHERE conditions and
    combine them using AND without writing SQL queries.
 */
public class OrderSpecification {

    public static Specification<Order> hasStatus(OrderStatus status) {
        return (root, query, cb) ->
                status == null ? null : cb.equal(root.get("status"), status);
    }

    public static Specification<Order> hasTableNumber(Integer tableNumber) {
        return (root, query, cb) ->
                tableNumber == null ? null : cb.equal(root.get("tableNumber"), tableNumber);
    }

    public static Specification<Order> createdAfterOrEqual(LocalDate startDate) {
        return (root, query, cb) -> {
            if (startDate == null) return null;
            LocalDateTime startDateTime = startDate.atStartOfDay();
            return cb.greaterThanOrEqualTo(root.get("createdAt"), startDateTime);
        };
    }

    public static Specification<Order> createdBefore(LocalDate endDate) {
        return (root, query, cb) -> {
            if (endDate == null) return null;
            LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
            return cb.lessThanOrEqualTo(root.get("createdAt"), endDateTime);
        };
    }
}