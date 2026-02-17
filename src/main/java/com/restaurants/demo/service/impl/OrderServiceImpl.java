package com.restaurants.demo.service.impl;

import com.restaurants.demo.dto.request.OrderItemRequest;
import com.restaurants.demo.dto.request.OrderRequest;
import com.restaurants.demo.dto.response.DailyReportResponse;
import com.restaurants.demo.dto.response.OrderResponse;
import com.restaurants.demo.entity.MenuItem;
import com.restaurants.demo.entity.Order;
import com.restaurants.demo.entity.OrderItem;
import com.restaurants.demo.exception.InvalidTransitionException;
import com.restaurants.demo.exception.ResourceNotAvailableException;
import com.restaurants.demo.exception.ResourceNotFoundException;
import com.restaurants.demo.mapper.OrderMapper;
import com.restaurants.demo.repository.MenuItemRepository;
import com.restaurants.demo.repository.OrderRepository;
import com.restaurants.demo.service.OrderService;
import com.restaurants.demo.specification.OrderSpecification;
import com.restaurants.demo.util.OrderItemsUtil;
import com.restaurants.demo.util.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final MenuItemRepository menuItemRepository;
    private final OrderMapper orderMapper;

    @Override
    public OrderResponse createOrder(OrderRequest request) {
        Order order = new Order();
        order.setTableNumber(request.getTableNumber());
        order.setStatus(OrderStatus.PLACED);

        Map<Long, MenuItem> menuItemMap = fetchAndValidateMenuItems(request.getItems());

        OrderItemsUtil.processNewOrderItems(order, request.getItems(), menuItemMap);

        Order savedOrder = orderRepository.save(order);
        return orderMapper.toResponse(savedOrder);
    }

    @Override
    public OrderResponse updateOrder(Long orderId, OrderRequest request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order Not Found with Id: "+ orderId));

        if (order.getStatus() == OrderStatus.BILLED || order.getStatus() == OrderStatus.CANCELLED) {
            throw new ResourceNotAvailableException("Table is not available for order updation!");
        }

        Map<Long, MenuItem> menuItemMap = fetchAndValidateMenuItems(request.getItems());

        Map<Long, OrderItem> existingItemMap = order.getItems().stream()
                .collect(Collectors.toMap(
                        item -> item.getMenuItem().getId(),
                        item -> item
                ));

        for (OrderItemRequest itemRequest : request.getItems()) {
            OrderItem existingItem = existingItemMap.get(itemRequest.getMenuItemId());

            if (existingItem != null) {
                OrderItemsUtil.updateItemQuantity(existingItem, itemRequest.getQuantity());
            } else {
                MenuItem menuItem = menuItemMap.get(itemRequest.getMenuItemId());
                OrderItem newItem = OrderItemsUtil.createOrderItem(order, menuItem, itemRequest.getQuantity());
                order.addItem(newItem);
            }
        }

        order.recalculateTotal();

        return orderMapper.toResponse(orderRepository.save(order));
    }

    @Override
    public OrderResponse updateOrderStatus(Long orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order Not Found with Id: "+ orderId));

        boolean isValidTransition = switch (order.getStatus()) {
            case PLACED -> status == OrderStatus.PREPARING || status == OrderStatus.CANCELLED;
            case PREPARING -> status == OrderStatus.READY;
            case READY -> status == OrderStatus.SERVED;
            case SERVED -> status == OrderStatus.BILLED;
            case CANCELLED, BILLED -> false;
        };

        if (!isValidTransition) {
            throw new InvalidTransitionException(order.getStatus(), status);
        }

        order.setStatus(status);
        return orderMapper.toResponse(orderRepository.save(order));
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrders(OrderStatus status,
                                         Integer tableNumber,
                                         LocalDate startDate,
                                         LocalDate endDate,
                                         Pageable pageable) {
        Specification<Order> spec = Specification.allOf(
                OrderSpecification.hasStatus(status),
                OrderSpecification.hasTableNumber(tableNumber),
                OrderSpecification.createdAfterOrEqual(startDate),
                OrderSpecification.createdBefore(endDate)
        );

        return orderRepository.findAll(spec, pageable).stream()
                .map(orderMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order Not Found with Id: "+ orderId));
        return orderMapper.toResponse(order);
    }

    @Override
    public void deleteOrder(Long orderId) {
        if (!orderRepository.existsById(orderId)) {
           throw new ResourceNotFoundException("Order Not Found with Id: "+ orderId);
        }
        orderRepository.deleteById(orderId);
    }

    @Override
    @Transactional(readOnly = true)
    public DailyReportResponse getDailyReport(LocalDate startDate, LocalDate endDate) {
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(LocalTime.MAX);

        Long totalRevenue = orderRepository.sumTotalAmountBetween(start, end);
        totalRevenue = totalRevenue == null ? 0L : totalRevenue;

        long count = orderRepository.countByCreatedAtBetween(start, end);

        return new DailyReportResponse(totalRevenue, count);
    }

    private Map<Long, MenuItem> fetchAndValidateMenuItems(List<OrderItemRequest> itemRequests) {
        List<Long> menuItemIds = itemRequests.stream()
                .map(OrderItemRequest::getMenuItemId)
                .distinct()
                .toList();

        List<MenuItem> menuItems = menuItemRepository.findAllById(menuItemIds);

        if (menuItems.size() != menuItemIds.size()) {
            throw new ResourceNotFoundException("Menu item(s) not found!");
        }

        return menuItems.stream()
                .collect(Collectors.toMap(MenuItem::getId, menuItem -> menuItem));
    }
}