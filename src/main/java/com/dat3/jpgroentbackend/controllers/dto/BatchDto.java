package com.dat3.jpgroentbackend.controllers.dto;

import com.dat3.jpgroentbackend.model.Batch;

// A Data Transfer Object (DTO) for representing batch details in API responses.
public class BatchDto {

    public final int batchId;
    public final String plantTypeId;
    public final String trayTypeId;
    public final String createdAt;
    public final String createdBy;


    /**
     * Constructs a BatchDto using a Batch object.
     *
     * @param batch The Batch object containing the data to populate this DTO.
     */
    public BatchDto(Batch batch) {
        this.batchId = batch.getId();
        this.plantTypeId = batch.getPlantType().getName();
        this.trayTypeId = batch.getTrayType().getName();
        this.createdAt = batch.getCreatedAt().toString(); // Converts creation date to a string
        this.createdBy = batch.getCreatedBy().getName();
    }
}
