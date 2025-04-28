package com.assessment.ecommerce.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InsightMetrics {
    private long totalProducts;
    private long totalOrders;
    private double totalRevenue;
    private double averageOrderValue;
}
