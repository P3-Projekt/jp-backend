package com.dat3.jpgroentbackend.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @OneToMany(mappedBy = "batch")
    public List<BatchLocation> batchLocations = new ArrayList<>();

    public Batch() {}
    public Batch(int amount, PlantType plantType, TrayType trayType, User createdBy) {
        this.plantType = plantType;
        this.trayType = trayType;
        this.createdBy = createdBy;
        this.createdAt = LocalDateTime.now();

        this.batchLocations.add(new BatchLocation(this, null, amount));
    }

    public List<Task> getAllTasks(){
        List<Task> tasks = new ArrayList<>();

        //Add watering and harvesting
        for (BatchLocation batchLocation : batchLocations) {
            tasks.add(batchLocation.plantTask);
            tasks.addAll(batchLocation.wateringTasks);
            tasks.add(batchLocation.harvestingTask);
        }
        return tasks;
    }

    public static Map<BatchLocation, List<Task>> getAllTasksByLocation(List<Batch> batches, Integer weekDayFilter){
        Map<BatchLocation, List<Task>> tasksByLocation = new HashMap<>();
        for (Batch batch : batches) {
            for(BatchLocation batchLocation : batch.batchLocations){
                List<Task> tasks = batchLocation.getTasks();
                //Filter if weekday is set
                if(weekDayFilter != null){
                    tasks = tasks.stream().filter(task -> task.isTaskDueInWeek(weekDayFilter)).toList();
                }
                //Only add if non-empty
                if(!tasks.isEmpty()){
                    tasksByLocation.put(batchLocation, batchLocation.getTasks());
                }
            }
        }
        return tasksByLocation;
    }

    public static List<Batch> filterPreGerminatingBatches(List<Batch> batches){
        return batches.stream().filter(batch -> batch.batchLocations.get(0).plantTask.completedAt == null).toList();
    }

}
