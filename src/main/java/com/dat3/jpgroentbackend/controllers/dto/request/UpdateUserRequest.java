package com.dat3.jpgroentbackend.controllers.dto.request;

import com.dat3.jpgroentbackend.model.User;
import jakarta.validation.constraints.Email;

public class UpdateUserRequest {
    private User.Role role;

    // Getters
    public User.Role getRole(){
        return  role;
    }

}
