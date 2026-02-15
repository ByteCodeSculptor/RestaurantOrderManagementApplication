package com.restaurants.demo.service;

import com.restaurants.demo.dto.request.OrderRequest;
import com.restaurants.demo.dto.response.DailyReportResponse;
import com.restaurants.demo.entity.Order;
import com.restaurants.demo.util.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.time.LocalDate;

//Entire business logic of order management lies here
@Service // Required annotation for spring to handle this Java class
public interface OrderService {
    Order createOrder (OrderRequest orderRequest);

    Page<Order> getOrders (OrderStatus status,
                           Integer tableNumber,
                           LocalDate startDate,
                           LocalDate endDate,
                           Pageable pageable);

    Order getOrderById (Long orderId);

    Order updateOrderStatus (Long orderId, OrderStatus status);

    DailyReportResponse getDailyReport ();
}
