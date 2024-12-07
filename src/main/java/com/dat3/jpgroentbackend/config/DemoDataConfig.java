package com.dat3.jpgroentbackend.config;

import com.dat3.jpgroentbackend.model.User;
import com.dat3.jpgroentbackend.model.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

// This class is used to create a demo user when the application starts
@Configuration // Used for Bean configuration for spring
public class DemoDataConfig {

    // Autowire the user repository and password encoder
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    /**
     * Constructor for the DemoDataConfig class
     * @param userRepository The user repository
     * @param passwordEncoder The password encoder
     */
    public DemoDataConfig(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Method to create a demo user
     * @return A CommandLineRunner object
     */
    @Bean // Bean is used for Spring to manage the object
    public CommandLineRunner demoData() {
        return args -> {
            // Check if user already exists
            if (!userRepository.findByName("demo").isPresent()) {
                // Create and save the demo user
                User demoUser = new User();
                demoUser.setName("demo");
                demoUser.setPassword(passwordEncoder.encode("12345")); // Use a simple password for testing
                demoUser.setRole(User.Role.Administrator); // Set the role as needed
                demoUser.setActive(true); // Set the user as active

                // Save the user
                userRepository.save(demoUser);
                System.out.println("Demo user created!");
            } else {
                System.out.println("Demo user already exists!");
            }
        };
    }
}
