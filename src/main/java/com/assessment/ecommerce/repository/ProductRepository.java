package com.assessment.ecommerce.repository;

import com.assessment.ecommerce.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByOwner(String owner);
    long countByOwner(String owner);
}