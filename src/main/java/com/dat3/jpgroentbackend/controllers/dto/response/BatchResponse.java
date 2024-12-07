package com.dat3.jpgroentbackend.controllers.dto.response;

import com.dat3.jpgroentbackend.model.Batch;
import com.dat3.jpgroentbackend.model.BatchLocation;

import java.time.LocalDate;


//  A response model representing summarized details of a batch.
// Provides key batch information for APIs or UIs.
public class BatchResponse {

    public final String plantName; // Plant name associated with the batch with getter
    public final String trayName; // Tray type name used for the batch
    public final LocalDate creationDate; // Calculated creation date of the batch
    public final LocalDate harvestDate; // Expected harvest date of the batch
    public final String createdBy; // Name of the user who created the batch
    public final int amount; // Total amount in the batch
    public final int batchId; // Unique identifier for the batch


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
        this.amount = batch.getBatchLocations().stream().map(BatchLocation::getBatchAmount).reduce(0, Integer::sum);
        this.batchId = batch.getId();
    }
}
