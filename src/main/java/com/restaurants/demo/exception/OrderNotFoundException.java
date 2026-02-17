package com.restaurants.demo.exception;

import org.springframework.http.HttpStatus;

public class OrderNotFoundException extends BaseException {
    public OrderNotFoundException(Long orderId) {
        super("Order " + orderId + " does not exist", HttpStatus.NOT_FOUND);
    }
}