package com.mycompany.orderapi.repository;

import com.mycompany.orderapi.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, String> {
}
