package com.assessment.ecommerce.service;

import com.assessment.ecommerce.model.CartItem;
import com.assessment.ecommerce.model.OrderEntity;
import com.assessment.ecommerce.model.Product;
import com.assessment.ecommerce.repository.OrderRepository;
import com.assessment.ecommerce.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final List<SseEmitter> orderEmitters = new CopyOnWriteArrayList<>();

    @Transactional
    public OrderEntity placeOrder(String customer, List<CartItem> cartItems) {
        double total = 0;
        List<Long> productIds = new ArrayList<>();

        for (CartItem item : cartItems) {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found with id: " + item.getProductId()));

            if (product.getStock() < item.getQuantity()) {
                throw new RuntimeException("Not enough quantity for product: " + product.getName());
            }

            product.setStock(product.getStock() - item.getQuantity());
            productRepository.save(product);

            total += product.getPrice() * item.getQuantity();
            productIds.add(product.getId());
        }

        OrderEntity orderEntity = new OrderEntity(null, customer, total, productIds);
        OrderEntity savedOrderEntity = orderRepository.save(orderEntity);

        notifyOrderEmitters(savedOrderEntity);

        return savedOrderEntity;
    }


    private void notifyOrderEmitters(OrderEntity orderEntity) {
        for (SseEmitter emitter : orderEmitters) {
            try {
                emitter.send(orderEntity);
            } catch (IOException e) {
                orderEmitters.remove(emitter);
            }
        }
    }

    public SseEmitter subscribeToOrderStream() {
        SseEmitter emitter = new SseEmitter();
        orderEmitters.add(emitter);
        emitter.onCompletion(() -> orderEmitters.remove(emitter));
        return emitter;
    }
}