package com.assessment.ecommerce.service;

import com.assessment.ecommerce.model.InsightMetrics;
import com.assessment.ecommerce.model.OrderEntity;
import com.assessment.ecommerce.repository.OrderRepository;
import com.assessment.ecommerce.repository.ProductRepository;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class InsightService {
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    public InsightMetrics calculateMetrics(String owner) {
        long totalProducts = productRepository.countByOwner(owner);
        long totalOrders = orderRepository.countByCustomer(owner);

        double totalRevenue = orderRepository.findByCustomer(owner)
                .stream()
                .mapToDouble(OrderEntity::getTotal)
                .sum();

        double averageOrderValue = totalOrders > 0 ? totalRevenue / totalOrders : 0;

        String formattedTotalRevenue = String.format("%.2f", totalRevenue);
        String formattedAverageOrderValue = String.format("%.2f", averageOrderValue);

        return new InsightMetrics(totalProducts, totalOrders, Double.parseDouble(formattedTotalRevenue), Double.parseDouble(formattedAverageOrderValue));
    }

}