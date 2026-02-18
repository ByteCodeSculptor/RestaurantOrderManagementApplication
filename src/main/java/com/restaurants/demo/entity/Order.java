package com.restaurants.demo.entity;

import com.restaurants.demo.exception.InvalidTransitionException;
import com.restaurants.demo.util.OrderStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "table_number", nullable = false)
    private Integer tableNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    @Column(name = "total_amount", nullable = false)
    private Long totalAmount = 0L;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    private void onCreate() {
        this.createdAt = LocalDateTime.now();
    }


    public void addOrUpdateItem(MenuItem menuItem, int quantity, Long currentPrice) {
        OrderItem existingItem = null;

        for (OrderItem item : this.items) {
            if (item.getMenuItem().getId().equals(menuItem.getId())) {
                existingItem = item; existingItem.setQuantity(quantity);
                break;
            }
        }

        if (existingItem == null) {
            OrderItem newItem = new OrderItem(this, menuItem, quantity, currentPrice);
            this.items.add(newItem);
        }

        this.recalculateTotal();
    }


    public void removeItem(Long menuItemId) {
        OrderItem itemToRemove = null;

        for (OrderItem item : this.items) {
            if (item.getMenuItem().getId().equals(menuItemId)) {
                itemToRemove = item;
                break;
            }
        }

        if (itemToRemove != null) {
            this.items.remove(itemToRemove);
            itemToRemove.setOrder(null);
            recalculateTotal();
        }
    }

    public void recalculateTotal() {
        long sum = 0L;
        for (OrderItem item : this.items) {
            if (item.getSubtotal() != null) {
                sum += item.getSubtotal();
            }
        }
        this.totalAmount = sum;
    }

    public void changeStatus(OrderStatus newStatus) {
        if (!this.status.canTransitionTo(newStatus)) {
            throw new InvalidTransitionException(this.status, newStatus);
        }
        this.status = newStatus;
    }
}