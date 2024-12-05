package com.dat3.jpgroentbackend.controllers.dto;

import com.dat3.jpgroentbackend.model.Batch;

// A Data Transfer Object (DTO) for representing batch details in API responses.
public class BatchDto {

    public int batchId;
    public String plantTypeId;
    public String trayTypeId;
    public String createdAt;
    public String createdBy;


    /**
     * Constructs a BatchDto using a Batch object.
     *
     * @param batch The Batch object containing the data to populate this DTO.
     */
    public BatchDto(Batch batch) {
        this.batchId = batch.id;
        this.plantTypeId = batch.plantType.getName();
        this.trayTypeId = batch.trayType.getName();
        this.createdAt = batch.createdAt.toString(); // Converts creation date to a string
        this.createdBy = batch.createdBy.getName();
    }
}
