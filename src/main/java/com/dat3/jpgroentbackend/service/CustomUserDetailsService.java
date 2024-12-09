package com.dat3.jpgroentbackend.service;


import com.dat3.jpgroentbackend.model.User;
import com.dat3.jpgroentbackend.model.repositories.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

// Service for loading user details from the database for authentication.
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Constructor for dependency injection of UserRepository.
     * @param userRepository Repository to fetch user data.
     */
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Loads a user's details by their username.
     * @param username The username to search for.
     * @return UserDetails object containing user information.
     * @throws UsernameNotFoundException If the user is not found.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByName(username);
        if (user.isEmpty()) {
            throw new UsernameNotFoundException("User not found: " + username);
        }

        User userDetails = user.get();

        // Builds a Spring Security UserDetails object from the fetched user.
        return org.springframework.security.core.userdetails.User
                .withUsername(userDetails.getName())
                .password(userDetails.getPassword())
                .roles(userDetails.getRole().name())
                .build();
    }
}
