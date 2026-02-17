package com.restaurants.demo.exception;

import org.springframework.http.HttpStatus;

public class MenuItemNotFoundException extends BaseException {
    public MenuItemNotFoundException(Long id) {
        super("Menu item with ID " + id + " not found", HttpStatus.NOT_FOUND);
    }
}