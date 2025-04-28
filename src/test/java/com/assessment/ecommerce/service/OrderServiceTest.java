package com.assessment.ecommerce.service;

import com.assessment.ecommerce.model.CartItem;
import com.assessment.ecommerce.model.OrderEntity;
import com.assessment.ecommerce.model.Product;
import com.assessment.ecommerce.repository.OrderRepository;
import com.assessment.ecommerce.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderServiceTest {

    private OrderRepository orderRepository;
    private ProductRepository productRepository;
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        orderRepository = mock(OrderRepository.class);
        productRepository = mock(ProductRepository.class);
        orderService = new OrderService(orderRepository, productRepository);
    }

    @Test
    void placeOrder_shouldPlaceOrderSuccessfully() {
        CartItem cartItem = new CartItem(1L, 2);
        Product product = new Product(1L, "Product 1", "desc", 50.0, 5, "owner");

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(orderRepository.save(any(OrderEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OrderEntity order = orderService.placeOrder("testUser", List.of(cartItem));

        assertThat(order.getCustomer()).isEqualTo("testUser");
        assertThat(order.getTotal()).isEqualTo(100.0);
        assertThat(order.getProductIds()).containsExactly(1L);

        verify(productRepository, times(1)).save(any(Product.class));
        verify(orderRepository, times(1)).save(any(OrderEntity.class));
    }

    @Test
    void placeOrder_shouldThrowExceptionWhenProductNotFound() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        CartItem cartItem = new CartItem(1L, 2);

        assertThatThrownBy(() -> orderService.placeOrder("testUser", List.of(cartItem)))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Product not found with id: 1");
    }

    @Test
    void placeOrder_shouldThrowExceptionWhenNotEnoughStock() {
        Product product = new Product(1L, "Product 1", "desc", 50.0, 1, "owner");

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        CartItem cartItem = new CartItem(1L, 2);

        assertThatThrownBy(() -> orderService.placeOrder("testUser", List.of(cartItem)))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Not enough quantity for product");
    }

    @Test
    void subscribeToOrderStream_shouldReturnEmitter() {
        SseEmitter emitter = orderService.subscribeToOrderStream();

        assertThat(emitter).isNotNull();
    }
}
