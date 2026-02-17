package com.restaurants.demo.service;

import java.util.List;

import com.restaurants.demo.dto.request.LoginRequest;
import com.restaurants.demo.dto.request.SignupRequest;
import com.restaurants.demo.dto.response.BulkRegistrationResponse;
import com.restaurants.demo.dto.response.JwtResponse;
import com.restaurants.demo.dto.response.MessageResponse;

public interface AuthService {
    JwtResponse authenticateUser(LoginRequest loginRequest);
    MessageResponse registerUser(SignupRequest signupRequest);
    BulkRegistrationResponse registerUsers(List<SignupRequest> signupRequests);
}