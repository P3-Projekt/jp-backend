package com.dat3.jpgroentbackend.controllers.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

// This class is used to represent a CreateBatchRequest
public class CreateBatchRequest {
    @NotNull // NotNull is used to validate that the value is not null
    private String plantTypeId; // The id (name) of the plant type

    @NotNull
    private String trayTypeId; // The name of the tray type

    @NotNull
    private String createdByUsername; // The name of the user creating the batch

    @NotNull
    @Min(1) // Min is used to validate that the value is greater or equal to 1
    private Integer amount; // The amount of plants in the batch

    // Getters
    public String getPlantTypeId() {
        return plantTypeId;
    }

    public String getTrayTypeId() {
        return trayTypeId;
    }

    public String getCreatedByUsername() {
        return createdByUsername;
    }

    public Integer getAmount() {
        return amount;
    }
}