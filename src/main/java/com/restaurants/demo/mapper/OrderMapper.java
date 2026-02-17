package com.restaurants.demo.mapper;

import com.restaurants.demo.dto.response.OrderItemResponse;
import com.restaurants.demo.dto.response.OrderResponse;
import com.restaurants.demo.entity.Order;
import com.restaurants.demo.entity.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/*
    OrderMapper is responsible for converting Order entities to OrderResponse DTOs, which are used to send order data in API responses.
    It also converts OrderItem entities to OrderItemResponse DTOs for detailed order information.
 */
@Mapper(componentModel = "spring")
public interface OrderMapper {
    @Mapping(source = "totalAmount", target = "totalAmount")
    OrderResponse toResponse (Order order);

    @Mapping(source = "menuItem.id", target = "menuItemId")
    @Mapping(source = "menuItem.name", target = "menuItemName") // Just to be safe
    @Mapping(source = "order.id", target = "orderId")
    @Mapping(source = "priceAtOrderTime", target = "priceAtOrderTime")
    @Mapping(source = "subtotal", target = "subtotal")
    OrderItemResponse toResponse (OrderItem item);
}




