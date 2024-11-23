package com.dat3.jpgroentbackend.controllers.dto.response;

import com.dat3.jpgroentbackend.model.Batch;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PreGerminatingBatchesResponse {

    public static class BatchResponse {
        private final int batchId;
        private final int amount;
        private final String plantName;
        private final LocalDate dueDate;

        public BatchResponse(Batch batch) {
            this.batchId = batch.id;
            this.amount = batch.getAmount();
            this.plantName = batch.plantType.getName();
            this.dueDate = batch.getPlantTask().dueDate;
        }

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

    private final List<BatchResponse> needsMorePreGermination = new ArrayList<>();
    private final List<BatchResponse> canBePlaced = new ArrayList<>();

    public PreGerminatingBatchesResponse(List<Batch> preGerminatingBatches) {
        for(Batch batch : preGerminatingBatches) {
            LocalDate plantDueDate = batch.getPlantTask().dueDate;
            if(plantDueDate.isAfter(LocalDate.now())) {
                needsMorePreGermination.add(new BatchResponse(batch));
            } else {
                canBePlaced.add(new BatchResponse(batch));
            }
        }
    }

    public List<BatchResponse> getNeedsMorePreGermination() {
        return needsMorePreGermination;
    }

    public List<BatchResponse> getCanBePlaced() {
        return canBePlaced;
    }
}
