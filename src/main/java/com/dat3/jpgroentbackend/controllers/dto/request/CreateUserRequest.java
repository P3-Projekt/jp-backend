package com.dat3.jpgroentbackend.controllers.dto.request;

import com.dat3.jpgroentbackend.model.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreateUserRequest {
    @NotBlank
    private String username;

    @NotNull
    private User.Role role;

    @NotNull
    private String password;

    // Getters
    public String getUsername() {
        return username;
    }

    public User.Role getRole() {
        return role;
    }

    public String getPassword() {
        return password;
    }
}
