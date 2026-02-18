package com.restaurants.demo.dto.response;


import java.time.LocalDateTime;


/*
    ErrorResponse is a simple DTO used to represent error responses in the application. It contains the HTTP status code, an error message, and a timestamp indicating when the error occurred.
*/

public class ErrorResponse {

    private int status;
    private String message;
    private LocalDateTime timestamp;

    public ErrorResponse(int status, String message) {
        this.status = status;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

    public int getStatus() { return status; }
    public String getMessage() { return message; }
    public LocalDateTime getTimestamp() { return timestamp; }
}
