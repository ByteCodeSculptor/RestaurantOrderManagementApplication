package com.restaurants.demo.config;

import com.restaurants.demo.entity.ERole;
import com.restaurants.demo.entity.User;
import com.restaurants.demo.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * DataInitializer class to bootstrap the application with initial data.
 * Implements CommandLineRunner to execute code after the Spring Boot application starts.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Constructor injection for required beans.
     * @param userRepository the repository for managing User entities
     * @param passwordEncoder the bean used to securely hash passwords
     */
    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // Step 1: Check if the users table is empty
        // We only bootstrap the first admin if no users currently exist in the database.
        if (userRepository.count() == 0) {
            
            // Step 2: Create the default Admin user
            // We use the email field as the unique identifier as per your SQL schema
            String adminEmail = "admin@gmail.com";
            String rawPassword = "admin123";
            
            // Securely hash the password before persistence
            User admin = new User(
                adminEmail,
                passwordEncoder.encode(rawPassword),
                ERole.ADMIN // Assigning the ADMIN role from the enum
            );

            // Step 3: Persist the admin user to the MySQL database
            userRepository.save(admin);

            // Logging the creation for the developer
            System.out.println("--------------------------------------------------");
            System.out.println("BOOTSTRAP: Initial Admin Account Created.");
            System.out.println("Email: " + adminEmail);
            System.out.println("Default Password: " + rawPassword);
            System.out.println("PLEASE CHANGE THE PASSWORD AFTER FIRST LOGIN.");
            System.out.println("--------------------------------------------------");
        } else {
            System.out.println("BOOTSTRAP: Users already exist. Skipping data initialization.");
        }
    }
}