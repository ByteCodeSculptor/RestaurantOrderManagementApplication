package com.restaurants.demo.exception;

import com.restaurants.demo.util.ResponseHelper;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/*
    GlobalExceptionHandler is a centralized exception handler for the application. It uses @RestControllerAdvice to handle exceptions thrown by any controller and return consistent error responses.
    It handles CustomException, AccessDeniedException, and any generic Exception, returning appropriate HTTP status codes and error messages in a JSON format.
*/
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public Object handleCustomException(CustomException ex) {
        return ResponseHelper.error(ex.getMessage(), ex.getStatus());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public Object handleAccessDenied() {
        return ResponseHelper.error("You are not authorized", HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(Exception.class)
    public Object handleGenericException(Exception ex) {
        return ResponseHelper.error(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
