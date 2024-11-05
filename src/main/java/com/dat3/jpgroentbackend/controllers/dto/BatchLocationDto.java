package com.dat3.jpgroentbackend.controllers.dto;

import com.dat3.jpgroentbackend.model.BatchLocation;

public class BatchLocationDto {

    public int shelfId;
    public int amount;
    public int batchId;

    public BatchLocationDto(BatchLocation batchLocation) {
        this.shelfId = batchLocation.id;
        this.amount = batchLocation.amount;
        this.batchId = batchLocation.batch.id;
    }
}
