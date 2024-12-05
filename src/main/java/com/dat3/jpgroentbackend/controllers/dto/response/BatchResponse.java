package com.dat3.jpgroentbackend.controllers.dto.response;

import com.dat3.jpgroentbackend.model.Batch;

import java.time.LocalDate;


//  A response model representing summarized details of a batch.
// Provides key batch information for APIs or UIs.
public class BatchResponse {

    private final String plantName; // Plant name associated with the batch with getter
    private final String trayName; // Tray type name used for the batch
    private final LocalDate creationDate; // Calculated creation date of the batch
    private final LocalDate harvestDate; // Expected harvest date of the batch
    private final String createdBy; // Name of the user who created the batch
    private final int amount; // Total amount in the batch
    private final int batchId; // Unique identifier for the batch


    /**
     * Constructs a BatchResponse using data from a Batch object.
     *
     * @param batch Source Batch object
     */
    public BatchResponse(Batch batch) {
        this.plantName = batch.getPlantType().getName();
        this.trayName = batch.getTrayType().getName();
        this.creationDate = batch.getPlantTask().getDueDate().minusDays(batch.getPlantType().getPreGerminationDays());
        this.harvestDate = batch.getHarvestingTask().getDueDate();
        this.createdBy = batch.getCreatedBy().getName();
        this.amount = batch.getBatchLocations().stream().map(batchLocation -> batchLocation.getBatchAmount()).reduce(0, Integer::sum);
        this.batchId = batch.getId();
    }

    // Getters
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
