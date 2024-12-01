package com.dat3.jpgroentbackend.controllers.dto.request;

import com.dat3.jpgroentbackend.model.User;
import jakarta.validation.constraints.NotNull;

public class CreateUserRequest {
    @NotNull
    public String name;
    @NotNull
    public User.Role role;
    @NotNull
    public String password;
}
