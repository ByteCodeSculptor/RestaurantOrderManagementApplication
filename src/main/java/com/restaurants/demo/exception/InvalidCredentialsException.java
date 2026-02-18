package com.restaurants.demo.exception;

import org.springframework.http.HttpStatus;

public class InvalidCredentialsException extends ValidationException {
    
    public InvalidCredentialsException(String message) {
        super(message, HttpStatus.UNAUTHORIZED);
    }
}
