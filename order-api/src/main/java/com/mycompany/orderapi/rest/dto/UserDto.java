package com.mycompany.orderapi.rest.dto;

import lombok.Data;

import java.time.ZonedDateTime;
import java.util.List;

@Data
public class UserDto {

    private Long id;
    private String username;
    private String name;
    private String email;
    private String role;
    private List<OrderDto> orders;

    @Data
    public static final class OrderDto {
        private String id;
        private String description;
        private ZonedDateTime createdAt;
    }
}