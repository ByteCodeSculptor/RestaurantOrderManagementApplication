package com.restaurants.demo.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.restaurants.demo.dto.request.LoginRequest;
import com.restaurants.demo.dto.request.SignupRequest;
import com.restaurants.demo.dto.response.BulkRegistrationResponse;
import com.restaurants.demo.dto.response.BulkRegistrationResponse.RegistrationError;
import com.restaurants.demo.dto.response.JwtResponse;
import com.restaurants.demo.dto.response.MessageResponse;
import com.restaurants.demo.entity.ERole;
import com.restaurants.demo.entity.User;
import com.restaurants.demo.repository.UserRepository;
import com.restaurants.demo.security.jwt.JwtUtils;
import com.restaurants.demo.service.AuthService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final JwtUtils jwtUtils;

    @Override
    public BulkRegistrationResponse registerUsers(List<SignupRequest> signupRequests) {
        List<String> successes = new ArrayList<>();
        List<RegistrationError> failures = new ArrayList<>();

        for (SignupRequest request : signupRequests) {
            try {
                if (userRepository.existsByEmail(request.getEmail())) {
                    failures.add(new RegistrationError(request.getEmail(), "Email already in use"));
                    continue;
                }

                User user = new User(
                        request.getEmail(),
                        encoder.encode(request.getPassword()),
                        ERole.valueOf(request.getRole().toUpperCase())
                );
                userRepository.save(user);
                successes.add(request.getEmail());
            } catch (Exception e) {
                failures.add(new RegistrationError(request.getEmail(), "System error: " + e.getMessage()));
            }
        }
        return new BulkRegistrationResponse(successes, failures);
    }

    @Override
    public JwtResponse authenticateUser(LoginRequest loginRequest) {
        // Authenticate using the provided email and password
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        // Generate JWT based on the authenticated principal
        String jwt = jwtUtils.generateJwtToken(authentication);

        org.springframework.security.core.userdetails.User userDetails = 
                (org.springframework.security.core.userdetails.User) authentication.getPrincipal();
        
        // Extract the role for the response
        String role = userDetails.getAuthorities().iterator().next().getAuthority();

        return new JwtResponse(jwt, userDetails.getUsername(), role);
    }

    @Override
    public MessageResponse registerUser(SignupRequest signUpRequest) {
    if (userRepository.existsByEmail(signUpRequest.getEmail())) {
        throw new RuntimeException("Error: Email is already in use!");
    }

    User user = new User(
            signUpRequest.getEmail(),
            encoder.encode(signUpRequest.getPassword()),
            ERole.valueOf(signUpRequest.getRole().toUpperCase())
    );

    userRepository.save(user);
    return new MessageResponse("User registered successfully!" , null);
}
}