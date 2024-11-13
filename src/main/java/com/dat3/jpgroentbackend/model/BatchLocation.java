package com.dat3.jpgroentbackend.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Entity
public class BatchLocation {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    public int id;

    public int amount;

    @ManyToOne
    public Batch batch;

    @ManyToOne
    public Shelf shelf;

    @OneToMany
    public List<Task> wateringTasks = new ArrayList<>();

    @OneToOne
    public Task harvestingTask;

    @ManyToOne
    public Task plantTask;

    public BatchLocation() {}
    public BatchLocation(Batch batch, Shelf shelf, int amount) {
        this.batch = batch;
        this.shelf = shelf;
        this.amount = amount;

        //Plant from pre-germination task
        LocalDate plantDueDate = LocalDate.now().plusDays(batch.plantType.preGerminationDays);
        this.plantTask = new Task(Task.Category.Plant, plantDueDate);

        //Water tasks
        for(int waterAfterDays : batch.plantType.wateringSchedule){
            LocalDate waterDueDate = LocalDate.now().plusDays(waterAfterDays);
            this.wateringTasks.add(new Task(Task.Category.Water, waterDueDate));
        }

        //Harvest task
        LocalDate harvestDueDate = LocalDate.now().plusDays(batch.plantType.growthTimeDays);
        this.harvestingTask = new Task(Task.Category.Harvest, harvestDueDate);
    }

    public Task getNextTask(){
        //Return plant tasks if that hasn't been completed
        if(this.plantTask.completedAt == null){
            return this.plantTask;
        }

        //Construct a list of all tasks except planting
        List<Task> locationTasks = new ArrayList<>(this.wateringTasks);
        locationTasks.add(this.harvestingTask);

        //Filter away completed tasks
        List<Task> allIncompleteTasks = locationTasks.stream().filter(task -> task.completedAt != null).toList();
        if(allIncompleteTasks.isEmpty()){return null;}

        //Find most relevant task
        Task nextTask = allIncompleteTasks.get(0);
        for(Task task : locationTasks){
            //Prioritize tasks that are closer to be completed
            if(nextTask.dueDate.isBefore(task.dueDate)) continue;
            //Prioritize watering tasks
            if(task.category == Task.Category.Water) nextTask = task;
        }
        return nextTask;
    }

    private BatchLocation(BatchLocation fromBatchLocation, Shelf shelf, int amount){
        this.amount = amount;
        this.batch = fromBatchLocation.batch;
        this.shelf = shelf;
        this.wateringTasks = Task.copy(fromBatchLocation.wateringTasks);
        this.harvestingTask = Task.copy(fromBatchLocation.harvestingTask);
        this.plantTask = fromBatchLocation.plantTask;
    }

    public List<Task> getTasks(){
        List<Task> tasks = new ArrayList<>(this.wateringTasks);
        tasks.add(this.harvestingTask);
        tasks.add(this.plantTask);
        return tasks;
    }
}
