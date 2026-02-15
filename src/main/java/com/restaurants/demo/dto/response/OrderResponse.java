package com.restaurants.demo.dto.response;

import com.restaurants.demo.util.OrderStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class OrderResponse {
    private Long id;
    private Integer tableNumber;
    private OrderStatus status;
    private List<OrderItemResponse> items;
    private BigDecimal totalAmount;
    private LocalDateTime createdAt;
}