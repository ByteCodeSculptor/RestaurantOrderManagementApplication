package com.restaurants.demo.service.impl;

import com.restaurants.demo.dto.request.OrderItemRequest;
import com.restaurants.demo.dto.request.OrderRequest;
import com.restaurants.demo.dto.response.DailyReportResponse;
import com.restaurants.demo.dto.response.OrderResponse;
import com.restaurants.demo.entity.MenuItem;
import com.restaurants.demo.entity.Order;
import com.restaurants.demo.entity.OrderItem;
import com.restaurants.demo.exception.ApiResponse;
import com.restaurants.demo.mapper.OrderMapper;
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
import org.springframework.http.ResponseEntity;
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
    private final OrderMapper orderMapper;

    @Override
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(OrderRequest orderRequest) {
        try {
            Order order = new Order();
            order.setTableNumber(orderRequest.getTableNumber());
            order.setStatus(OrderStatus.PLACED);

            List<OrderItem> items = new ArrayList<>();
            BigDecimal total = BigDecimal.ZERO;

            // Map fields of orderItemRequest with the OrderItem entity
            for (OrderItemRequest item : orderRequest.getItems()) {
                MenuItem menuItem = menuItemRepository.findById(item.getMenuItemId())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Menu item not found!"));

                if (!menuItem.getAvailable()) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, menuItem.getName() + " is not available!");
                }

                OrderItem orderItem = OrderItem.builder()
                        .order(order)
                        .menuItem(menuItem)
                        .menuItemName(menuItem.getName())
                        .priceAtOrderTime(menuItem.getPrice())
                        .quantity(item.getQuantity())
                        .build();

                // Subtotal of each item is quantity x price
                BigDecimal subtotal = menuItem.getPrice().multiply(BigDecimal.valueOf(orderItem.getQuantity()));
                orderItem.setSubtotal(subtotal);

                items.add(orderItem);
                total = total.add(subtotal);
            }

            order.setItems(items);
            order.setTotalAmount(total);

            Order newOrder = orderRepository.save(order);

            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(true,"Order created Successfully", orderMapper.toResponse(newOrder)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(false, "Failed to created order", null));
        }
    }

    @Override
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getOrders(OrderStatus status,
                                 Integer tableNumber,
                                 LocalDate startDate,
                                 LocalDate endDate,
                                 Pageable pageable) {
        try {
            // All filters must be satisfied; specification resembles WHERE queries in SQL
            Specification<Order> spec = Specification.allOf(
                    OrderSpecification.hasStatus(status),
                    OrderSpecification.hasTableNumber(tableNumber),
                    OrderSpecification.createdAfterOrEqual(startDate),
                    OrderSpecification.createdBefore(endDate)
            );

            Page<Order> orders = orderRepository.findAll(spec, pageable);

            List<OrderResponse> orderResponses = new ArrayList<>();

            for (Order order : orders) {
                orderResponses.add (orderMapper.toResponse(order));
            }

            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(true,"Fetched orders successfully", orderResponses));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(false, "Failed to fetch orders", null));
        }
    }

    @Override
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderById(Long orderId) {
        try {
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
            OrderResponse orderResponse = orderMapper.toResponse(order);
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(true,"Fetched the order successfully", orderResponse));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(false, "Failed to fetch order", null));
        }
    }

    @Override
    public ResponseEntity<ApiResponse<OrderResponse>> updateOrder(Long orderId, OrderRequest orderRequest) {
        try {
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND, "Order not found!"
                    ));

            if (!order.getIsTableOpen()) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Order cannot be updated!"
                );
            }

            order.getItems().clear();

            BigDecimal total = BigDecimal.ZERO;

            for (OrderItemRequest itemRequest : orderRequest.getItems()) {
                MenuItem menuItem = menuItemRepository.findById(itemRequest.getMenuItemId())
                        .orElseThrow(() -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND, "Menu item not found!"
                        ));

                if (!menuItem.getAvailable()) {
                    throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST, "Item not available!"
                    );
                }

                OrderItem orderItem = OrderItem.builder()
                        .order(order)
                        .menuItem(menuItem)
                        .menuItemName(menuItem.getName())
                        .priceAtOrderTime(menuItem.getPrice())
                        .quantity(itemRequest.getQuantity())
                        .build();

                BigDecimal subTotal = menuItem.getPrice().multiply(BigDecimal.valueOf(itemRequest.getQuantity()));

                orderItem.setSubtotal(subTotal);

                order.getItems().add(orderItem);

                total = total.add(subTotal);
            }

            order.setTotalAmount(total);

            Order newOrder = orderRepository.save(order);

            OrderResponse orderResponse = orderMapper.toResponse(newOrder);

            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(true,"Order updated Successfully", orderResponse));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(false, "Failed to update order", null));
        }
    }

    @Override
    public ResponseEntity<ApiResponse<OrderResponse>> updateOrderStatus(Long orderId, OrderStatus status) {
        try {
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
            OrderStatus currentStatus = order.getStatus();

            // Check if transition is valid using switch expression
            boolean isValidTransition = switch (currentStatus) {
                case PLACED -> status == OrderStatus.PREPARING || status == OrderStatus.CANCELLED;
                case PREPARING -> status == OrderStatus.READY;
                case READY -> status == OrderStatus.SERVED;
                case SERVED, CANCELLED -> false;
            };

            if (!isValidTransition) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Invalid transition");
            }

            order.setStatus(status);
            Order newOrder = orderRepository.save(order);

            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(true,"Order status updated successfully", orderMapper.toResponse(newOrder)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(false, "Failed to update order status", null));
        }
    }

    @Override
    public ResponseEntity<ApiResponse<OrderResponse>> deleteOrder(Long orderId) {
        try {
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found!"));

            orderRepository.delete(order);
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(true,"Order deleted successfully", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(false, "Failed to delete order!", null));
        }
    }

    @Override
    public ResponseEntity<ApiResponse<DailyReportResponse>> getDailyReport(LocalDate startDate, LocalDate endDate) {
        try {
            LocalDateTime startOfToday = startDate.atStartOfDay();
            LocalDateTime endOfToday = endDate.atTime(LocalTime.MAX);

            List<Order> orders = orderRepository.findByCreatedAtBetween(startOfToday, endOfToday);

            BigDecimal totalRevenue = BigDecimal.ZERO;

            for (Order order : orders) {
                totalRevenue = totalRevenue.add(order.getTotalAmount());
            }

            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(true,"Report fetched successfully", new DailyReportResponse(totalRevenue, (long) orders.size())));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(false, "Failed to fetch report!", null));
        }
    }
}