package com.restaurants.demo.mapper;

import com.restaurants.demo.dto.response.OrderItemResponse;
import com.restaurants.demo.dto.response.OrderResponse;
import com.restaurants.demo.entity.Order;
import com.restaurants.demo.entity.OrderItem;
import org.mapstruct.Mapper;

/*
    OrderMapper is responsible for converting Order entities to OrderResponse DTOs, which are used to send order data in API responses.
    It also converts OrderItem entities to OrderItemResponse DTOs for detailed order information.
 */
@Mapper(componentModel = "spring")
public interface OrderMapper {
    OrderResponse toResponse (Order order);

    OrderItemResponse toResponse (OrderItem item);
}




