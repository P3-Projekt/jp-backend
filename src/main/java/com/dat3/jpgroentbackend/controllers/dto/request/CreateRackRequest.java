package com.dat3.jpgroentbackend.controllers.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

// This class is used to represent a CreateRackRequest
public class CreateRackRequest {
    @NotNull // The rack name cannot be null
    @Min(0) // The value must be at least 0
    public Integer xCoordinate; // xCoordinate of the rack

    @NotNull
    @Min(0)
    public Integer yCoordinate; // yCoordinate of the rack
}