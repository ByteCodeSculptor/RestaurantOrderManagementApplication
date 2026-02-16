package com.restaurants.demo.service.impl;

import com.restaurants.demo.dto.request.OrderItemRequest;
import com.restaurants.demo.dto.request.OrderRequest;
import com.restaurants.demo.dto.response.DailyReportResponse;
import com.restaurants.demo.entity.MenuItem;
import com.restaurants.demo.entity.Order;
import com.restaurants.demo.entity.OrderItem;
import com.restaurants.demo.repository.MenuItemRepository;
import com.restaurants.demo.repository.OrderRepository;
import com.restaurants.demo.service.OrderService;
import com.restaurants.demo.specification.OrderSpecification;
import com.restaurants.demo.util.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final MenuItemRepository menuItemRepository;

    @Override
    public Order createOrder(OrderRequest orderRequest) {
        Order order = new Order();
        order.setTableNumber(orderRequest.getTableNumber());
        order.setStatus(OrderStatus.PLACED);

        List<OrderItem> items = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        // Map fields of orderItemRequest with the OrderItem entity
        for (OrderItemRequest item : orderRequest.getItems()) {
            MenuItem menuItem = menuItemRepository.findById(item.getMenuItemId())
                    .orElseThrow(() -> new CustomException("Menu item not found!", HttpStatus.NOT_FOUND));

            if (!menuItem.getAvailable()) {
                throw new CustomException(menuItem.getName() + " is not available!", HttpStatus.BAD_REQUEST);
            }

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setPriceAtOrderTime(menuItem.getPrice());
            orderItem.setQuantity(item.getQuantity());
            orderItem.setMenuItemName(menuItem.getName());
            orderItem.setMenuItem(menuItem);

            // Subtotal of each item is quantity x price
            BigDecimal subtotal = menuItem.getPrice().multiply(BigDecimal.valueOf(orderItem.getQuantity()));
            orderItem.setSubtotal(subtotal);

            items.add(orderItem);
            total = total.add(subtotal);
        }

        order.setItems(items);
        order.setTotalAmount(total);

        return orderRepository.save(order);
    }

    @Override
    public Page<Order> getOrders(OrderStatus status,
                                 Integer tableNumber,
                                 LocalDate startDate,
                                 LocalDate endDate,
                                 Pageable pageable) {
        // All filters must be satisfied; specification resembles WHERE queries in SQL
        Specification<Order> spec = Specification.allOf(
                OrderSpecification.hasStatus(status),
                OrderSpecification.hasTableNumber(tableNumber),
                OrderSpecification.createdAfterOrEqual(startDate),
                OrderSpecification.createdBefore(endDate)
        );

        return orderRepository.findAll(spec, pageable);
    }

    @Override
    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new CustomException("Order not found!", HttpStatus.NOT_FOUND));
    }

    @Override
    public Order updateOrderStatus(Long orderId, OrderStatus status) {
        Order order = this.getOrderById(orderId);
        OrderStatus currentStatus = order.getStatus();

        // Check if transition is valid using switch expression
        boolean isValidTransition = switch (currentStatus) {
            case PLACED -> status == OrderStatus.PREPARING || status == OrderStatus.CANCELLED;
            case PREPARING -> status == OrderStatus.READY;
            case READY -> status == OrderStatus.SERVED;
            case SERVED, CANCELLED -> false;
        };

        if (!isValidTransition) {
            throw new CustomException("Invalid status transition from " + currentStatus + " to " + status, HttpStatus.BAD_REQUEST);
        }

        order.setStatus(status);
        return orderRepository.save(order);
    }

    @Override
    public DailyReportResponse getDailyReport() {
        LocalDateTime startOfToday = LocalDate.now().atStartOfDay();
        LocalDateTime endOfToday = LocalDate.now().atTime(LocalTime.MAX);

        List<Order> orders = orderRepository.findByCreatedAtBetween(startOfToday, endOfToday);

        BigDecimal totalRevenue = BigDecimal.ZERO;

        for (Order order : orders) {
            totalRevenue = totalRevenue.add(order.getTotalAmount());
        }

        return new DailyReportResponse(totalRevenue, (long) orders.size());
    }
}