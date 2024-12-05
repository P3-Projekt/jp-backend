package com.dat3.jpgroentbackend.controllers.dto.request;

import com.dat3.jpgroentbackend.model.User;
import jakarta.validation.constraints.Email;

public class UpdateUserRequest {
    private User.Role role;

    private String telephone;

    @Email(message = "Invalid email format")
    private String email;

    private String address;

    // Getters
    public User.Role getRole(){
        return  role;
    }

    public String getTelephone(){
        return telephone;
    }

    public String getEmail(){
        return email;
    }

    public String getAddress(){
        return address;
    }
}
