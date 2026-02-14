package com.restaurants.demo.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderItemRequest {
    @NotNull(message = "Menu item ID cannot be null!")
    private Long menuItemId;

    @NotNull(message = "Quantity cannot be null!")
    @Positive(message = "Quantity must be positive")
    private Integer quantity;
}