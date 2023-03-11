package com.ivanfranchin.orderapi.mapper;

import com.ivanfranchin.orderapi.model.User;
import com.ivanfranchin.orderapi.rest.dto.UserDto;

public interface UserMapper {

    UserDto toUserDto(User user);
}