package com.restaurants.demo.entity;

import com.restaurants.demo.exception.ResourceNotAvailableException;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_item_id")
    private MenuItem menuItem;

    @Column(name = "menu_item_name")
    private String menuItemName;

    // Snapshot price
    @Column(name = "price_at_order_time")
    private Long priceAtOrderTime;

    private Integer quantity;

    private Long subtotal;

    public OrderItem(Order order, MenuItem menuItem, int quantity, Long currentPrice) {
        if (!menuItem.getAvailable()) {
            throw new ResourceNotAvailableException("Some menu item(s) may be currently unavailable");
        }

        this.order = order;
        this.menuItem = menuItem;
        this.menuItemName = menuItem.getName();
        this.quantity = quantity;

        this.priceAtOrderTime = currentPrice;

        this.recalculateSubtotal();
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
        this.recalculateSubtotal();
    }

    private void recalculateSubtotal() {
        if (this.priceAtOrderTime != null && this.quantity != null) {
            this.subtotal = this.priceAtOrderTime * this.quantity;
        } else {
            this.subtotal = 0L;
        }
    }
}