package com.dat3.jpgroentbackend.controllers.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

// This class represents a request to create a new tray type.
public class CreateTrayTypeRequest {
    @NotNull
    @NotBlank
    public String name;

    @NotNull
    @Min(1)
    public Integer widthCm;

    @NotNull
    @Min(1)
    public Integer lengthCm;
}