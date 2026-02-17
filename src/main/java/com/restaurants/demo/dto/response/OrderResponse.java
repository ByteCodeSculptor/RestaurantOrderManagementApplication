package com.restaurants.demo.dto.response;

import com.restaurants.demo.util.OrderStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/*
    OrderResponse is a DTO used to represent an order in the response. It contains the order ID, table number, order status, a list of ordered items (each represented by an OrderItemResponse), the total amount for the order, and the timestamp when the order was created.
*/
@Getter
@Builder
public class OrderResponse {
    private Long id;
    private Integer tableNumber;
    private OrderStatus status;
    private List<OrderItemResponse> items;
    private Long totalAmount;
    private LocalDateTime createdAt;
}