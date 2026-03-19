package com.ivanfranchin.orderapi.user;

import com.ivanfranchin.orderapi.rest.dto.SignUpRequest;

import java.util.List;
import java.util.Optional;

public interface UserService {

    List<User> getUsers();

    long countUsers();

    Optional<User> getUserByUsername(String username);

    boolean hasUserWithUsername(String username);

    boolean hasUserWithEmail(String email);

    User validateAndGetUserByUsername(String username);

    User saveUser(User user);

    User createUser(SignUpRequest request, String encodedPassword);

    void deleteUser(User user);
}
