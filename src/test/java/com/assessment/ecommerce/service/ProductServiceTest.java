package com.assessment.ecommerce.service;

import com.assessment.ecommerce.model.Product;
import com.assessment.ecommerce.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductServiceTest {

    private ProductRepository productRepository;
    private ProductService productService;

    @BeforeEach
    void setUp() {
        productRepository = mock(ProductRepository.class);
        productService = new ProductService(productRepository);
    }

    @Test
    void addProduct_shouldSaveAndReturnProduct() {
        Product product = new Product(1L, "Product 1", "desc", 50.0, 10, "owner");

        when(productRepository.save(product)).thenReturn(product);

        Product savedProduct = productService.addProduct(product);

        assertThat(savedProduct).isEqualTo(product);
        verify(productRepository, times(1)).save(product);
    }

    @Test
    void updateProduct_shouldUpdateAndReturnProduct() {
        Product existingProduct = new Product(1L, "Old Name", "Old Desc", 30.0, 5, "owner");
        Product updatedProduct = new Product(null, "New Name", "New Desc", 50.0, 5, "owner");

        when(productRepository.findById(1L)).thenReturn(Optional.of(existingProduct));

        Product result = productService.updateProduct(1L, updatedProduct);

        assertThat(result.getName()).isEqualTo("New Name");
        assertThat(result.getPrice()).isEqualTo(50.0);
        assertThat(result.getDescription()).isEqualTo("New Desc");

        verify(productRepository, times(0)).save(any());
    }

    @Test
    void deleteProduct_shouldDeleteProduct() {
        when(productRepository.existsById(1L)).thenReturn(true);

        productService.deleteProduct(1L);

        verify(productRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteProduct_shouldThrowExceptionWhenProductNotFound() {
        when(productRepository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> productService.deleteProduct(1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Product not found with id 1");
    }

    @Test
    void getAllProducts_shouldReturnProducts() {
        List<Product> products = List.of(new Product(1L, "Product 1", "desc", 50.0, 10, "owner"));

        when(productRepository.findAll()).thenReturn(products);

        List<Product> result = productService.getAllProducts();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Product 1");
    }

    @Test
    void subscribeToProductStream_shouldReturnEmitter() {
        SseEmitter emitter = productService.subscribeToProductStream();

        assertThat(emitter).isNotNull();
    }
}
