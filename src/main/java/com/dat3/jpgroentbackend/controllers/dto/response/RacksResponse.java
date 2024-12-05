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


 // A response DTO representing a rack, its position, and the details of its shelves and batches.
public class RacksResponse {

    // Represents a 2D position with x and y coordinates.
    public static class Position{
        private final int x;
        private final int y;

        public Position(Vector2 position) {
            this.x = position.getX();
            this.y = position.getY();
        }

        // Getters
        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }
    }

    // Represents a shelf, its ID, and the details of its batches.
    public static class ShelfResponse{
        // Represents a batch stored on a shelf.
        public static class BatchResponse{
            // Represents the next task related to a batch.
            public static class NextTaskResponse{
                // Task properties
                private final int id;
                private final Task.Category category;
                private final LocalDate dueDate;
                private final double progress;

                /**
                 * Constructs a NextTaskResponse from a Task object.
                 * @param task The Task object to extract data from
                 * @param progress The progress of the task
                 */
                public NextTaskResponse(Task task, double progress){
                    this.id = task.id;
                    this.category = task.category;
                    this.dueDate = task.dueDate;
                    this.progress = progress;
                }

                // Getters
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

            // Batch properties
            private final int id;
            private final int amount;
            private final String tray;
            private final String plant;
            private final String createdBy;
            private final LocalDate harvestDate;
            private final NextTaskResponse nextTaskResponse;

            /**
             * Constructs a BatchResponse from a Batch object.
             * @param batch The Batch object to extract data from
             */
            public BatchResponse(Batch batch) {
                // Extract data from the batch
                this.id = batch.id;
                this.amount = batch.getAmount();
                this.tray = batch.trayType.getName();
                this.plant = batch.plantType.getName();
                this.createdBy = batch.createdBy.getName();
                this.harvestDate = batch.getHarvestingTask().dueDate;

                // Calculate the progress of the next task
                Task nextTask = batch.getNextTask(); // Get the next task
                LocalDate latestTaskCompletion = batch.getLatestCompletedTaskDate(); // Get the date of the latest task completion
                long lastCompletionEpoch = latestTaskCompletion.toEpochDay(); // Convert the date to an epoch day, for easier comparison
                long nowEpoch = LocalDate.now().toEpochDay(); // Get the current date as an epoch day
                long nextTaskEpoch = nextTask.dueDate.toEpochDay(); // Get the due date of the next task as an epoch day

                // Calculate the progression of the next task
                double nextTaskProgression = ((double) nowEpoch - lastCompletionEpoch + 1) / (nextTaskEpoch - lastCompletionEpoch + 1);

                // Create the NextTaskResponse object
                this.nextTaskResponse = new NextTaskResponse(nextTask, nextTaskProgression);
            }

            // Getters
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

        // Shelf properties
        private final int id;
        private final List<BatchResponse> batches = new ArrayList<>(); // List of batches stored on the shelf
        // Constructor for ShelfResponse
        public ShelfResponse(Shelf shelf) {
            this.id = shelf.getId();
            // Add each batch on the shelf to the list of batches
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

    // Rack properties
    private final int id;
    private final Position position;
    private final ArrayList<ShelfResponse> shelves = new ArrayList<>();

    /**
     * Constructs a RacksResponse from a Rack object.
     * @param rack The Rack object to extract data from
     */
    public RacksResponse(Rack rack){
        this.id = rack.getId();
        this.position = new Position(rack.getPosition());
        // Add each shelf on the rack to the list of shelves
        rack.getShelves().reversed().forEach(shelf -> this.shelves.add(new ShelfResponse(shelf)));
    }

    // Getters
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
