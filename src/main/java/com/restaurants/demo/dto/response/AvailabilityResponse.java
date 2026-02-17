package com.restaurants.demo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class AvailabilityResponse {
    private Long id;
    private Boolean available;
}
