package com.ivanfranchin.orderapi.mapper;

import com.ivanfranchin.orderapi.model.Order;
import com.ivanfranchin.orderapi.model.User;
import com.ivanfranchin.orderapi.rest.dto.UserDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserMapperImpl implements UserMapper {

    @Override
    public UserDto toUserDto(User user) {
        List<UserDto.OrderDto> orders = user.getOrders().stream().map(this::toUserDtoOrderDto).toList();
        return new UserDto(user.getId(), user.getUsername(), user.getName(), user.getEmail(), user.getRole(), orders);
    }

    private UserDto.OrderDto toUserDtoOrderDto(Order order) {
        return new UserDto.OrderDto(order.getId(), order.getDescription(), order.getCreatedAt());
    }
}
