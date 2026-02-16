package com.restaurants.demo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/*
    OrderItem is an entity class that represents an item in a customer's order. 
    It contains references to the associated Order and MenuItem, as well as details about the quantity ordered, price at the time of order, and the subtotal for that item.
*/

@Entity
@Table(name = "order_items")
@Getter
@Setter
@Builder
@NoArgsConstructor // OrderItem orderItem = new OrderItem()
@AllArgsConstructor
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne
    @JoinColumn(name = "menu_item_id", nullable = false)
    private MenuItem menuItem;

    @Column(name = "menu_item_name", length = 100, nullable = false)
    private String menuItemName;

    @Column(name = "price_at_order_time", nullable = false)
    private BigDecimal priceAtOrderTime;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private BigDecimal subtotal;
}