package com.ivanfranchin.orderapi.rest.dto;

import java.time.Instant;

public record OrderDto(String id, String description, OrderDto.UserDto user, Instant createdAt) {

    public record UserDto(String username) {
    }
}