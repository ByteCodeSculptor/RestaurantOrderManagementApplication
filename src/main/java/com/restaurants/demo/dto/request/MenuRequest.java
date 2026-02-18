package com.restaurants.demo.dto.request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

/*
    MenuRequest is a DTO used for creating and updating menu items. 
 */
@Getter
@Setter
public class MenuRequest {

    @NotBlank(message = "Name is required")
    private String name;

    private String description;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be positive")
    private Long price;

    @NotNull(message = "Availability is required")
    private Boolean available;
}
