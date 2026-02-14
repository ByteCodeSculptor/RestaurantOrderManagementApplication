package com.restaurants.demo.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;



@Getter
@Setter
public class AvailabilityRequest {

    @NotNull(message = "Availability is required")
    private Boolean available;
}
