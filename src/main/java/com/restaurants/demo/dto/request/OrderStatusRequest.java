package com.restaurants.demo.dto.request;

import com.restaurants.demo.util.OrderStatus;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;

@Getter
public class OrderStatusRequest {
    @NotEmpty
    private OrderStatus orderStatus;
}
