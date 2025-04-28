package com.assessment.ecommerce.service;

import com.assessment.ecommerce.model.InsightMetrics;
import com.assessment.ecommerce.model.OrderEntity;
import com.assessment.ecommerce.repository.OrderRepository;
import com.assessment.ecommerce.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;
import java.util.List;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

class InsightServiceTest {

    private ProductRepository productRepository;
    private OrderRepository orderRepository;
    private InsightService insightService;

    @BeforeEach
    void setUp() {
        productRepository = mock(ProductRepository.class);
        orderRepository = mock(OrderRepository.class);
        insightService = new InsightService(productRepository, orderRepository);
    }

    @Test
    void calculateMetrics_shouldReturnCorrectMetrics() {
        String owner = "testUser";

        when(productRepository.countByOwner(owner)).thenReturn(5L);
        when(orderRepository.countByCustomer(owner)).thenReturn(2L);

        OrderEntity order1 = new OrderEntity(1L, owner, 100.50, List.of(1L, 2L));
        OrderEntity order2 = new OrderEntity(2L, owner, 150.25, List.of(3L));
        when(orderRepository.findByCustomer(owner)).thenReturn(List.of(order1, order2));

        InsightMetrics metrics = insightService.calculateMetrics(owner);

        assertThat(metrics.getTotalProducts()).isEqualTo(5);
        assertThat(metrics.getTotalOrders()).isEqualTo(2);
        assertThat(metrics.getTotalRevenue()).isEqualTo(250.75);
        assertThat(metrics.getAverageOrderValue()).isEqualTo(125.38);
    }

    @Test
    void calculateMetrics_whenNoOrders_shouldHandleZeroDivision() {
        String owner = "noOrdersUser";

        when(productRepository.countByOwner(owner)).thenReturn(2L);
        when(orderRepository.countByCustomer(owner)).thenReturn(0L);
        when(orderRepository.findByCustomer(owner)).thenReturn(Collections.emptyList());

        InsightMetrics metrics = insightService.calculateMetrics(owner);

        assertThat(metrics.getTotalProducts()).isEqualTo(2);
        assertThat(metrics.getTotalOrders()).isEqualTo(0);
        assertThat(metrics.getTotalRevenue()).isEqualTo(0.00);
        assertThat(metrics.getAverageOrderValue()).isEqualTo(0.00);
    }
}
