package com.restaurants.demo.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

/*
    OrderItemRequest is a DTO used to represent an item in an order. It contains the menu
    item ID and the quantity ordered.
 */
@Getter
@Setter
public class OrderItemRequest {
    @NotNull(message = "Menu item ID cannot be null!")
    private Long menuItemId;

    @NotNull(message = "Quantity cannot be null!")
    @Positive(message = "Quantity must be positive")
    private Integer quantity;
}