package com.dat3.jpgroentbackend.controllers;

import com.dat3.jpgroentbackend.controllers.dto.request.AuthRequest;
import com.dat3.jpgroentbackend.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// @RestController marks this class as a controller
// where every method returns JSON or similar data directly.
// Exposes endpoints under the "/api/auth" URL for login functionality.
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired // @Autowired injects the required dependency automatically, reducing boilerplate code.
    private AuthenticationService authenticationService;

    /**
     * Authenticates a user using the provided username and password.
     * @param authRequest The authentication request containing the username and password.
     * @return A JWT token if the authentication is successful.
     * @throws Exception If the authentication fails.
     */
    @PostMapping("/login")
    public String login(@RequestBody AuthRequest authRequest) throws Exception {
        System.out.println("test");
        return authenticationService.authenticate(authRequest.getUsername(), authRequest.getPassword());
    }
}