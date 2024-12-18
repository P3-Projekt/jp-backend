package com.dat3.jpgroentbackend.model;

import com.dat3.jpgroentbackend.model.repositories.utils.ShelfScore;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

// Represents a batch of plants, including tasks related to planting, watering, and harvesting.
@Entity
public class Batch {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    private PlantType plantType;

    @ManyToOne
    private TrayType trayType;

    private LocalDateTime createdAt;

    @ManyToOne
    private User createdBy;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<BatchLocation> batchLocations = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<Task> wateringTasks = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private Task harvestingTask;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private Task plantTask;

    public Batch() {}
    public Batch(int amount, PlantType plantType, TrayType trayType, User createdBy) {
        this.plantType = plantType;
        this.trayType = trayType;
        this.createdBy = createdBy;
        this.createdAt = LocalDateTime.now();

        this.batchLocations.add(new BatchLocation(this, null, amount));

        // Plant from pre-germination task
        LocalDate plantDueDate = LocalDate.now().plusDays(plantType.getPreGerminationDays());
        this.plantTask = new Task(Task.Category.Plant, plantDueDate, this);

        // Water tasks
        for(int waterAfterDays : plantType.getWateringSchedule()){
            LocalDate waterDueDate = LocalDate.now().plusDays(waterAfterDays);
            this.wateringTasks.add(new Task(Task.Category.Water, waterDueDate, this));
        }

        // Harvest task
        LocalDate harvestDueDate = LocalDate.now().plusDays(plantType.getGrowthTimeDays());
        this.harvestingTask = new Task(Task.Category.Harvest, harvestDueDate, this);
    }


    // Returns the next task that needs to be completed based on priority (plant, water, harvest).
    public Task getNextTask(){
        //Return pre-germination task if it hasn't been completed
        if(this.plantTask.getCompletedAt() == null) return this.plantTask;

        //Construct a list of all tasks except planting
        List<Task> locationTasks = new ArrayList<>(this.wateringTasks);
        locationTasks.add(this.harvestingTask);

        //Filter away completed tasks
        List<Task> incompleteLocationTasks = locationTasks.stream().filter(task -> task.getCompletedAt() == null).toList();
        if(incompleteLocationTasks.isEmpty()){return null;}

        // Find the next task with the earliest due date, prioritize watering tasks

        Task nextTask = incompleteLocationTasks.getFirst();
        for(Task task : incompleteLocationTasks){
            boolean isNewFirst = task.getDueDate().isBefore(nextTask.getDueDate());
            boolean isNoneFirst = task.getDueDate().isEqual(nextTask.getDueDate());
            boolean isWatering = task.getCategory() == Task.Category.Water;
            //Prioritize tasks that are closer to be completed, then prioritize watering
            if(isNewFirst || (isNoneFirst && isWatering)){
                nextTask = task;
            }
        }
        return nextTask;

    }

    public LocalDate getLatestCompletedTaskDate(){
        LocalDateTime latestCompletesTaskTime = getTasks().stream()
                .map(Task::getCompletedAt)
                .filter(Objects::nonNull)
                .max(LocalDateTime::compareTo)
                .orElse(null);
        if(latestCompletesTaskTime == null){
            return null;
        } else {
            return latestCompletesTaskTime.toLocalDate();
        }
    }


    /**
     * Returns all tasks associated with this batch.
     */
    public List<Task> getTasks(){
        List<Task> tasks = new ArrayList<>();

        tasks.add(this.plantTask);
        tasks.addAll(this.wateringTasks);
        tasks.add(this.harvestingTask);

        return tasks;
    }

    /**
     * Returns the total amount of items in all batch locations.
     */
    public int getAmount(){

        return this.batchLocations.stream().map(BatchLocation::getBatchAmount).reduce(0, Integer::sum);
    }

