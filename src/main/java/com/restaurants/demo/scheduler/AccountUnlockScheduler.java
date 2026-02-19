package com.restaurants.demo.service.schedular;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.restaurants.demo.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AccountUnlockScheduler {

    private static final Logger logger = LoggerFactory.getLogger(AccountUnlockScheduler.class);
    private final UserRepository userRepository;

    // Runs every 1 minute (60,000 ms)
    @Scheduled(fixedRate = 60000)
    public void unlockAccounts() {
        LocalDateTime now = LocalDateTime.now();

        userRepository.unlockExpiredAccounts(now);

        logger.debug("Executed background task to unlock expired accounts at {}", now);
    }
}