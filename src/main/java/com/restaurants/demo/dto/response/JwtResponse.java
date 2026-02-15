package com.restaurants.demo.dto.response;

import lombok.Data;


/*
    JwtResponse is a simple DTO used to represent the response after a successful login. It contains the JWT token, the token type, the user's email, and their role.
*/
@Data
public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private String email;
    private String role;

    public JwtResponse(String accessToken, String email, String role) {
        this.token = accessToken;
        this.email = email;
        this.role = role;
    }
}