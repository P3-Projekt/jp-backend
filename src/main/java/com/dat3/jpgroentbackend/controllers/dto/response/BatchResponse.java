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
        this.plantName = batch.plantType.getName();
        this.trayName = batch.trayType.getName();
        this.creationDate = batch.getPlantTask().dueDate.minusDays(batch.plantType.getPreGerminationDays());
        this.harvestDate = batch.getHarvestingTask().dueDate;
        this.createdBy = batch.createdBy.getName();
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
