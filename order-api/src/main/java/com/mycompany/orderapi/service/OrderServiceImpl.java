package com.mycompany.orderapi.service;

import com.mycompany.orderapi.exception.OrderNotFoundException;
import com.mycompany.orderapi.model.Order;
import com.mycompany.orderapi.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    @Override
    public List<Order> getOrders() {
        return orderRepository.findAllByOrderByCreatedAtDesc();
    }

    @Override
    public List<Order> getOrdersContainingText(String text) {
        return orderRepository.findByIdContainingOrDescriptionContainingOrderByCreatedAt(text, text);
    }

    @Override
    public Order validateAndGetOrder(String id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(String.format("Order with id %s not found", id)));
    }

    @Override
    public Order saveOrder(Order order) {
        return orderRepository.save(order);
    }

    @Override
    public void deleteOrder(Order order) {
        orderRepository.delete(order);
    }
}
