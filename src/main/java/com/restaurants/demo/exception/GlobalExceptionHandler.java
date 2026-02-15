package com.restaurants.demo.exception;

import com.restaurants.demo.util.ResponseHelper;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

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
