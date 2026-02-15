package com.restaurants.demo.mapper;

import com.restaurants.demo.dto.response.OrderItemResponse;
import com.restaurants.demo.dto.response.OrderResponse;
import com.restaurants.demo.entity.Order;
import com.restaurants.demo.entity.OrderItem;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderMapper {

    public OrderResponse toResponse(Order order) {
        OrderResponse orderResponse = new OrderResponse();
        orderResponse.setId(order.getId());
        orderResponse.setTableNumber(order.getTableNumber());
        orderResponse.setStatus(order.getStatus());
        orderResponse.setTotalAmount(order.getTotalAmount());
        orderResponse.setCreatedAt(order.getCreatedAt());

        List<OrderItemResponse> items = new ArrayList<>();

        for (OrderItem item : order.getItems()) {
            items.add(toItemResponse(item));
        }

        orderResponse.setItems(items);

        return orderResponse;
    }

    private OrderItemResponse toItemResponse(OrderItem item) {
        OrderItemResponse orderItemResponse = new OrderItemResponse();
        orderItemResponse.setId(item.getId());
        orderItemResponse.setOrderId(item.getOrder().getId());
        orderItemResponse.setMenuItemId(item.getMenuItem().getId());
        orderItemResponse.setMenuItemName(item.getMenuItemName());
        orderItemResponse.setQuantity(item.getQuantity());
        orderItemResponse.setPriceAtOrderTime(item.getPriceAtOrderTime());
        orderItemResponse.setSubtotal(item.getSubtotal());
        return orderItemResponse;
    }
}
