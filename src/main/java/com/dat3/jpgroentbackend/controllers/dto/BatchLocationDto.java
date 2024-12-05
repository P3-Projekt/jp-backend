package com.dat3.jpgroentbackend.controllers.dto;

import com.dat3.jpgroentbackend.model.BatchLocation;
import jakarta.validation.constraints.NotNull;

// A Data Transfer Object (DTO) for representing batch location details in API requests or responses.
public class BatchLocationDto {
    @NotNull // Ensures the field cannot be null during validation
    private int shelfId;
    @NotNull
    private int amount;
    @NotNull
    private int batchId;

    /**
     * Constructs a BatchLocationDto using a BatchLocation object.
     * @param batchLocation The BatchLocation object containing the data to populate this DTO.
     */
    public BatchLocationDto(BatchLocation batchLocation) {
        this.shelfId = batchLocation.getId();
        this.amount = batchLocation.getBatchAmount();
        this.batchId = batchLocation.getBatch().getId();
    }
}
