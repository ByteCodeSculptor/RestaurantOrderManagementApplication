package com.restaurants.demo.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BulkRegistrationResponse {
    private List<String> successfulEmails;
    private List<RegistrationError> failures;

    @Data
    @AllArgsConstructor
    public static class RegistrationError {
        private String email;
        private String errorMessage;
    }
}