package com.restaurants.demo.service;

import com.restaurants.demo.dto.request.OrderRequest;
import com.restaurants.demo.dto.response.OrderResponse;
import com.restaurants.demo.entity.Order;
import com.restaurants.demo.entity.OrderItem;
import com.restaurants.demo.mapper.OrderMapper;
import com.restaurants.demo.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    // private final MenuRepository menuRepository; // Uncomment when you have a Menu table

    @Transactional
    public OrderResponse createOrder(OrderRequest request) {
        Order order = orderMapper.toEntity(request);

        List<OrderItem> orderItems = request.getItems().stream().map(reqItem -> {
            // SECURITY NOTE: In a real app, fetch price from DB:

//             Double realPrice = menuRepository.findById(reqItem.getMenuItemId()).getPrice();

            return OrderItem.builder()
                    .order(order)
                    .itemName("Fetched from DB")
//                    .priceAtOrder(realPrice)
                    .quantity(reqItem.getQuantity())
//                    .subtotal(realPrice * reqItem.getQuantity())
                    .build();
        }).collect(Collectors.toList());

        order.setItems(orderItems);
        order.setTotalAmount(orderItems.stream().mapToDouble(OrderItem::getSubtotal).sum());

        Order savedOrder = orderRepository.save(order);
        return orderMapper.toResponseDTO(savedOrder);
    }
}