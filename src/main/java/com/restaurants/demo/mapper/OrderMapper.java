package com.restaurants.demo.mapper;

import com.restaurants.demo.dto.response.OrderItemResponse;
import com.restaurants.demo.dto.response.OrderResponse;
import com.restaurants.demo.entity.Order;
import com.restaurants.demo.entity.OrderItem;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/*
    OrderMapper is responsible for converting Order entities to OrderResponse DTOs, which are used to send order data in API responses.
    It also converts OrderItem entities to OrderItemResponse DTOs for detailed order information.
 */
@Component
public class OrderMapper {

    public OrderResponse toResponse(Order order) {

        List<OrderItemResponse> items = new ArrayList<>();
        for (OrderItem item : order.getItems()) {
            items.add(toItemResponse(item));
        }

        return OrderResponse.builder()
                .id(order.getId())
                .tableNumber(order.getTableNumber())
                .status(order.getStatus())
                .totalAmount(order.getTotalAmount())
                .createdAt(order.getCreatedAt())
                .items(items)
                .build();
    }

    private OrderItemResponse toItemResponse(OrderItem item) {
        return OrderItemResponse.builder()
                .id(item.getId())
                .orderId(item.getOrder().getId())
                .menuItemId(item.getMenuItem().getId())
                .menuItemName(item.getMenuItemName())
                .quantity(item.getQuantity())
                .priceAtOrderTime(item.getPriceAtOrderTime())
                .subtotal(item.getSubtotal())
                .build();
    }
}

