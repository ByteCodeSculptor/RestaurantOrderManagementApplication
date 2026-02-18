package com.restaurants.demo.exception;

import com.restaurants.demo.util.ApiResponse;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
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

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleParseError(
            HttpMessageNotReadableException ex) {

        return ResponseEntity.badRequest().body(
                ApiResponse.failure(
                        "Invalid request body. Check field data types."
                )
        );
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleConstraintViolation(
            ConstraintViolationException ex) {

        return ResponseEntity.badRequest().body(
                ApiResponse.failure(
                        ex.getMessage()
                )
        );
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(ValidationException ex){
        return ResponseEntity.badRequest().body(ApiResponse.failure(ex.getMessage()));
    }

}
