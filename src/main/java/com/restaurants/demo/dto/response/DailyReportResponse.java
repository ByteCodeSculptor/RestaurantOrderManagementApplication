package com.restaurants.demo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
public class DailyReportResponse {
    private BigDecimal totalRevenue;
    private Long totalOrders;
}