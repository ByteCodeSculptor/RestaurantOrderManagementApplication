package com.restaurants.demo.dto.response;

import com.restaurants.demo.util.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    private Long orderId;
    private Integer tableNumber;
    private OrderStatus status;    // Managed by your lifecycle logic
    private Double totalAmount;    // The server-side calculated sum
    private List<OrderItemResponse> items;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}