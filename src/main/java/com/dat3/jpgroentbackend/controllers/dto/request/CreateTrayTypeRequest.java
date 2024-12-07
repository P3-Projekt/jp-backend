package com.dat3.jpgroentbackend.controllers.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

// This class represents a request to create a new tray type.
public class CreateTrayTypeRequest {
    @NotNull
    private String trayTypeName;

    @NotNull
    @Min(0)
    private Integer widthCm;

    @NotNull
    @Min(0)
    private Integer lengthCm;

    // Getters
    public String getTrayTypeName() {
        return trayTypeName;
    }

    public Integer getWidthCm() {
        return widthCm;
    }

    public Integer getLengthCm() {
        return lengthCm;
    }
}