package com.restaurants.demo.exception;

import com.restaurants.demo.util.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public <T> ResponseEntity<ApiResponse<T>> handleBusinessException(
            BusinessException ex) {

        ApiResponse<T> response = ApiResponse.failure(ex.getMessage());

        return ResponseEntity
                .status(ex.getStatus())
                .body(response);
    }
}
