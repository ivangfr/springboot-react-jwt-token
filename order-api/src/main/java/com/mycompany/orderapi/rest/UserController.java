package com.mycompany.orderapi.rest;

import com.mycompany.orderapi.exception.UserNotFoundException;
import com.mycompany.orderapi.model.User;
import com.mycompany.orderapi.rest.dto.CreateUserDto;
import com.mycompany.orderapi.security.WebSecurityConfig;
import com.mycompany.orderapi.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<User> getUsers() {
        return userService.getUsers();
    }

    @GetMapping("/{username}")
    public User getUser(@PathVariable String username) throws UserNotFoundException {
        return userService.validateAndGetUserByUsername(username);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public User createUser(@Valid @RequestBody CreateUserDto createUserDto) {
        return userService.saveUser(new User(createUserDto.getUsername(), createUserDto.getPassword(), WebSecurityConfig.USER));
    }

    @DeleteMapping("/{username}")
    public User deleteUser(@PathVariable String username) throws UserNotFoundException {
        User user = userService.validateAndGetUserByUsername(username);
        userService.deleteUser(user);
        return user;
    }

}
