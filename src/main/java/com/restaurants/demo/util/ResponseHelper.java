package com.restaurants.demo.util;


import com.restaurants.demo.exception.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResponseHelper {

    // Success response with data
    public static <T> ResponseEntity<ApiResponse<T>> success(T data, String message) {
        ApiResponse<T> response = new ApiResponse<>(true, message, data);
        return ResponseEntity.ok(response); // status 200
    }

    // Success response without data
    public static ResponseEntity<ApiResponse<Object>> success(String message) {
        ApiResponse<Object> response = new ApiResponse<>(true, message, null);
        return ResponseEntity.ok(response);
    }

    // Error response
    public static ResponseEntity<ApiResponse<Object>> error(String message, HttpStatus status) {
        ApiResponse<Object> response = new ApiResponse<>(false, message, null);
        return new ResponseEntity<>(response, status);
    }
}
