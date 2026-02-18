package com.restaurants.demo.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;


/*
    SignupRequest is a simple DTO used for registering new users. It includes email, password, and role fields, with validation annotations to ensure proper input.
*/
@Data
public class SignupRequest {
    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String password;

    @NotBlank
    private String role; // Expecting "ADMIN" or "STAFF"
}
