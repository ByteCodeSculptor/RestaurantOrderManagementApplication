package com.restaurants.demo.dto.response;

import java.util.List;

import lombok.Data;


/*
    JwtResponse is a simple DTO used to represent the response after a successful login. It contains the JWT token, the token type, the user's email, and their role.
*/
@Data
public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private String email;
    private List<String> roles;

    public JwtResponse(String accessToken, String email, List<String> roles) {
        this.token = accessToken;
        this.email = email;
        this.roles = roles;
    }
}