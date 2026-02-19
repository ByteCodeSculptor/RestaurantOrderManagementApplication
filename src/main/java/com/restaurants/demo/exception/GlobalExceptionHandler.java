package com.restaurants.demo.exception;

import com.restaurants.demo.util.ApiResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

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

        String cleanMessage = ex.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .findFirst()
                .orElse("Validation error");

        return ResponseEntity.badRequest().body(
                ApiResponse.failure(cleanMessage)
        );
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(ValidationException ex){
        return ResponseEntity.badRequest().body(ApiResponse.failure(ex.getMessage()));
    }

    // Errors in input validations
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex) {

        String allErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .collect(Collectors.joining(", "));

        return ResponseEntity.badRequest().body(
                ApiResponse.failure(allErrors)
        );
    }

    // Exception in param status entered
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Void>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {

        String message;

        if (ex.getRequiredType() != null && ex.getRequiredType().isEnum()) {
            message = "Invalid value for parameter '" + ex.getName() +
                    "'. Allowed values are: " +
                    Arrays.toString(ex.getRequiredType().getEnumConstants());
        } else {
            message = "Invalid value for parameter '" + ex.getName() + "'";
        }

        return ResponseEntity.badRequest().body(ApiResponse.failure(message));
    }

}
