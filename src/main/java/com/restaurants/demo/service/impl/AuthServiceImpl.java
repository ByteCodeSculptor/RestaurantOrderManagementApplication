package com.restaurants.demo.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
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
import com.restaurants.demo.exception.AccountLockedException;
import com.restaurants.demo.exception.InvalidCredentialsException;
import com.restaurants.demo.repository.UserRepository;
import com.restaurants.demo.security.jwt.JwtUtils;
import com.restaurants.demo.service.AuthService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final JwtUtils jwtUtils;

    @Override
    @Transactional
    public BulkRegistrationResponse registerUsers(List<SignupRequest> signupRequests) {
        List<String> successes = new ArrayList<>();
        List<RegistrationError> failures = new ArrayList<>();

        for (SignupRequest request : signupRequests) {
            if (userRepository.existsByEmail(request.getEmail())) { //(Fail-Safe)
                failures.add(new RegistrationError(request.getEmail(), "Email already in use"));
                continue;
             }
            try {
                User user = new User(
                        request.getEmail(),
                        encoder.encode(request.getPassword()),
                        ERole.valueOf(request.getRole().toUpperCase()),
                        0, true, null
                );
                userRepository.save(user);
                successes.add(request.getEmail());
            } catch (IllegalArgumentException e) {
                failures.add(new RegistrationError(request.getEmail(), "Invalid role: " + request.getRole()));
             }
        // They will break the loop and fail the entire request (Fail-Fast).
    }
        return new BulkRegistrationResponse(successes, failures);
    }

 

    @Override
    public JwtResponse authenticateUser(LoginRequest loginRequest) {
        try{
                // 1. Validate credentials via the AuthenticationManager
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

            // 2. Generate the JWT token
            String jwt = jwtUtils.generateJwtToken(authentication);

            // 3. Extract user details for the response
            org.springframework.security.core.userdetails.User userDetails = 
                        (org.springframework.security.core.userdetails.User) authentication.getPrincipal();
            
            // Convert the collection of authorities into a list of strings
            List<String> roles = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());
            return new JwtResponse(jwt, userDetails.getUsername(), roles);
        }
            catch (LockedException e) {
            throw new AccountLockedException("Your account has been locked due to 5 failed attempts. Please try again later.");
        } 
            catch (BadCredentialsException e) {
            throw new InvalidCredentialsException("Invalid email or password.");
        }
    }



    @Override
    public MessageResponse registerUser(SignupRequest signUpRequest) {
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new RuntimeException("Error: Email is already in use!");
        }

        User user = new User(
                signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()),
                ERole.valueOf(signUpRequest.getRole().toUpperCase()),
                0, true, null
        );

        userRepository.save(user);
        return new MessageResponse("User registered successfully!" , null);
    }
}
