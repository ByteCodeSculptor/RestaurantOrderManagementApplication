package com.restaurants.demo.controller;

import com.restaurants.demo.dto.request.OrderRequest;
import com.restaurants.demo.dto.response.DailyReportResponse;
import com.restaurants.demo.dto.response.OrderResponse;
import com.restaurants.demo.entity.Order;
import com.restaurants.demo.mapper.OrderMapper;
import com.restaurants.demo.service.OrderService;
import com.restaurants.demo.util.OrderStatus;
import com.restaurants.demo.util.ResponseHelper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
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
    public ResponseEntity<?> createOrder (@Valid @RequestBody OrderRequest orderRequest) {
        Order order = orderService.createOrder(orderRequest);
        OrderResponse orderResponse = orderMapper.toResponse(order);

        return ResponseHelper.success(orderResponse, "Order created successfully!");
    }

    // dynamic filtering controller whose access can be given to both admin and staff
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<?> getOrders (@RequestParam(required = false) OrderStatus status,
                                                          @RequestParam(required = false) Integer tableNumber,
                                                          @RequestParam(required = false) LocalDate startDate,
                                                          @RequestParam(required = false) LocalDate endDate,
                                                          Pageable pageable) {
        Page<Order> orders = orderService.getOrders(status, tableNumber, startDate, endDate, pageable);

        List<OrderResponse> orderResponses = new ArrayList<>();

        for (Order order : orders) {
            orderResponses.add (orderMapper.toResponse(order));
        }

        return ResponseHelper.success(orderResponses, "Successfully fetched all orders!");
    }

    /*
        Endpoint for fetching a specific order by ID. Accessible to both admin and staff.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<?> getOrderById (@PathVariable Long id) {
        Order order = orderService.getOrderById(id);

        OrderResponse orderResponse = orderMapper.toResponse(order);

        return ResponseHelper.success(orderResponse, "Successfully fetched the order!");
    }

    /*
        Endpoint for updating the status of an order. Accessible only to staff members.
    */
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('STAFF')")
    public ResponseEntity<?> updateOrderStatus (@PathVariable Long id,
                                                            @RequestParam OrderStatus orderStatus) {
        Order order = orderService.updateOrderStatus(id, orderStatus);

        OrderResponse orderResponse = orderMapper.toResponse(order);

        return ResponseHelper.success(orderResponse, "Successfully updated the order status!");
    }

    /*
        Endpoint for generating a daily report of orders. Accessible only to admin users.
    */
    @GetMapping("/reports/daily")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getDailyReport () {
        DailyReportResponse dailyReportResponse = orderService.getDailyReport();

        return ResponseHelper.success(dailyReportResponse, "Successfully fetched today's report!");
    }
}