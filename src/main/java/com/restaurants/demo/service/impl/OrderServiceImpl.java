package com.restaurants.demo.service.impl;

import com.restaurants.demo.dto.request.OrderItemRequest;
import com.restaurants.demo.dto.request.OrderRequest;
import com.restaurants.demo.dto.response.DailyReportResponse;
import com.restaurants.demo.entity.MenuItem;
import com.restaurants.demo.entity.Order;
import com.restaurants.demo.entity.OrderItem;
import com.restaurants.demo.exception.CustomException;
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
import org.springframework.web.server.ResponseStatusException;

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
    public Order createOrder (OrderRequest orderRequest) {
        try {
            Order order = new Order();
            order.setTableNumber(orderRequest.getTableNumber());
            order.setStatus(OrderStatus.PLACED);

            List<OrderItem> items = new ArrayList<>();

            BigDecimal total = BigDecimal.ZERO; // Initial value set to Zero

            // We have to map fields of orderItemRequest with the OrderItem entity to store in DB
            for (OrderItemRequest item : orderRequest.getItems()) {
                MenuItem menuItem = menuItemRepository.findById(item.getMenuItemId())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Menu item not found"));

                if (!menuItem.getAvailable()) {
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
                total = total.add(subtotal);
            }

            order.setItems(items);
            order.setTotalAmount(total);

            return orderRepository.save(order);
        } catch (Exception e) {
            throw new CustomException("Failed to create order!", HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public Page<Order> getOrders (OrderStatus status,
                                  Integer tableNumber,
                                  LocalDate startDate,
                                  LocalDate endDate,
                                  Pageable pageable) {
        try {
            // All filters must be satisfied and specification resemble WHERE queries in SQL
            // and all there queries must be fullfilled
            Specification<Order> spec = Specification.allOf(
                    OrderSpecification.hasStatus(status),
                    OrderSpecification.hasTableNumber(tableNumber),
                    OrderSpecification.createdAfterOrEqual(startDate),
                    OrderSpecification.createdBefore(endDate)
            );

            return orderRepository.findAll(spec, pageable);
        } catch (Exception e) {
            throw new CustomException("Failed to fetch orders!", HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public Order getOrderById (Long orderId) {
        try {
            return orderRepository.findById(orderId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found!"));
        } catch (Exception e) {
            throw new CustomException("Failed to fetch order!", HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public Order updateOrderStatus (Long orderId, OrderStatus status) {
        try {
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
        } catch (Exception e) {
            throw new CustomException("Failed to update order status!", HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public DailyReportResponse getDailyReport () {
        try {
            LocalDateTime startOfToday = LocalDate.now().atStartOfDay();
            LocalDateTime endOfToday = LocalDate.now().atTime(LocalTime.MAX);

            List<Order> orders = orderRepository.findByCreatedAtBetween(startOfToday, endOfToday);

            BigDecimal totalRevenue = BigDecimal.ZERO;

            for (Order order : orders) {
                totalRevenue = totalRevenue.add(order.getTotalAmount());
            }

            return new DailyReportResponse(totalRevenue, (long) orders.size());
        } catch (Exception e) {
            throw new CustomException("Failed to get daily report!", HttpStatus.BAD_REQUEST);
        }
    }
}
