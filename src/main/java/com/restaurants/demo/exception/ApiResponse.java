package com.restaurants.demo.exception;

import lombok.Builder;
import lombok.Getter;

/*
    ApiResponse is a generic class used to standardize the structure of API responses. It contains a success flag, a message, and an optional data payload of type T.
*/
@Getter
@Builder
public class ApiResponse<T> {

    private boolean success;
    private String message;
    private T data;

    public ApiResponse(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public T getData() { return data; }
}

