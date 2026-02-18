package com.restaurants.demo.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.restaurants.demo.dto.request.LoginRequest;
import com.restaurants.demo.dto.request.SignupRequest;
import com.restaurants.demo.dto.response.BulkRegistrationResponse;
import com.restaurants.demo.dto.response.JwtResponse;
import com.restaurants.demo.service.AuthService;
import com.restaurants.demo.util.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/auth/login")
    public ResponseEntity<ApiResponse<JwtResponse>> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        JwtResponse response = authService.authenticateUser(loginRequest);
        return ResponseEntity.ok(ApiResponse.success(response, "Successfully logged in"));
    }

    @PostMapping("/admin/register")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<BulkRegistrationResponse>> registerUsers(@Valid @RequestBody List<SignupRequest> signUpRequests) {
        BulkRegistrationResponse response = authService.registerUsers(signUpRequests);
        return ResponseEntity.ok(ApiResponse.success(response, "Bulk registration completed"));
    }
}
