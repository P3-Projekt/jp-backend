package com.dat3.jpgroentbackend.controllers.dto.request;

import com.dat3.jpgroentbackend.model.User;

public class CompleteTaskRequest {

    private final String username;

    public CompleteTaskRequest(String username) {
        this.username = username;
    }

    public String getUsername() {
        return this.username;
    }
}
