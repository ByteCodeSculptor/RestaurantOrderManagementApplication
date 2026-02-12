package com.restaurants.demo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "order_item")
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

    @Column(name = "menu_item_name", nullable = false, length = 150)
    private String itemName;

    @DecimalMin(value = "1.0")
    @Column(name = "price_at_order_time", nullable = false)
    private Double itemPrice;

    @Min(value = 1)
    private Integer quantity;

    private Double subtotal;
}
