package com.dat3.jpgroentbackend.controllers.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public class CreateRackRequest {
    @NotBlank
    @Min(0)
    public Integer xCoordinate;
    @NotBlank
    @Min(0)
    public Integer yCoordinate;
}
