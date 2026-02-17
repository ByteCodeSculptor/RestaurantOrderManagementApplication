package com.restaurants.demo.controller;

import com.restaurants.demo.dto.request.OrderRequest;
import com.restaurants.demo.dto.request.OrderStatusRequest;
import com.restaurants.demo.dto.response.DailyReportResponse;
import com.restaurants.demo.dto.response.OrderResponse;
import com.restaurants.demo.util.ApiResponse;
import com.restaurants.demo.service.OrderService;
import com.restaurants.demo.util.OrderStatus;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor

public class OrderController {
    private final OrderService orderService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder (@Valid @RequestBody OrderRequest request) {
        OrderResponse response = orderService.createOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response,"Order Created Successfully!"));
    }

    // dynamic filtering controller whose access can be given to both admin and staff
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getOrders (@RequestParam(required = false) OrderStatus status,
                                        @RequestParam(required = false) Integer tableNumber,
                                        @RequestParam(required = false) LocalDate startDate,
                                        @RequestParam(required = false) LocalDate endDate,
                                                          Pageable pageable) {
        List<OrderResponse> response = orderService.getOrders(status, tableNumber, startDate, endDate, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(response,"Orders Fetched Successfully!"));
    }


    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderById (@PathVariable Long id) {
        OrderResponse response =  orderService.getOrderById(id);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(response,"Order Fetched Successfully!"));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<OrderResponse>> updateOrderStatus (@PathVariable Long id,
                                                            @RequestBody OrderStatusRequest orderStatusRequest) {
        OrderResponse response =  orderService.updateOrderStatus(id, orderStatusRequest.getStatus());
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(response,"Order Status Updated Successfully!"));
    }


    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<OrderResponse>> updateOrder (@PathVariable Long id,
                                                @Valid @RequestBody OrderRequest orderRequest) {
        OrderResponse response =  orderService.updateOrder(id, orderRequest);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(response,"Order Updated Successfully!"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<OrderResponse>> deleteOrder (@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(null,"Order Deleted Successfully!"));
    }


    @GetMapping("/reports")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<DailyReportResponse>> getDailyReport (LocalDate startDate, LocalDate endDate) {
        DailyReportResponse response =  orderService.getDailyReport(startDate, endDate);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(response,"Report Fetched Successfully!"));
    }
}