package com.dat3.jpgroentbackend.controllers.dto.request;

import com.dat3.jpgroentbackend.model.User;
import jakarta.validation.constraints.Email;

public class UpdateUserRequest {
    public User.Role role;
}
