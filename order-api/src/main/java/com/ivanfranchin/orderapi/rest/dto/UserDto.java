package com.ivanfranchin.orderapi.rest.dto;

import com.ivanfranchin.orderapi.order.Order;
import com.ivanfranchin.orderapi.security.CustomUserDetails;
import com.ivanfranchin.orderapi.user.User;

import java.time.Instant;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;

public record UserDto(Long id, String username, String name, String email, String role, List<OrderDto> orders) {

    public record OrderDto(String id, String description, Instant createdAt) {

        public static OrderDto from(Order order) {
            return new OrderDto(order.getId(), order.getDescription(), order.getCreatedAt());
        }
    }

    public static UserDto from(User user) {
        List<OrderDto> orders = user.getOrders().stream()
                .map(OrderDto::from)
                .toList();

        return new UserDto(
                user.getId(),
                user.getUsername(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                orders
        );
    }

    public static UserDto from(CustomUserDetails userDetails) {
        String role = userDetails.getAuthorities().stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElse(null);

        return new UserDto(
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getName(),
                userDetails.getEmail(),
                role,
                List.of()
        );
    }
}