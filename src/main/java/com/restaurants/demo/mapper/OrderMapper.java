package com.restaurants.demo.mapper;

import com.restaurants.demo.dto.request.OrderRequest;
import com.restaurants.demo.dto.response.OrderItemResponse;
import com.restaurants.demo.dto.response.OrderResponse;
import com.restaurants.demo.entity.Order;
import com.restaurants.demo.entity.OrderItem;
import com.restaurants.demo.util.OrderStatus;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderMapper {

    public Order toEntity(OrderRequest request) {
        return Order.builder()
                .tableNumber(request.getTableNumber())
                .status(OrderStatus.PLACED)
                .build();
    }

    public OrderResponse toResponseDTO(Order order) {
        List<OrderItemResponse> itemDTOs = order.getItems().stream()
                .map(this::toItemResponseDTO)
                .collect(Collectors.toList());

        return new OrderResponse(
                order.getOrderId(),
                order.getTableNumber(),
                order.getStatus(),
                order.getTotalAmount(),
                itemDTOs,
                order.getCreatedAt(),
                order.getUpdatedAt()
        );
    }

    private OrderItemResponse toItemResponseDTO(OrderItem item) {
        return new OrderItemResponse(
                item.getId(),
                item.getItemName(),
                item.getQuantity(),
                item.getItemPrice(),
                item.getSubtotal()
        );
    }
}