package com.ivanfranchin.orderapi.order;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    // -- getOrders --

    @Test
    void getOrders_returnsAllOrdersDescending() {
        Order o1 = new Order("Order one");
        Order o2 = new Order("Order two");
        when(orderRepository.findAllByOrderByCreatedAtDesc()).thenReturn(List.of(o1, o2));

        List<Order> result = orderService.getOrders();

        assertThat(result).hasSize(2).containsExactly(o1, o2);
    }

    @Test
    void getOrders_returnsEmptyListWhenNoOrders() {
        when(orderRepository.findAllByOrderByCreatedAtDesc()).thenReturn(List.of());

        List<Order> result = orderService.getOrders();

        assertThat(result).isEmpty();
    }

    // -- countOrders --

    @Test
    void countOrders_delegatesToRepository() {
        when(orderRepository.count()).thenReturn(7L);

        assertThat(orderService.countOrders()).isEqualTo(7L);
    }

    // -- getOrdersContainingText --

    @Test
    void getOrdersContainingText_returnsMatchingOrders() {
        Order order = new Order("Buy iPhone");
        when(orderRepository.findByIdContainingOrDescriptionContainingIgnoreCaseOrderByCreatedAt("iphone", "iphone"))
                .thenReturn(List.of(order));

        List<Order> result = orderService.getOrdersContainingText("iphone");

        assertThat(result).hasSize(1).containsExactly(order);
    }

    @Test
    void getOrdersContainingText_returnsEmptyWhenNoMatch() {
        when(orderRepository.findByIdContainingOrDescriptionContainingIgnoreCaseOrderByCreatedAt("xyz", "xyz"))
                .thenReturn(List.of());

        List<Order> result = orderService.getOrdersContainingText("xyz");

        assertThat(result).isEmpty();
    }

    // -- validateAndGetOrder --

    @Test
    void validateAndGetOrder_returnsOrderWhenFound() {
        Order order = new Order("Buy iPhone");
        when(orderRepository.findById("abc-123")).thenReturn(Optional.of(order));

        Order result = orderService.validateAndGetOrder("abc-123");

        assertThat(result).isEqualTo(order);
    }

    @Test
    void validateAndGetOrder_throwsWhenNotFound() {
        when(orderRepository.findById("missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.validateAndGetOrder("missing"))
                .isInstanceOf(OrderNotFoundException.class)
                .hasMessageContaining("missing");
    }

    // -- saveOrder --

    @Test
    void saveOrder_delegatesToRepositoryAndReturnsOrder() {
        Order order = new Order("Buy iPhone");
        when(orderRepository.save(order)).thenReturn(order);

        Order result = orderService.saveOrder(order);

        assertThat(result).isEqualTo(order);
        verify(orderRepository).save(order);
    }

    // -- deleteOrder --

    @Test
    void deleteOrder_delegatesToRepository() {
        Order order = new Order("Buy iPhone");

        orderService.deleteOrder(order);

        verify(orderRepository).delete(order);
    }
}
