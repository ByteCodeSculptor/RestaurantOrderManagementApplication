package com.restaurants.demo.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


import java.math.BigDecimal;


@Getter
@Setter
@Builder
public class MenuResponse {

    private Long id;
    private String name;
    private String description;
    private Long price;
    private Boolean available;
}
