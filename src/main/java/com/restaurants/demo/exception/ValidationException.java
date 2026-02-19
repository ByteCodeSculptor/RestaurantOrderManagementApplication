package com.restaurants.demo.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class ValidationException extends RuntimeException {

    private final HttpStatus status;

    public ValidationException(String message,HttpStatus status) {
        super(message);
        this.status = status;
    }
}
