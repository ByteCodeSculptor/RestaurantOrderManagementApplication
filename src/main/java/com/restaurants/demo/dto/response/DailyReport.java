package com.restaurants.demo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DailyReport {

    private Long totalOrders;

    private Double totalRevenue;
}
