package com.assessment.ecommerce.repository;

import com.assessment.ecommerce.model.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
    List<OrderEntity> findByCustomer(String customer);
    long countByCustomer(String customer);
}
