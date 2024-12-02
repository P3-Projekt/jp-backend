package com.dat3.jpgroentbackend.controllers.dto.request;

import com.dat3.jpgroentbackend.model.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreateUserRequest {
    @NotBlank
    public String name;
    @NotNull
    public User.Role role;

    public String telephone;

    @Email(message = "Invalid email format")
    public String email;

    public String address;
}
