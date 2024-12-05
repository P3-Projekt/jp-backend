package com.dat3.jpgroentbackend.controllers.dto.response;

import com.dat3.jpgroentbackend.model.Batch;
import com.dat3.jpgroentbackend.model.Rack;
import com.dat3.jpgroentbackend.model.Shelf;
import com.dat3.jpgroentbackend.model.Task;
import com.dat3.jpgroentbackend.model.repositories.utils.Vector2;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RacksResponse {

    public static class Position{
        private final int x;
        private final int y;

        public Position(Vector2 position) {
            this.x = position.getX();
            this.y = position.getY();
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }
    }

    public static class ShelfResponse{
        public static class BatchResponse{
            public static class NextTaskResponse{
                private final int id;
                private final Task.Category category;
                private final LocalDate dueDate;
                private final double progress;

                public NextTaskResponse(Task task, double progress){
                    this.id = task.id;
                    this.category = task.category;
                    this.dueDate = task.dueDate;
                    this.progress = progress;
                }

                public int getId() {
                    return id;
                }

                public Task.Category getCategory() {
                    return category;
                }

                public LocalDate getDueDate() {
                    return dueDate;
                }

                public double getProgress() {
                    return progress;
                }
            }


            private final int id;
            private final int amount;
            private final String tray;
            private final String plant;
            private final String createdBy;
            private final LocalDate harvestDate;
            private final NextTaskResponse nextTaskResponse;

            public BatchResponse(Batch batch) {
                this.id = batch.id;
                this.amount = batch.getAmount();
                this.tray = batch.trayType.getName();
                this.plant = batch.plantType.getName();
                this.createdBy = batch.createdBy.getName();
                this.harvestDate = batch.getHarvestingTask().dueDate;

                Task nextTask = batch.getNextTask();
                LocalDate latestTaskCompletion = batch.getLatestCompletedTaskDate();
                long lastCompletionEpoch = latestTaskCompletion.toEpochDay();
                long nowEpoch = LocalDate.now().toEpochDay();
                long nextTaskEpoch = nextTask.dueDate.toEpochDay();

                double nextTaskProgression = ((double) nowEpoch - lastCompletionEpoch + 1) / (nextTaskEpoch - lastCompletionEpoch + 1);

                this.nextTaskResponse = new NextTaskResponse(nextTask, nextTaskProgression);

            }

            public int getId() {
                return id;
            }

            public int getAmount() {
                return amount;
            }

            public String getTray() {
                return tray;
            }

            public String getPlant() {
                return plant;
            }

            public String getCreatedBy() {
                return createdBy;
            }

            public LocalDate getHarvestDate() {
                return harvestDate;
            }

            public NextTaskResponse getNextTask() {
                return nextTaskResponse;
            }
        }
        private final int id;
        private final List<BatchResponse> batches = new ArrayList<>();
        public ShelfResponse(Shelf shelf) {
            this.id = shelf.getId();
            shelf.getBatchLocations().stream()
                    .map(batchLocation -> batchLocation.batch)
                    .forEach(batch -> this.batches.add(new BatchResponse(batch)));
        }

        public int getId() {
            return id;
        }

        public List<BatchResponse> getBatches() {
            return batches;
        }

    }

    private final int id;
    private final Position position;
    private final ArrayList<ShelfResponse> shelves = new ArrayList<>();

    public RacksResponse(Rack rack){
        this.id = rack.getId();
        this.position = new Position(rack.getPosition());
        if (rack.getShelves() != null) {
            rack.getShelves().reversed().forEach(shelf -> this.shelves.add(new ShelfResponse(shelf)));
        }
    }

    public int getId() {
        return id;
    }

    public Position getPosition() {
        return position;
    }

    public List<ShelfResponse> getShelves() {
        return shelves;
    }
}
