package com.restaurants.demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "orders")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

//    @ManyToOne
//    @JoinColumn(name = "menu_item_id", nullable = false)
//    private MenuItem menuItem;

    @Column(name = "menu_item_name", nullable = false)
    private String itemName;

    @Column(name = "price_at_order_time", nullable = false)
    private Double itemPrice;

    private Integer quantity;

    private Double subtotal;

    @PrePersist
    @PreUpdate
    public void calculateSubtotal () {
        if (this.itemPrice != null && this.quantity != null) {
            this.subtotal = this.itemPrice * this.quantity;
        }
    }
}
