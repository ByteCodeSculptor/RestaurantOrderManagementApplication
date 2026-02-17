package com.restaurants.demo.util;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/*
    ApiResponse is a generic class used to standardize the structure of API responses. It contains a success flag, a message, and an optional data payload of type T.
*/
@Builder
@Getter
@Setter
public class ApiResponse<T> {

    private final boolean success;
    private final String message;
    private final T data;

    public static <T> ApiResponse<T> success(T data, String message){
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> failure(String message){
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .build();
    }
}

