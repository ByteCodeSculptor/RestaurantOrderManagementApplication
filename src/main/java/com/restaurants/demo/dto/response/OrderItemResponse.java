package com.restaurants.demo.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/*
    OrderItemResponse is a DTO used to represent an item in an order response. It contains the order item ID, the associated order ID, the menu item ID, the name of the menu item, the quantity ordered, the price at the time of order, and the subtotal for that item.
*/ 

@Getter
@Builder
public class OrderItemResponse {
    private Long id;
    private Long orderId;
    private Long menuItemId;
    private String menuItemName;
    private Integer quantity;
    private BigDecimal priceAtOrderTime;
    private BigDecimal subtotal;
}