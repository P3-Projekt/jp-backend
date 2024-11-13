package com.dat3.jpgroentbackend.controllers.dto.response;

import com.dat3.jpgroentbackend.model.Batch;

import java.time.LocalDate;

public class BatchResponse {
    private final String plantName;
    private final String trayName;
    private final LocalDate creationDate;
    private final LocalDate harvestDate;
    private final String createdBy;
    private final int amount;
    private final int batchId;

    public BatchResponse(Batch batch) {
        this.plantName = batch.plantType.name;
        this.trayName = batch.trayType.name;
        this.creationDate = batch.batchLocations.get(0).plantTask.dueDate.minusDays(batch.plantType.preGerminationDays);
        this.harvestDate = batch.batchLocations.get(0).harvestingTask.dueDate;
        this.createdBy = batch.createdBy.name;
        this.amount = batch.batchLocations.stream().map(batchLocation -> batchLocation.amount).reduce(0, Integer::sum);
        this.batchId = batch.id;
    }

    public String getPlantName() {
        return plantName;
    }

    public String getTrayName() {
        return trayName;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public LocalDate getHarvestDate() {
        return harvestDate;
    }

    public int getAmount() {
        return amount;
    }

    public int getBatchId() {
        return batchId;
    }

    public String getCreatedBy() {
        return createdBy;
    }
}
