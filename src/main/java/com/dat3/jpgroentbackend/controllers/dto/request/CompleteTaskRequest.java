package com.dat3.jpgroentbackend.controllers.dto.request;

import com.dat3.jpgroentbackend.model.User;

// This class is used to represent a CompleteTaskRequest
public class CompleteTaskRequest {

    // The name of the user completing the task
    private String username;
    
    // Getter for username
    public String getUsername() {
        return username;
    }

    public CompleteTaskRequest() {}
}