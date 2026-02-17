package com.restaurants.demo.util;

import com.restaurants.demo.dto.request.OrderItemRequest;
import com.restaurants.demo.entity.MenuItem;
import com.restaurants.demo.entity.Order;
import com.restaurants.demo.entity.OrderItem;
import com.restaurants.demo.exception.ResourceNotFoundException;

import java.util.List;
import java.util.Map;

public class OrderItemsUtil {
    /**
     * Creates a new OrderItem with validated availability and price.
     */
    public static OrderItem createOrderItem(Order order, MenuItem menuItem, int quantity) {
        if (menuItem == null) {
            throw new ResourceNotFoundException("Menu Item Not Found");
        }

        if (!menuItem.getAvailable()) {
            throw new RuntimeException("Menu item not available!");
        }

        Long price = menuItem.getPrice();
        Long subtotal = price * quantity;

        return OrderItem.builder()
                .order(order)
                .menuItem(menuItem)
                .menuItemName(menuItem.getName())
                .priceAtOrderTimeInCents(price)
                .quantity(quantity)
                .subtotalInCents(subtotal)
                .build();
    }

    /**
     * Updates an existing item's quantity and recalculates its subtotal.
     */
    public static void updateItemQuantity(OrderItem existingItem, int extraQuantity) {
        int newQuantity = existingItem.getQuantity() + extraQuantity;
        existingItem.setQuantity(newQuantity);

        // Recalculate based on the ORIGINAL price at order time (Price locking)
        Long newSubtotal = existingItem.getPriceAtOrderTimeInCents() * newQuantity;
        existingItem.setSubtotalInCents(newSubtotal);
    }

    /**
     * Batch processes items for a brand new order.
     */
    public static void processNewOrderItems(Order order, List<OrderItemRequest> requests, Map<Long, MenuItem> menuItemMap) {
        for (OrderItemRequest itemRequest : requests) {
            MenuItem menuItem = menuItemMap.get(itemRequest.getMenuItemId());

            OrderItem orderItem = createOrderItem(order, menuItem, itemRequest.getQuantity());

            order.addItem(orderItem);
        }
    }
}