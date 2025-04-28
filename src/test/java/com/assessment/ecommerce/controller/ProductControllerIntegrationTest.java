package com.assessment.ecommerce.controller;

import com.assessment.ecommerce.model.Product;
import com.assessment.ecommerce.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ProductControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    private final String USER = "user";
    private final String PASS = "password";

    @Test
    void getAll_requiresAuth_andReturnsProducts() throws Exception {
        Product p = new Product(1L, "A", "desc", 10.0, 5, "user");
        given(productService.getAllProducts()).willReturn(List.of(p));

        mockMvc.perform(get("/products").with(httpBasic(USER, PASS)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("[0].name").value("A"));
    }

    @Test
    void postProduct_requiresAuth_andCreates() throws Exception {
        Product input = new Product(null, "B", "d2", 20.0, 3, null);
        Product output = new Product(2L, "B", "d2", 20.0, 3, "user");
        given(productService.addProduct(any(Product.class))).willReturn(output);

        mockMvc.perform(post("/products")
                        .with(httpBasic(USER, PASS))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath(".id").value(2))
                .andExpect(jsonPath(".owner").value("user"));
    }

    @Test
    void putProduct_requiresAuth_andUpdates() throws Exception {
        Product updated = new Product(1L, "C", "d3", 30.0, 2, "user");
        given(productService.updateProduct(eq(1L), any(Product.class))).willReturn(updated);

        mockMvc.perform(put("/products/1")
                        .with(httpBasic(USER, PASS))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath(".name").value("C"));
    }

    @Test
    void deleteProduct_requiresAuth_andDeletes() throws Exception {
        mockMvc.perform(delete("/products/1").with(httpBasic(USER, PASS)))
                .andExpect(status().isOk());
    }
}