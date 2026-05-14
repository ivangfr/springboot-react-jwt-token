package com.ivanfranchin.orderapi.order;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, String> {

  List<Order> findAllByOrderByCreatedAtDesc();

  List<Order> findByIdContainingOrDescriptionContainingIgnoreCaseOrderByCreatedAt(
      String id, String description);
}
