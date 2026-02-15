package com.restaurants.demo.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OrderRequest {
    @NotNull(message = "Table number cannot be null!")
    @Positive(message = "Table number must be positive")
    private Integer tableNumber;

    @Valid
    @NotEmpty(message = "Atleast one item must be present!")
    private List<OrderItemRequest> items;
}