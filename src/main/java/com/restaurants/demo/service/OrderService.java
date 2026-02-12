package com.restaurants.demo.service;

import com.restaurants.demo.dto.request.OrderItemRequest;
import com.restaurants.demo.dto.request.OrderRequest;
import com.restaurants.demo.dto.response.DailyReport;
import com.restaurants.demo.entity.Order;
import com.restaurants.demo.entity.OrderItem;
import com.restaurants.demo.repository.OrderRepository;
import com.restaurants.demo.util.OrderStatus;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    // private final MenuRepository menuRepository;

    @Transactional
    public Order createOrder(OrderRequest request) {
        Order order = new Order();
        order.setTableNumber(request.getTableNumber());
        order.setStatus(OrderStatus.PLACED);

        List<OrderItem> items = new ArrayList<>();
        double total = 0.0;

        for (OrderItemRequest itemRequest : request.getItems()) {
            OrderItem item = new OrderItem();
            item.setOrder(order);

            /* // Real implementation would look like this:
            MenuItem menuItem = menuRepository.findById(itemRequest.getMenuItemId())
                    .orElseThrow(() -> new RuntimeException("Menu item not found"));

            item.setItemName(menuItem.getName());
            double realPrice = menuItem.getPrice();
            */

            item.setItemName("Item " + itemRequest.getMenuItemId());
            double realPrice = 100.0;

            item.setItemPrice(realPrice);
            item.setQuantity(itemRequest.getQuantity());

            double lineItemTotal = realPrice * itemRequest.getQuantity();

            item.setSubtotal(lineItemTotal);

            items.add(item);
            total += lineItemTotal;
        }

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

        double totalRevenue = 0.0;
        for (Order order : todayOrders) {
            totalRevenue += order.getTotalAmount();
        }

        return new DailyReport((long) todayOrders.size(), totalRevenue);
    }
}