package com.restaurants.demo.security.listener;

import java.time.LocalDateTime;

import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

import com.restaurants.demo.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AuthenticationEventsListener {
    private final UserRepository userRepository;
    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final int LOCK_TIME_DURATION = 1; // Hours

    @EventListener //to enable loose coupling between components in an event-driven architecture
    public void onFailure(AuthenticationFailureBadCredentialsEvent event) {
        String email = event.getAuthentication().getName();
        userRepository.findByEmail(email).ifPresent(user -> {
            if (user.isAccountNonLocked()) {
                user.setFailedAttempts(user.getFailedAttempts() + 1);
                if (user.getFailedAttempts() >= MAX_FAILED_ATTEMPTS) {
                    user.setAccountNonLocked(false);
                    user.setLockoutTime(LocalDateTime.now().plusHours(LOCK_TIME_DURATION));
                }
                userRepository.save(user);
            }
        });
    }

    @EventListener
    public void onSuccess(AuthenticationSuccessEvent event) {
        String email = event.getAuthentication().getName();
        userRepository.findByEmail(email).ifPresent(user -> {
            if (user.getFailedAttempts() > 0) {
                user.setFailedAttempts(0);
                user.setAccountNonLocked(true);
                user.setLockoutTime(null);
                userRepository.save(user);
            }
        });
    }
}
