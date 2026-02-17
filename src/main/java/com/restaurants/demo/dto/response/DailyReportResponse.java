package com.restaurants.demo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

/*
    DailyReportResponse is a simple DTO used to represent the daily report of the restaurant. It contains the total revenue and the total number of orders for the day.
*/

@Getter
@Setter
@AllArgsConstructor
public class DailyReportResponse {
    private double totalRevenue;
    private Long totalOrders;
}