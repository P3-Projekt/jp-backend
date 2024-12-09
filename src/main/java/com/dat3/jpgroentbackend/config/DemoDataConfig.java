package com.dat3.jpgroentbackend.config;

import com.dat3.jpgroentbackend.model.User;
import com.dat3.jpgroentbackend.model.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

// This class is used to create a demo user when the application starts
@Configuration // Used for Bean configuration for spring
public class DemoDataConfig {

    // User config
    private static final String initUserName = "Jens";
    private static final String initUserPassword = "Jens9876";


    // Autowire the user repository and password encoder
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    /**
     * Constructor for the DemoDataConfig class
     * @param userRepository The user repository
     * @param passwordEncoder The password encoder
     */
    @Autowired
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
            if (userRepository.findByName(initUserName).isEmpty()) {
                // Create and save the demo user
                User initialUser = new User();
                initialUser.setName(initUserName);
                initialUser.setPassword(passwordEncoder.encode(initUserPassword)); // Use a simple password for testing
                initialUser.setRole(User.Role.Administrator); // Set the role as needed
                initialUser.setActive(true); // Set the user as active

                // Save the user
                userRepository.save(initialUser);
                System.out.println("Initial user for Jens created!");
            } else {
                System.out.println("Initial user for Jens already exists!");
            }
        };
    }

    public static String getInitUserName() {
        return DemoDataConfig.initUserName;
    }
}
