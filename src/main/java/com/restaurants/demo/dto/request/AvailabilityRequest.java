package com.restaurants.demo.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/*
    AvailabilityRequest is a simple DTO used to update the availability status of menu items.
 */
@Getter
@Setter
public class AvailabilityRequest {

    @NotNull(message = "Availability is required")
    private Boolean available;
}
