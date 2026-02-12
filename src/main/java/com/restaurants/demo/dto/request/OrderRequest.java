package com.restaurants.demo.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class OrderRequest {
    @NotNull(message = "Table Number is required")
    private Integer tableNumber;

    @NotEmpty(message = "Order must have atleast one item")
    @Valid
    private List<OrderItemRequest> items;
}
