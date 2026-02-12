package com.restaurants.demo.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class MenuResponse {

    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Boolean available;
}
