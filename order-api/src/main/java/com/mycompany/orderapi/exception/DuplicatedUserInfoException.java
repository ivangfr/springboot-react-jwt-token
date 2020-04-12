package com.mycompany.orderapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class DuplicatedUserInfoException extends Exception {

    public DuplicatedUserInfoException(String message) {
        super(message);
    }
}
