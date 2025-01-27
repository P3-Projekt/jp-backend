package com.dat3.jpgroentbackend.controllers.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

// This class is used to represent a CompleteTaskRequest
public class CompleteTaskRequest {

    // The name of the user completing the task
    @NotNull
    @NotBlank
    public String username;
}