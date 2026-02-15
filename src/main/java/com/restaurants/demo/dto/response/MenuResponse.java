package com.restaurants.demo.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


import java.math.BigDecimal;


/*
    MenuResponse is a simple DTO used to represent menu items in the response. It contains the menu item ID, name, description, price, and availability status.
*/

@Getter
@Setter
@Builder
public class MenuResponse {

    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Boolean available;
}
