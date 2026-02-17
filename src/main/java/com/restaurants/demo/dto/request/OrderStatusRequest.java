package com.restaurants.demo.dto.request;

import com.restaurants.demo.util.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter; // Add Setter for Jackson deserialization
import lombok.NoArgsConstructor; // Good practice for DTOs

@Getter
@Setter
@NoArgsConstructor
public class OrderStatusRequest {

    @NotNull(message = "Status cannot be null")
    private OrderStatus status;
}