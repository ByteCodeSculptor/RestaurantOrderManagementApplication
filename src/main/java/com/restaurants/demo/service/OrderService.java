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

    @Transactional
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

            // 1. Fetch the MenuItem Entity
            MenuItem menuItem = menuRepository.findById(itemRequest.getMenuItemId())
                    .orElseThrow(() -> new RuntimeException("Menu item not found with ID: " + itemRequest.getMenuItemId()));

            // 2. Validate Availability
            if (Boolean.FALSE.equals(menuItem.getAvailable())) {
                throw new RuntimeException("Item '" + menuItem.getName() + "' is currently unavailable.");
            }

            // 3. Set the Relationship (FIX: Use setMenuItem, not setMenuItemId)
            item.setMenuItem(menuItem);

            // 4. SNAPSHOT DATA (Critical for history)
            // Even though we linked the entity above, we MUST copy the name and price
            // so historical records don't change if the menu changes later.
            item.setItemName(menuItem.getName());
            item.setItemPrice(menuItem.getPrice());

            // 5. Set Quantity and Calculate Subtotal
            item.setQuantity(itemRequest.getQuantity());
            BigDecimal lineTotal = menuItem.getPrice().multiply(BigDecimal.valueOf(itemRequest.getQuantity()));
            item.setSubtotal(lineTotal);

            items.add(item);
            total = total.add(lineTotal);
        }

        // 6. Finalize Order
        order.setItems(items);
        order.setTotalAmount(total);

        return orderRepository.save(order);
    }

    public Page<Order> getAllOrders(
            OrderStatus status,
            Integer tableNumber,
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable
    ) {
        LocalDateTime start = startDate != null ? startDate.atStartOfDay() : null;
        LocalDateTime end = endDate != null ? endDate.atTime(LocalTime.MAX) : null;

        Specification<Order> spec = OrderSpecification.hasStatus(status)
                .and(OrderSpecification.hasTableNumber(tableNumber))
                .and(OrderSpecification.createdAfter(start))
                .and(OrderSpecification.createdBefore(end));

        return orderRepository.findAll(spec, pageable);
    }

    public Order getOrderDetails(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));
    }

    public Order updateStatus(Long orderId, OrderStatus newStatus) {
        Order order = getOrderDetails(orderId);
        OrderStatus current = order.getStatus();

        // 1. Check if the transition is allowed
        boolean isAllowed = switch (current) {
            case PLACED -> (newStatus == OrderStatus.PREPARING || newStatus == OrderStatus.CANCELLED);
            case PREPARING -> (newStatus == OrderStatus.READY);
            case READY -> (newStatus == OrderStatus.SERVED);
            case SERVED -> false;    // No transitions allowed once served
            case CANCELLED -> false; // No transitions allowed once cancelled
        };

        // 2. If not allowed, block the update
        if (!isAllowed) {
            throw new RuntimeException("Invalid transition: Cannot move from " + current + " to " + newStatus);
        }

        // 3. Update and Save
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