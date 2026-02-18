package com.restaurants.demo.service;

import com.restaurants.demo.dto.request.OrderRequest;
import com.restaurants.demo.dto.response.DailyReportResponse;
import com.restaurants.demo.dto.response.OrderResponse;
import com.restaurants.demo.util.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.LocalDate;
import java.util.List;

public interface OrderService {
    OrderResponse createOrder (OrderRequest request);

    Page<OrderResponse> getOrders (OrderStatus status,
                                   Integer tableNumber,
                                   LocalDate startDate,
                                   LocalDate endDate,
                                   Pageable pageable);

    OrderResponse getOrderById (Long orderId);

    OrderResponse updateOrderStatus (Long orderId, OrderStatus status);

    OrderResponse updateOrder (Long orderId, OrderRequest orderRequest);

    DailyReportResponse getDailyReport (LocalDate startDate, LocalDate endDate);
}