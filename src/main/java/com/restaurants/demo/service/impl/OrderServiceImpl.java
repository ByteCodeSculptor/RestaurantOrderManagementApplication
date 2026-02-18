package com.restaurants.demo.service.impl;

import com.restaurants.demo.dto.request.OrderItemRequest;
import com.restaurants.demo.dto.request.OrderRequest;
import com.restaurants.demo.dto.response.DailyReportResponse;
import com.restaurants.demo.dto.response.OrderResponse;
import com.restaurants.demo.entity.MenuItem;
import com.restaurants.demo.entity.Order;
import com.restaurants.demo.exception.ResourceNotAvailableException;
import com.restaurants.demo.exception.ResourceNotFoundException;
import com.restaurants.demo.mapper.OrderMapper;
import com.restaurants.demo.repository.MenuItemRepository;
import com.restaurants.demo.repository.OrderRepository;
import com.restaurants.demo.service.OrderService;
import com.restaurants.demo.specification.OrderSpecification;
import com.restaurants.demo.util.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        for (OrderItemRequest itemReq : request.getItems()) {
            MenuItem menuItem = menuItemMap.get(itemReq.getMenuItemId());
            order.addOrUpdateItem(menuItem, itemReq.getQuantity(), menuItem.getPrice());
        }

        Order savedOrder = orderRepository.save(order);
        return orderMapper.toResponse(savedOrder);
    }

    @Override
    public OrderResponse updateOrder(Long orderId, OrderRequest request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order Not Found: " + orderId));

        validateOrderModifiable(order);

        Map<Long, MenuItem> menuItemMap = fetchAndValidateMenuItems(request.getItems());

        Map<Long, Integer> requestItemMap = request.getItems().stream()
                .collect(Collectors.toMap(OrderItemRequest::getMenuItemId, OrderItemRequest::getQuantity));

        List<Long> itemsToRemove = order.getItems().stream()
                .map(item -> item.getMenuItem().getId())
                .filter(id -> !requestItemMap.containsKey(id))
                .toList();

        itemsToRemove.forEach(order::removeItem);

        request.getItems().forEach(itemReq -> {
            MenuItem menuItem = menuItemMap.get(itemReq.getMenuItemId());
            order.addOrUpdateItem(menuItem, itemReq.getQuantity(), menuItem.getPrice());
        });

        return orderMapper.toResponse(orderRepository.save(order));
    }

    @Override
    public OrderResponse updateOrderStatus(Long orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order Not Found with Id: " + orderId));

        order.changeStatus(status);

        return orderMapper.toResponse(orderRepository.save(order));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponse> getOrders(OrderStatus status,
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

        return orderRepository.findAll(spec, pageable)
                .map(orderMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order Not Found with Id: " + orderId));
        return orderMapper.toResponse(order);
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

    // --- Private Helpers ---

    private Map<Long, MenuItem> fetchAndValidateMenuItems(List<OrderItemRequest> itemRequests) {
        List<Long> menuItemIds = itemRequests.stream()
                .map(OrderItemRequest::getMenuItemId)
                .distinct()
                .toList();

        List<MenuItem> menuItems = menuItemRepository.findAllById(menuItemIds);

        if (menuItems.size() != menuItemIds.size()) {
            throw new ResourceNotFoundException("One or more Menu Items not found!");
        }

        return menuItems.stream()
                .collect(Collectors.toMap(MenuItem::getId, menuItem -> menuItem));
    }

    private void validateOrderModifiable(Order order) {
        if (order.getStatus() == OrderStatus.BILLED || order.getStatus() == OrderStatus.CANCELLED) {
            throw new ResourceNotAvailableException("Table not available for modification!");
        }
    }
}