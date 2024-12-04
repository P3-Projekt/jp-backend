package com.dat3.jpgroentbackend.controllers;

import com.dat3.jpgroentbackend.controllers.dto.request.AuthRequest;
import com.dat3.jpgroentbackend.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping("/login")
    public String login(@RequestBody AuthRequest authRequest) throws Exception {
        System.out.println("Test login");
        return authenticationService.authenticate(authRequest.getUsername(), authRequest.getPassword());
    }
}
