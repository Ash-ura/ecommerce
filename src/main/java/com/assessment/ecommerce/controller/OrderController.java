package com.assessment.ecommerce.controller;

import com.assessment.ecommerce.model.CartItem;
import com.assessment.ecommerce.model.OrderEntity;
import com.assessment.ecommerce.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/cart")
    public OrderEntity placeOrder(@RequestBody List<CartItem> cartItems, Authentication auth) {
        return orderService.placeOrder(auth.getName(), cartItems);
    }

    @GetMapping("/stream")
    public SseEmitter stream() {
        return orderService.subscribeToOrderStream();
    }
}