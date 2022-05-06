package com.mycompany.orderapi.repository;

import com.mycompany.orderapi.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, String> {

    List<Order> findAllByOrderByCreatedAtDesc();

    // Add @Param as a workaround for https://github.com/spring-projects/spring-data-jpa/issues/2512
    // Remove them when this issue https://github.com/spring-projects/spring-data-jpa/issues/2519 is closed
    List<Order> findByIdContainingOrDescriptionContainingOrderByCreatedAt(@Param("id") String id, @Param("description") String description);
}
