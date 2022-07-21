package com.ivanfranchin.orderapi.mapper;

import com.ivanfranchin.orderapi.model.User;
import com.ivanfranchin.orderapi.rest.dto.UserDto;
import org.mapstruct.Mapper;
import org.springframework.context.annotation.Configuration;

@Configuration
@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto toUserDto(User user);
}