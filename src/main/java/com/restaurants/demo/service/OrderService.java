package com.restaurants.demo.service;

import com.restaurants.demo.dto.request.OrderRequest;
import com.restaurants.demo.dto.response.DailyReportResponse;
import com.restaurants.demo.dto.response.OrderResponse;
import com.restaurants.demo.util.OrderStatus;
import org.springframework.data.domain.Pageable;
import java.time.LocalDate;
import java.util.List;

/**
 * Service interface for managing Orders in the system.
 * This interface defines all business operations related to orders, such as:
 * - Creating a new order from an order request
 * - Fetching orders with optional filters (status, table number, date range) and pagination
 * - Retrieving a single order by its ID
 * - Updating the status of an existing order
 * - Generating a daily report of orders
 */
public interface OrderService {
    OrderResponse createOrder (OrderRequest request);

    List<OrderResponse> getOrders (OrderStatus status,
                                   Integer tableNumber,
                                   LocalDate startDate,
                                   LocalDate endDate,
                                   Pageable pageable);

    OrderResponse getOrderById (Long orderId);

    OrderResponse updateOrderStatus (Long orderId, OrderStatus status);

    OrderResponse updateOrder (Long orderId, OrderRequest orderRequest);

    void deleteOrder (Long orderId);

    DailyReportResponse getDailyReport (LocalDate startDate, LocalDate endDate);
}