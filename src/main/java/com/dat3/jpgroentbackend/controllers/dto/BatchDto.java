package com.dat3.jpgroentbackend.controllers.dto;

import com.dat3.jpgroentbackend.model.Batch;

public class BatchDto {

    public int batchId;
    public String plantTypeId;
    public String trayTypeId;
    public String createdAt;
    public String createdBy;


    public BatchDto(Batch batch) {
        this.batchId = batch.id;
        this.plantTypeId = batch.plantType.name;
        this.trayTypeId = batch.trayType.name;
        this.createdAt = batch.createdAt.toString();
        this.createdBy = batch.createdBy.name;
    }
}
