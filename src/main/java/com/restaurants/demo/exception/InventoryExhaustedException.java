package com.restaurants.demo.exception;

import org.springframework.http.HttpStatus;

public class InventoryExhaustedException extends BaseException {
    public InventoryExhaustedException(String itemName) {
        super("Item " + itemName + " is out of stock", HttpStatus.BAD_REQUEST);
    }
}