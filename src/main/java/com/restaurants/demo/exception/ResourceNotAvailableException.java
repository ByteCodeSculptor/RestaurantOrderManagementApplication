package com.restaurants.demo.exception;

import org.springframework.http.HttpStatus;

public class ResourceNotAvailableException extends BusinessException{
    public ResourceNotAvailableException(String message) {
        super(message, HttpStatus.UNPROCESSABLE_ENTITY);
    }
}
