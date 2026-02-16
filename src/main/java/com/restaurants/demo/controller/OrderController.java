package com.restaurants.demo.controller;

import com.restaurants.demo.dto.request.OrderRequest;
import com.restaurants.demo.dto.request.OrderStatusRequest;
import com.restaurants.demo.dto.response.DailyReportResponse;
import com.restaurants.demo.dto.response.OrderResponse;
import com.restaurants.demo.exception.ApiResponse;
import com.restaurants.demo.mapper.OrderMapper;
import com.restaurants.demo.service.OrderService;
import com.restaurants.demo.util.OrderStatus;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
/*  
    OrderController handles all order-related endpoints, including creating orders, updating order status, and generating daily reports.
    Access to endpoints is controlled using @PreAuthorize annotations to ensure only authorized roles can perform certain actions.
 */
public class OrderController {
    private final OrderService orderService;

    private final OrderMapper orderMapper;

    /*
        Endpoint for creating a new order. Accessible only to staff members.
     */
    @PostMapping
    @PreAuthorize("hasRole('STAFF')")
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder (@Valid @RequestBody OrderRequest orderRequest) {
        return orderService.createOrder(orderRequest);
    }

    // dynamic filtering controller whose access can be given to both admin and staff
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getOrders (@RequestParam(required = false) OrderStatus status,
                                        @RequestParam(required = false) Integer tableNumber,
                                        @RequestParam(required = false) LocalDate startDate,
                                        @RequestParam(required = false) LocalDate endDate,
                                                          Pageable pageable) {
        return orderService.getOrders(status, tableNumber, startDate, endDate, pageable);
    }

    /*
        Endpoint for fetching a specific order by ID. Accessible to both admin and staff.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderById (@PathVariable Long id) {
        return orderService.getOrderById(id);
    }

    /*
        Endpoint for updating the status of an order.
    */
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('STAFF')")
    public ResponseEntity<?> updateOrderStatus (@PathVariable Long id,
                                                            @RequestBody OrderStatusRequest orderStatusRequest) {
        return orderService.updateOrderStatus(id, orderStatusRequest.getOrderStatus());
    }

    /*
        Endpoint for updating the order.
    */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('STAFF')")
    public ResponseEntity<ApiResponse<OrderResponse>> updateOrder (@PathVariable Long id,
                                                @Valid @RequestBody OrderRequest orderRequest) {
        return orderService.updateOrder(id, orderRequest);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('STAFF')")
    public ResponseEntity<ApiResponse<OrderResponse>> deleteOrder (@PathVariable Long id) {
        return orderService.deleteOrder(id);
    }

    /*
        Endpoint for generating a daily report of orders. Accessible only to admin users.
    */
    @GetMapping("/reports")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<DailyReportResponse>> getDailyReport (LocalDate startDate, LocalDate endDate) {
        return orderService.getDailyReport(startDate, endDate);
    }
}