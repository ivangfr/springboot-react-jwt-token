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
        if (user == null) {
            return null;
        }
        List<UserDto.OrderDto> orders = user.getOrders().stream().map(this::toUserDtoOrderDto).toList();
        return new UserDto(user.getId(), user.getUsername(), user.getName(), user.getEmail(), user.getRole(), orders);
    }

    private UserDto.OrderDto toUserDtoOrderDto(Order order) {
        if (order == null) {
            return null;
        }
        return new UserDto.OrderDto(order.getId(), order.getDescription(), order.getCreatedAt());
    }
}
