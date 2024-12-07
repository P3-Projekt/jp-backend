package com.dat3.jpgroentbackend.service;

import com.dat3.jpgroentbackend.model.repositories.utils.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

// Service class for handling authentication requests
@Service
public class AuthenticationService {
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService customUserDetailsService;
    private final JwtUtil jwtUtil;

    // Constructor Injection
    public AuthenticationService(AuthenticationManager authenticationManager,
                                 CustomUserDetailsService customUserDetailsService,
                                 JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.customUserDetailsService = customUserDetailsService;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Authenticates a user with the given username and password.
     * @param username The username of the user to authenticate.
     * @param password The password of the user to authenticate.
     * @return A JWT token if the authentication is successful.
     * @throws Exception If the authentication fails.
     */
    public String authenticate(String username, String password) throws Exception {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );
        } catch (Exception e) {
            throw new Exception("Incorrect username or password", e);
        }

        // Load the user details and generate a JWT token
        final UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
        return jwtUtil.generateToken(userDetails.getUsername());
    }

}
