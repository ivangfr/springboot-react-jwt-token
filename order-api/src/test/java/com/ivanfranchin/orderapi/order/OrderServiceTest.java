package com.ivanfranchin.orderapi.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@Import(OrderService.class)
class OrderServiceTest {

  @MockitoBean private OrderRepository orderRepository;

  @Autowired private OrderService orderService;

  // -- getOrders --

  @Test
  void getOrders_returnsAllOrdersDescending() {
    Order o1 = new Order("Order one");
    Order o2 = new Order("Order two");
    when(orderRepository.findAllByOrderByCreatedAtDesc()).thenReturn(List.of(o1, o2));

    List<Order> result = orderService.getOrders();

    assertThat(result).hasSize(2).containsExactly(o1, o2);
    verify(orderRepository).findAllByOrderByCreatedAtDesc();
    verifyNoMoreInteractions(orderRepository);
  }

  @Test
  void getOrders_returnsEmptyListWhenNoOrders() {
    when(orderRepository.findAllByOrderByCreatedAtDesc()).thenReturn(List.of());

    List<Order> result = orderService.getOrders();

    assertThat(result).isEmpty();
    verify(orderRepository).findAllByOrderByCreatedAtDesc();
    verifyNoMoreInteractions(orderRepository);
  }

  // -- countOrders --

  @Test
  void countOrders_delegatesToRepository() {
    when(orderRepository.count()).thenReturn(7L);

    assertThat(orderService.countOrders()).isEqualTo(7L);
    verify(orderRepository).count();
    verifyNoMoreInteractions(orderRepository);
  }

  // -- getOrdersContainingText --

  @Test
  void getOrdersContainingText_returnsMatchingOrders() {
    Order order = new Order("Buy iPhone");
    when(orderRepository.findByIdContainingOrDescriptionContainingIgnoreCaseOrderByCreatedAt(
            "iphone", "iphone"))
        .thenReturn(List.of(order));

    List<Order> result = orderService.getOrdersContainingText("iphone");

    assertThat(result).hasSize(1).containsExactly(order);
    verify(orderRepository)
        .findByIdContainingOrDescriptionContainingIgnoreCaseOrderByCreatedAt("iphone", "iphone");
    verifyNoMoreInteractions(orderRepository);
  }

  @Test
  void getOrdersContainingText_returnsEmptyWhenNoMatch() {
    when(orderRepository.findByIdContainingOrDescriptionContainingIgnoreCaseOrderByCreatedAt(
            "xyz", "xyz"))
        .thenReturn(List.of());

    List<Order> result = orderService.getOrdersContainingText("xyz");

    assertThat(result).isEmpty();
    verify(orderRepository)
        .findByIdContainingOrDescriptionContainingIgnoreCaseOrderByCreatedAt("xyz", "xyz");
    verifyNoMoreInteractions(orderRepository);
  }

  // -- validateAndGetOrder --

  @Test
  void validateAndGetOrder_returnsOrderWhenFound() {
    Order order = new Order("Buy iPhone");
    when(orderRepository.findById("abc-123")).thenReturn(Optional.of(order));

    Order result = orderService.validateAndGetOrder("abc-123");

    assertThat(result).isEqualTo(order);
    verify(orderRepository).findById("abc-123");
    verifyNoMoreInteractions(orderRepository);
  }

  @Test
  void validateAndGetOrder_throwsWhenNotFound() {
    when(orderRepository.findById("missing")).thenReturn(Optional.empty());

    assertThatThrownBy(() -> orderService.validateAndGetOrder("missing"))
        .isInstanceOf(OrderNotFoundException.class)
        .hasMessageContaining("missing");
    verify(orderRepository).findById("missing");
    verifyNoMoreInteractions(orderRepository);
  }

  // -- saveOrder --

  @Test
  void saveOrder_delegatesToRepositoryAndReturnsOrder() {
    Order order = new Order("Buy iPhone");
    when(orderRepository.save(order)).thenReturn(order);

    Order result = orderService.saveOrder(order);

    assertThat(result).isEqualTo(order);
    verify(orderRepository).save(order);
    verifyNoMoreInteractions(orderRepository);
  }

  // -- deleteOrder --

  @Test
  void deleteOrder_delegatesToRepository() {
    Order order = new Order("Buy iPhone");

    orderService.deleteOrder(order);

    verify(orderRepository).delete(order);
    verifyNoMoreInteractions(orderRepository);
  }
}
