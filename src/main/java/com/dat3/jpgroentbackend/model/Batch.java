package com.dat3.jpgroentbackend.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    /**
     * Returns the date of the latest completed task.
     */
    /**
     * Calculates the area
     * @return
     */
    public int getBatchArea() {
        return getTrayType().getLengthCm() * getTrayType().getWidthCm() * getAmount();
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
}
