package com.restaurants.demo.service;

import com.restaurants.demo.dto.request.OrderItemRequest;
import com.restaurants.demo.dto.request.OrderRequest;
import com.restaurants.demo.dto.response.DailyReport;
import com.restaurants.demo.entity.MenuItem;
import com.restaurants.demo.entity.Order;
import com.restaurants.demo.entity.OrderItem;
import com.restaurants.demo.repository.MenuItemRepository;
import com.restaurants.demo.repository.OrderRepository;
import com.restaurants.demo.specifications.OrderSpecification;
import com.restaurants.demo.util.OrderStatus;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final MenuItemRepository menuRepository;

    @Transactional // for atomicity - both order and all its items must be created else everything will be rolled back
    // business logic to create an order with all its orderItems and store in db
    public Order createOrder(OrderRequest request) {
        Order order = new Order();
        order.setTableNumber(request.getTableNumber());
        order.setStatus(OrderStatus.PLACED);

        // Initialize the list so we can add items to it
        List<OrderItem> items = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (OrderItemRequest itemRequest : request.getItems()) {
            OrderItem item = new OrderItem();
            item.setOrder(order);

            // Check if menu item with given menuItemId exists in menuItem table or not
            MenuItem menuItem = menuRepository.findById(itemRequest.getMenuItemId())
                    .orElseThrow(() -> new RuntimeException("Menu item not found with ID: " + itemRequest.getMenuItemId()));

            // Validate the availability of the menuItem
            if (Boolean.FALSE.equals(menuItem.getAvailable())) {
                throw new RuntimeException("Item '" + menuItem.getName() + "' is currently unavailable.");
            }

            item.setMenuItem(menuItem);
            item.setItemName(menuItem.getName());
            item.setItemPrice(menuItem.getPrice());

            item.setQuantity(itemRequest.getQuantity());
            BigDecimal subTotal = menuItem.getPrice().multiply(BigDecimal.valueOf(itemRequest.getQuantity()));
            item.setSubtotal(subTotal);

            items.add(item);
            total = total.add(subTotal);
        }

        order.setItems(items);
        order.setTotalAmount(total);

        return orderRepository.save(order);
    }

    // Service to fetch all orders by adding dynamic filtering
    public Page<Order> getAllOrders(
            OrderStatus status,
            Integer tableNumber,
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable
    ) {
        LocalDateTime start = startDate != null ? startDate.atStartOfDay() : null;
        LocalDateTime end = endDate != null ? endDate.atTime(LocalTime.MAX) : null;

        Specification<Order> spec = Specification.allOf(
                OrderSpecification.hasStatus(status),
                OrderSpecification.hasTableNumber(tableNumber),
                OrderSpecification.createdAfter(start),
                OrderSpecification.createdBefore(end)
        );

        return orderRepository.findAll(spec, pageable);
    }

    public Order getOrderDetails(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));
    }

    public Order updateStatus(Long orderId, OrderStatus newStatus) {
        Order order = getOrderDetails(orderId);
        OrderStatus current = order.getStatus();

        boolean isAllowed = switch (current) {
            case PLACED -> (newStatus == OrderStatus.PREPARING || newStatus == OrderStatus.CANCELLED);
            case PREPARING -> (newStatus == OrderStatus.READY);
            case READY -> (newStatus == OrderStatus.SERVED);
            case SERVED -> false;
            case CANCELLED -> false;
        };

        if (!isAllowed) {
            throw new RuntimeException("Invalid transition: Cannot move from " + current + " to " + newStatus);
        }

        order.setStatus(newStatus);
        return orderRepository.save(order);
    }

    public DailyReport getDailyReport() {
        LocalDateTime start = LocalDate.now().atStartOfDay();
        LocalDateTime end = LocalDateTime.now();

        List<Order> todayOrders = orderRepository.findByCreatedAtBetween(start, end);

        BigDecimal totalRevenue = BigDecimal.ZERO;
        for (Order order : todayOrders) {
            totalRevenue = totalRevenue.add(order.getTotalAmount());
        }

        return new DailyReport((long) todayOrders.size(), totalRevenue.doubleValue());
    }
}