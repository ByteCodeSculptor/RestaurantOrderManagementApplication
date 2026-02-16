package com.restaurants.demo.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/*
    ApiResponse is a generic class used to standardize the structure of API responses. It contains a success flag, a message, and an optional data payload of type T.
*/
@Builder
@Getter
@Setter
@AllArgsConstructor
public class ApiResponse<T> {

    private boolean success;
    private String message;
    private T data;
}

