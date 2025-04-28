package com.assessment.ecommerce.service;

import com.assessment.ecommerce.model.Product;
import com.assessment.ecommerce.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository repository;
    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    public Product addProduct(Product product) {
        Product saved = repository.save(product);
        notifyEmitters();
        return saved;
    }

    @Transactional
    public Product updateProduct(Long id, Product product) {
        Product existingProduct = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id " + id));

        existingProduct.setName(product.getName());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setDescription(product.getDescription());
        notifyEmitters();
        return existingProduct;
    }


    @Transactional
    public void deleteProduct(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Product not found with id " + id);
        }
        repository.deleteById(id);
        notifyEmitters();
    }


    public List<Product> getAllProducts() {
        return repository.findAll();
    }

    @CacheEvict(value = "products", allEntries = true)
    public void notifyEmitters() {
        List<Product> data = repository.findAll();
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(data);
            } catch (IOException e) {
                emitters.remove(emitter);
            }
        }
    }

    public SseEmitter subscribeToProductStream() {
        SseEmitter emitter = new SseEmitter();
        emitters.add(emitter);
        emitter.onCompletion(() -> emitters.remove(emitter));
        return emitter;
    }
}
