package com.dat3.jpgroentbackend.controllers.dto.response;

import com.dat3.jpgroentbackend.model.Batch;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * A response DTO for batches in the pre-germination process.
 * This class organizes batches into two categories:
 * - Batches that need more pre-germination time.
 * - Batches that are ready to be placed elsewhere.
 */
public class PreGerminatingBatchesResponse {

    // Inner class representing a simplified view of a batch.
    public static class BatchResponse {
        private final int batchId; // Unique identifier for the batch
        private final int amount; // Total amount in the batch
        private final String plantName; // Name of the plant type in the batch
        private final LocalDate dueDate; // Due date for the batch's next task

        /**
         * Constructs a BatchResponse from a Batch object.
         *
         * @param batch The Batch object to extract data from
         */
        public BatchResponse(Batch batch) {
            this.batchId = batch.getId();
            this.amount = batch.getAmount();
            this.plantName = batch.getPlantType().getName();
            this.dueDate = batch.getPlantTask().getDueDate();
        }

        // Getters
        public int getBatchId() {
            return batchId;
        }

        public int getAmount() {
            return amount;
        }

        public LocalDate getDueDate() {
            return dueDate;
        }

        public String getPlantName() {
            return plantName;
        }
    }

    // List of batches that still need more pre-germination time
    private final List<BatchResponse> needsMorePreGermination = new ArrayList<>();

    // List of batches that are ready to be placed elsewhere
    private final List<BatchResponse> canBePlaced = new ArrayList<>();

    /**
     * Constructs a PreGerminatingBatchesResponse by categorizing batches
     * based on their due date.
     *
     * @param preGerminatingBatches List of batches in the pre-germination stage
     */
    public PreGerminatingBatchesResponse(List<Batch> preGerminatingBatches) {
        for(Batch batch : preGerminatingBatches) {
            LocalDate plantDueDate = batch.getPlantTask().getDueDate();
            if(plantDueDate.isAfter(LocalDate.now())) {
                needsMorePreGermination.add(new BatchResponse(batch));
            } else {
                canBePlaced.add(new BatchResponse(batch));
            }
        }
    }

    // getters
    public List<BatchResponse> getNeedsMorePreGermination() {
        return needsMorePreGermination;
    }

    public List<BatchResponse> getCanBePlaced() {
        return canBePlaced;
    }
}
