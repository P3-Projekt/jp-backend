package com.dat3.jpgroentbackend.controllers.dto.response;

import com.dat3.jpgroentbackend.model.Batch;
import com.dat3.jpgroentbackend.model.Rack;
import com.dat3.jpgroentbackend.model.Shelf;
import com.dat3.jpgroentbackend.model.Task;
import com.dat3.jpgroentbackend.model.BatchLocation;
import com.dat3.jpgroentbackend.model.repositories.utils.Vector2;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


 // A response DTO representing a rack, its position, and the details of its shelves and batches.
public class RacksResponse {

    // Represents a 2D position with x and y coordinates.
    public static class Position{
        public final int x;
        public final int y;

        public Position(Vector2 position) {
            this.x = position.getX();
            this.y = position.getY();
        }
    }

    // Represents a shelf, its ID, and the details of its batches.
    public static class ShelfResponse{
        // Represents a batch stored on a shelf.
        public static class BatchResponse{
            // Represents the next task related to a batch.
            public static class NextTaskResponse{
                // Task properties
                public final int id;
                public final Task.Category category;
                public final LocalDate dueDate;
                public final double progress;

                /**
                 * Constructs a NextTaskResponse from a Task object.
                 * @param task The Task object to extract data from
                 * @param progress The progress of the task
                 */
                public NextTaskResponse(Task task, double progress){
                    this.id = task.getId();
                    this.category = task.getCategory();
                    this.dueDate = task.getDueDate();
                    this.progress = progress;
                }
            }

            // Batch properties
            public final int id;
            public final int amount;
            public final String tray;
            public final String plant;
            public final String createdBy;
            public final LocalDate harvestDate;
            public final NextTaskResponse nextTaskResponse;

            /**
             * Constructs a BatchResponse from a Batch object.
             * @param batch The Batch object to extract data from
             */
            public BatchResponse(Batch batch) {
                // Extract data from the batch
                this.id = batch.getId();
                this.amount = batch.getAmount();
                this.tray = batch.getTrayType().getName();
                this.plant = batch.getPlantType().getName();
                this.createdBy = batch.getCreatedBy().getName();
                this.harvestDate = batch.getHarvestingTask().getDueDate();

                // Calculate the progress of the next task
                Task nextTask = batch.getNextTask(); // Get the next task
                LocalDate latestTaskCompletion = batch.getLatestCompletedTaskDate(); // Get the date of the latest task completion
                long lastCompletionEpoch = latestTaskCompletion.toEpochDay(); // Convert the date to an epoch day, for easier comparison
                long nowEpoch = LocalDate.now().toEpochDay(); // Get the current date as an epoch day
                long nextTaskEpoch = nextTask.getDueDate().toEpochDay(); // Get the due date of the next task as an epoch day

                // Calculate the progression of the next task
                double nextTaskProgression = ((double) nowEpoch - lastCompletionEpoch + 1) / (nextTaskEpoch - lastCompletionEpoch + 1);

                // Create the NextTaskResponse object
                this.nextTaskResponse = new NextTaskResponse(nextTask, nextTaskProgression);
            }
        }

        // Shelf properties
        public final int id;
        public final List<BatchResponse> batches = new ArrayList<>(); // List of batches stored on the shelf
        // Constructor for ShelfResponse
        public ShelfResponse(Shelf shelf) {
            this.id = shelf.getId();
            // Add each batch on the shelf to the list of batches
            shelf.getBatchLocations().stream()
                    .map(BatchLocation::getBatch)
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
    public final int id;
    public final Position position;
    public final ArrayList<ShelfResponse> shelves = new ArrayList<>();

    /**
     * Constructs a RacksResponse from a Rack object.
     * @param rack The Rack object to extract data from
     */
    public RacksResponse(Rack rack){
        this.id = rack.getId();
        this.position = new Position(rack.getPosition());

        rack.getShelves().reversed().forEach(shelf -> this.shelves.add(new ShelfResponse(shelf)));

    }
}
