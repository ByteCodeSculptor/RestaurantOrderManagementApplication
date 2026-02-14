package com.restaurants.demo.service;

import com.restaurants.demo.dto.request.OrderItemRequest;
import com.restaurants.demo.dto.request.OrderRequest;
import com.restaurants.demo.dto.response.DailyReportResponse;
import com.restaurants.demo.dto.response.OrderItemResponse;
import com.restaurants.demo.dto.response.OrderResponse;
import com.restaurants.demo.entity.MenuItem;
import com.restaurants.demo.entity.Order;
import com.restaurants.demo.entity.OrderItem;
import com.restaurants.demo.mapper.OrderMapper;
import com.restaurants.demo.repository.MenuItemRepository;
import com.restaurants.demo.repository.OrderRepository;
import com.restaurants.demo.specification.OrderSpecification;
import com.restaurants.demo.util.OrderStatus;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.aspectj.weaver.ast.Or;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

//Entire business logic of order management lies here
@Service // Required annotation for spring to handle this Java class
@RequiredArgsConstructor // Generates a constructor that initializes all fields marked as final
public class OrderService {
    private final OrderRepository orderRepository;

    private final MenuItemRepository menuItemRepository;

    public Order createOrder (OrderRequest orderRequest) {
        Order order = new Order ();
        order.setTableNumber (orderRequest.getTableNumber());
        order.setStatus (OrderStatus.PLACED);

        List<OrderItem> items = new ArrayList<>();

        BigDecimal total = BigDecimal.ZERO; // Initial value set to Zero

        // We have to map fields of orderItemRequest with the OrderItem entity to store in DB
        for (OrderItemRequest item : orderRequest.getItems()) {
            MenuItem menuItem = menuItemRepository.findById (item.getMenuItemId())
                    .orElseThrow(() -> new ResponseStatusException (HttpStatus.NOT_FOUND, "Menu item not found"));

            if (!menuItem.isAvailable()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Item not available!");
            }

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setPriceAtOrderTime(menuItem.getPrice());
            orderItem.setQuantity(item.getQuantity());
            orderItem.setMenuItemName(menuItem.getName());
            orderItem.setMenuItem(menuItem);

            // Subtotal of each item is quantity x price of that item
            BigDecimal subtotal = menuItem.getPrice().multiply(BigDecimal.valueOf(orderItem.getQuantity()));

            orderItem.setSubtotal(subtotal);

            // updating the items list
            items.add(orderItem);

            // updating the total amount for this order
            total = total.add (subtotal);
        }

        order.setItems(items);
        order.setTotalAmount(total);

        return orderRepository.save(order);
    }

    public Page<Order> getOrders (OrderStatus status,
                                  Integer tableNumber,
                                  LocalDate startDate,
                                  LocalDate endDate,
                                  Pageable pageable) {
        // All filters must be satisfied and specification resemble WHERE queries in SQL
        // and all there queries must be fullfilled
        Specification<Order> spec = Specification.allOf(
                OrderSpecification.hasStatus(status),
                OrderSpecification.hasTableNumber(tableNumber),
                OrderSpecification.createdAfterOrEqual(startDate),
                OrderSpecification.createdBefore(endDate)
        );

        return orderRepository.findAll(spec, pageable);
    }

    public Order getOrderById (Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException (HttpStatus.NOT_FOUND, "Order not found!"));
    }

    public Order updateOrderStatus (Long orderId, OrderStatus status) {
        Order order = this.getOrderById(orderId);
        OrderStatus currentStatus = order.getStatus();

        // Checking if transition is valid or not
        boolean isValidTransition = switch (currentStatus) {
            case PLACED -> status == OrderStatus.PREPARING || status == OrderStatus.CANCELLED;
            case PREPARING -> status == OrderStatus.READY;
            case READY -> status == OrderStatus.SERVED;
            case SERVED, CANCELLED -> false;
        };

        if (!isValidTransition) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid status transition!");
        }

        order.setStatus(status);
        return orderRepository.save(order);
    }

    public DailyReportResponse getDailyReport () {
        LocalDateTime startOfToday = LocalDate.now ().atStartOfDay();
        LocalDateTime endOfToday = LocalDate.now ().atTime(LocalTime.MAX);

        List<Order> orders = orderRepository.findByCreatedAtBetween(startOfToday, endOfToday);

        BigDecimal totalRevenue = BigDecimal.ZERO;

        for (Order order : orders) {
            totalRevenue = totalRevenue.add(order.getTotalAmount());
        }

        return new DailyReportResponse (totalRevenue, (long) orders.size ());
    }
}
