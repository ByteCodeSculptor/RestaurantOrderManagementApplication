package com.restaurants.demo.controller;

import com.restaurants.demo.dto.request.OrderRequest;
import com.restaurants.demo.dto.response.DailyReportResponse;
import com.restaurants.demo.dto.response.OrderResponse;
import com.restaurants.demo.entity.Order;
import com.restaurants.demo.mapper.OrderMapper;
import com.restaurants.demo.service.OrderService;
import com.restaurants.demo.util.OrderStatus;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    private final OrderMapper orderMapper;

    // ResponseEntity used to return JSON along with a custom HTTP status code.

    // Controller to create order
    @PostMapping
    @PreAuthorize("hasRole('STAFF')")
    public ResponseEntity<OrderResponse> createOrder (@Valid @RequestBody OrderRequest orderRequest) {
        Order order = orderService.createOrder(orderRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orderMapper.toResponse(order));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<List<OrderResponse>> getOrders (@RequestParam(required = false) OrderStatus status,
                                                          @RequestParam(required = false) Integer tableNumber,
                                                          @RequestParam(required = false) LocalDate startDate,
                                                          @RequestParam(required = false) LocalDate endDate,
                                                          Pageable pageable) {
        Page<Order> orders = orderService.getOrders(status, tableNumber, startDate, endDate, pageable);

        List<OrderResponse> orderResponses = new ArrayList<>();

        for (Order order : orders) {
            orderResponses.add (orderMapper.toResponse(order));
        }
        return ResponseEntity.status(HttpStatus.OK)
                .body(orderResponses);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<OrderResponse> getOrderById (@PathVariable Long id) {
        Order order = orderService.getOrderById(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(orderMapper.toResponse((order)));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('STAFF')")
    public ResponseEntity<OrderResponse> updateOrderStatus (@PathVariable Long id,
                                                            @RequestParam OrderStatus orderStatus) {
        Order order = orderService.updateOrderStatus(id, orderStatus);
        return ResponseEntity.status(HttpStatus.OK)
                .body(orderMapper.toResponse(order));
    }

    @GetMapping("/reports/daily")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DailyReportResponse> getDailyReport () {
        DailyReportResponse dailyReportResponse = orderService.getDailyReport();
        return ResponseEntity.status(HttpStatus.OK)
                .body(dailyReportResponse);
    }
}