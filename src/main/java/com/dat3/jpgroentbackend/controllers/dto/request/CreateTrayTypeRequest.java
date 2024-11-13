package com.dat3.jpgroentbackend.controllers.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class CreateTrayTypeRequest {
    @NotNull
    public String name;

    @NotNull
    @Min(0)
    public Integer widthCm;

    @NotNull
    @Min(0)
    public Integer lengthCm;
}