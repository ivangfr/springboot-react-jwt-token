package com.ivanfranchin.orderapi.rest.dto;

import java.time.Instant;
import java.util.List;

public record UserDto(Long id, String username, String name, String email, String role, List<OrderDto> orders) {

    public record OrderDto(String id, String description, Instant createdAt) {
    }
}