package com.dat3.jpgroentbackend.controllers.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class CreateRackRequest {
    @NotNull
    @Min(0)
    public Integer xCoordinate;
    @NotNull
    @Min(0)
    public Integer yCoordinate;
}