    public void removeBatchLocations(){
        int amount = this.getAmount();
        this.batchLocations.clear();
        this.batchLocations.add(new BatchLocation(this, null, amount));
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public PlantType getPlantType() {
        return plantType;
    }



    public TrayType getTrayType() {
        return trayType;
    }


    public LocalDateTime getCreatedAt() {
        return createdAt;
    }



    public User getCreatedBy() {
        return createdBy;
    }



    public List<BatchLocation> getBatchLocations() {
        return batchLocations;
    }

    public void clearBatchLocations() {
        this.batchLocations.clear();
    }

    public void addAllBatchLocations(List<BatchLocation> batchLocations) {
        this.batchLocations.addAll(batchLocations);
    }



    public Task getHarvestingTask() {
        return harvestingTask;
    }



    public Task getPlantTask() {
        return plantTask;
    }



    // Helper methods
    public static List<Task> getTasks(List<Batch> batches, Integer weekDayFilter){
        List<Task> tasks = new ArrayList<>();
        for (Batch batch : batches) {
            tasks.addAll(batch.getTasks());
        }

        //Optional filter
        if(weekDayFilter != null){
            tasks = tasks.stream().filter(task -> task.isTaskDueInWeek(weekDayFilter)).toList();
        }

        return tasks;
    }


    public static List<Batch> filterPreGerminatingBatches(List<Batch> batches){
        return batches.stream().filter(batch -> batch.plantTask.getCompletedAt() == null).toList();
    }

    /**
     * Find optimal location(s) for the batch as a list of ShelfScore
     * @param racks A list of available racks, sorted with the 'best' racks in the start
     * @return A List<ShelfScore> sorted by score, descending.
     */
    public List<ShelfScore> autolocate(List<Rack> racks){
        List<ShelfScore> scoreList = new ArrayList<>();

        PlantType.PreferredPosition preferredPosition = getPlantType().getPreferredPosition();

        // Iterate through all racks in the list
        for (Rack rack : racks) {
            // If no shelves on this rack, continue to the next rack
            if (rack.getShelves() == null) {
                continue;
            }

            // Get a list of the maximum amount of this batch that can be on each shelf
            List<Integer> maxAmountOnShelves = rack.getMaxAmountOnShelves(this);

            // Get a list of shelves on the current rack
            List<Shelf> shelves = rack.getShelves();

            int highShelfIndex = shelves.size(); // Top position in the rack

            // Go through all shelves in a rack, from the lowest one, as the positions are incremented each time; if we wanted to start from the top reversed() would be necessary
            for (Shelf shelf : shelves) {

                // Get the max amount from this batch that can be placed on the current shelf, must be reversed as maxAmountOnShelves is also reversed
                int maxOnThisShelf = maxAmountOnShelves.reversed().get(shelf.getPosition() - 1);

                // If the shelf cant have any trays of the batch on it, continue
                if (maxOnThisShelf == 0) continue;

                // Sets the score to 10 or 0 depending on if the rack contains batches
                int score = !rack.isEmpty() ? 10 : 0;

                // Increases score if the plant type has a preferred position and the shelf matches the preferred position
                if ((shelf.getPosition() == 1 && preferredPosition == PlantType.PreferredPosition.Low) ||
                        (shelf.getPosition() == highShelfIndex && preferredPosition == PlantType.PreferredPosition.High)) {
                    score += 100;
                } else if (preferredPosition != PlantType.PreferredPosition.NoPreferred) { // Otherwise if the preferredPosition is not NoPreferred
                    continue;
                }

                // Get all the plant types on the current shelf
                Set<PlantType> plantTypesOnShelf = shelf.getPlantTypesOnShelf();

                // If the shelf does NOT already contain the plant type which the batch has, increase score
                if (!plantTypesOnShelf.contains(getPlantType())) {
                    score += 50;
                }

                // Get all the harvest dates of batches on the current shelf
                Set<LocalDate> harvestDatesOnShelf = shelf.getHarvestDatesOnShelf();

                // If there is any batch with the same harvest date on the current shelf, increase score
                if (harvestDatesOnShelf.contains(getHarvestingTask().getDueDate())) {
                    score += 25;
                }

                // Add the new shelfScore to the list
                scoreList.add(new ShelfScore(shelf.getId(), score, maxOnThisShelf));
            }
        }

        // Sort the list by score, descending
        scoreList.sort(Comparator.comparingInt(ShelfScore::getScore).reversed());

        return scoreList;
    }
}
