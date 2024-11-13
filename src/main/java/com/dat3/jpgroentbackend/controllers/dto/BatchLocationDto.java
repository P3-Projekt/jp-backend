package com.dat3.jpgroentbackend.controllers.dto;

import com.dat3.jpgroentbackend.model.BatchLocation;
import jakarta.validation.constraints.NotNull;

public class BatchLocationDto {

    @NotNull
    public int shelfId;
    @NotNull
    public int amount;
    @NotNull
    public int batchId;

    public BatchLocationDto(BatchLocation batchLocation) {
        this.shelfId = batchLocation.id;
        this.amount = batchLocation.amount;
        this.batchId = batchLocation.batch.id;
    }
}
