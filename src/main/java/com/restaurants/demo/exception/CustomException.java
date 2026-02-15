package com.restaurants.demo.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/*
    CustomException is a custom runtime exception that includes an HTTP status code. It is used to represent application-specific exceptions that can be thrown during the execution of the application, allowing for more informative error handling and responses.
*/
@Getter
public class CustomException extends RuntimeException {

    private final HttpStatus status;

    public CustomException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
}
