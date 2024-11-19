package com.dat3.jpgroentbackend.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Batch {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    public int id;

    @ManyToOne
    public PlantType plantType;

    @ManyToOne
    public TrayType trayType;

    public LocalDateTime createdAt;

    @ManyToOne
    public User createdBy;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    public List<BatchLocation> batchLocations = new ArrayList<>();

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

        //Plant from pre-germination task
        LocalDate plantDueDate = LocalDate.now().plusDays(plantType.getPreGerminationDays());
        this.plantTask = new Task(Task.Category.Plant, plantDueDate, this);

        //Water tasks
        for(int waterAfterDays : plantType.getWateringSchedule()){
            LocalDate waterDueDate = LocalDate.now().plusDays(waterAfterDays);
            this.wateringTasks.add(new Task(Task.Category.Water, waterDueDate, this));
        }

        //Harvest task
        LocalDate harvestDueDate = LocalDate.now().plusDays(plantType.getGrowthTimeDays());
        this.harvestingTask = new Task(Task.Category.Harvest, harvestDueDate, this);
    }

    public Task getNextTask(){
        //Return pre-germination task if it hasn't been completed
        if(this.plantTask.completedAt == null) return this.plantTask;

        //Construct a list of all tasks except planting
        List<Task> locationTasks = new ArrayList<>(this.wateringTasks);
        locationTasks.add(this.harvestingTask);

        //Filter away completed tasks
        List<Task> incompleteLocationTasks = locationTasks.stream().filter(task -> task.completedAt != null).toList();
        if(incompleteLocationTasks.isEmpty()){return null;}

        //Find most relevant task
        Task nextTask = incompleteLocationTasks.getFirst();
        for(Task task : incompleteLocationTasks){
            boolean isNewFirst = task.dueDate.isBefore(nextTask.dueDate);
            boolean isNoneFirst = task.dueDate.isEqual(nextTask.dueDate);
            boolean isWatering = task.category == Task.Category.Water;
            //Prioritize tasks that are closer to be completed, then prioritize watering
            if(isNewFirst || (isNoneFirst && isWatering)){
                nextTask = task;
            }
        }
        return nextTask;

    }

    public List<Task> getTasks(){
        List<Task> tasks = new ArrayList<>();

        tasks.add(this.plantTask);
        tasks.addAll(this.wateringTasks);
        tasks.add(this.harvestingTask);

        return tasks;
    }

    public int getAmount(){
        return this.batchLocations.stream().map(batchLocation -> batchLocation.amount).reduce(0, Integer::sum);
    }

    public Task getPlantTask() {
        return plantTask;
    }

    public Task getHarvestingTask() {
        return harvestingTask;
    }

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
        return batches.stream().filter(batch -> batch.plantTask.completedAt == null).toList();
    }

}
