package com.dat3.jpgroentbackend.controllers;

import com.dat3.jpgroentbackend.config.DemoDataConfig;
import com.dat3.jpgroentbackend.controllers.dto.request.AuthRequest;
import com.dat3.jpgroentbackend.model.User;
import com.dat3.jpgroentbackend.model.repositories.UserRepository;
import com.dat3.jpgroentbackend.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

// @RestController marks this class as a controller
// where every method returns JSON or similar data directly.
// Exposes endpoints under the "/api/auth" URL for login functionality.
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired // @Autowired injects the required dependency automatically, reducing boilerplate code.
    private AuthenticationService authenticationService;
    @Autowired
    private UserRepository userRepository;

    /**
     * Authenticates a user using the provided username and password.
     * @param authRequest The authentication request containing the username and password.
     * @return A JWT token if the authentication is successful.
     * @throws Exception If the authentication fails.
     */
    @PostMapping("/login")
    public String login(@RequestBody AuthRequest authRequest) throws Exception {
        ResponseStatusException loginError = new ResponseStatusException(HttpStatus.NOT_FOUND, "Invalid username or password");
        User user = userRepository.findById(authRequest.username).orElseThrow(() -> loginError);

        if(user.isActive() || user.getName().equals(DemoDataConfig.getInitUserName())){
            return authenticationService.authenticate(authRequest.username, authRequest.password);
        } else {
            throw loginError;
        }
    }
}