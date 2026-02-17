package com.restaurants.demo.exception;

import com.restaurants.demo.util.OrderStatus;
import org.springframework.http.HttpStatus;

public class InvalidTransitionException extends BaseException {
    public InvalidTransitionException(OrderStatus initialStatus, OrderStatus finalStatus) {
        super("Transition from " + initialStatus + " to " + finalStatus + " is invalid!", HttpStatus.BAD_REQUEST);
    }
}
