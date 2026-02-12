package com.restaurants.demo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemResponse {
    private Long id;
    private String itemName;
    private Integer quantity;
    private Double itemPrice;
    private Double subtotal;
}
