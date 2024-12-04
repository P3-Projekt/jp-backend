package com.dat3.jpgroentbackend.config;

import com.dat3.jpgroentbackend.model.User;
import com.dat3.jpgroentbackend.model.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class DemoDataConfig {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public DemoDataConfig(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
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

                userRepository.save(demoUser);
                System.out.println("Demo user created!");
            } else {
                System.out.println("Demo user already exists!");
            }
        };
    }
}
