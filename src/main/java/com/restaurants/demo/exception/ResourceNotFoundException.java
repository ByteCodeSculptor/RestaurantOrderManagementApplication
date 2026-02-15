package com.restaurants.demo.exception;


/*
    ResourceNotFoundException is a custom runtime exception that is thrown when a requested resource (such as an order, menu item, or user) cannot be found in the database. It extends RuntimeException and provides a constructor that accepts a message describing the error.
*/
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
