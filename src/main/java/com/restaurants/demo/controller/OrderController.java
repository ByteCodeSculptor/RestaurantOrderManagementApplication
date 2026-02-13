package com.restaurants.demo.controller;

import com.restaurants.demo.dto.request.OrderRequest;
import com.restaurants.demo.dto.response.DailyReport;
import com.restaurants.demo.dto.response.OrderItemResponse;
import com.restaurants.demo.dto.response.OrderResponse;
import com.restaurants.demo.entity.Order;
import com.restaurants.demo.service.OrderService;
import com.restaurants.demo.util.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @PreAuthorize("hasRole('STAFF')")
    public ResponseEntity<OrderResponse> createOrder(@RequestBody OrderRequest request) {
        Order order = orderService.createOrder(request);
        return ResponseEntity.ok(mapToResponse(order));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<Page<OrderResponse>> getAllOrders(
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false) Integer tableNumber,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable
    ) {
        Page<Order> orderPage = orderService.getAllOrders(status, tableNumber, startDate, endDate, pageable);
        return ResponseEntity.ok(orderPage.map(this::mapToResponse));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long id) {
        Order order = orderService.getOrderDetails(id);
        return ResponseEntity.ok(mapToResponse(order));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('STAFF')")
    public ResponseEntity<OrderResponse> updateStatus(
            @PathVariable Long id,
            @RequestParam OrderStatus newStatus
    ) {
        Order order = orderService.updateStatus(id, newStatus);
        return ResponseEntity.ok(mapToResponse(order));
    }

    @GetMapping("/reports/daily")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DailyReport> getDailyReport() {
        return ResponseEntity.ok(orderService.getDailyReport());
    }

    private OrderResponse mapToResponse(Order order) {
        List<OrderItemResponse> itemResponses = order.getItems().stream()
                .map(item -> new OrderItemResponse(
                        item.getId(),
                        item.getItemName(),
                        item.getQuantity(),

                        item.getItemPrice().doubleValue(),

                        item.getSubtotal().doubleValue()
                )).collect(Collectors.toList());

        return new OrderResponse(
                order.getId(),
                order.getTableNumber(),
                order.getStatus(),
                order.getTotalAmount().doubleValue(),
                itemResponses,
                order.getCreatedAt(),
                order.getUpdatedAt()
        );
    }
}