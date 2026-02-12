package com.restaurants.demo.service;

import com.restaurants.demo.dto.request.OrderItemRequest;
import com.restaurants.demo.dto.request.OrderRequest;
import com.restaurants.demo.dto.response.DailyReport;
import com.restaurants.demo.entity.MenuItem;
import com.restaurants.demo.entity.Order;
import com.restaurants.demo.entity.OrderItem;
import com.restaurants.demo.repository.MenuItemRepository;
import com.restaurants.demo.repository.OrderRepository;
import com.restaurants.demo.util.OrderStatus;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
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

    public Page<Order> getAllOrders(OrderStatus status, Integer tableNumber, Pageable pageable) {
        if (status != null && tableNumber != null) {
            return orderRepository.findByStatusAndTableNumber(status, tableNumber, pageable);
        } else if (status != null) {
            return orderRepository.findByStatus(status, pageable);
        } else if (tableNumber != null) {
            return orderRepository.findByTableNumber(tableNumber, pageable);
        }
        return orderRepository.findAll(pageable);
    }

    public Order getOrderDetails(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));
    }

    public Order updateStatus(Long orderId, OrderStatus newStatus) {
        Order order = getOrderDetails(orderId);
        OrderStatus current = order.getStatus();

        // Validation logic for status transitions
        if (current == OrderStatus.SERVED && newStatus == OrderStatus.PLACED) {
            throw new RuntimeException("Invalid transition: Cannot go back to PLACED from SERVED");
        }
        if (current == OrderStatus.CANCELLED) {
            throw new RuntimeException("Invalid transition: Cannot update a CANCELLED order");
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