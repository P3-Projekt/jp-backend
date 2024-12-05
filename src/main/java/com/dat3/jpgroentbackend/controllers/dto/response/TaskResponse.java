package com.dat3.jpgroentbackend.controllers.dto.response;


import com.dat3.jpgroentbackend.model.Task;

import java.time.LocalDate;
import java.time.LocalDateTime;

// A response DTO for representing a task and its associated details.
public class TaskResponse{
    private final int batchId;
    private final int taskId;
    private final String plantType;
    private final int fields;
    private final LocalDate dueDate;
    private final Task.Category category;
    private final LocalDateTime completedAt;
    private String completedBy;

    /**
     * Constructs a TaskResponse based on a Task object.
     *
     * @param task The Task object containing the details to populate this DTO.
     */
    public TaskResponse(Task task) {
        this.batchId = task.getBatch().getId();
        this.taskId = task.getId();
        this.plantType = task.getBatch().getPlantType().getName();
        this.fields = task.getBatch().getAmount();
        this.dueDate = task.getDueDate();
        this.category = task.getCategory();
        this.completedAt = task.getCompletedAt();
        // Populate completedBy if the task has a completedBy user
        if(task.getCompletedBy() != null) {
            this.completedBy = task.getCompletedBy().getName();
        }
    }

    // Getters
    public int getBatchId() {
        return batchId;
    }

    public String getPlantType() {
        return plantType;
    }

    public int getFields() {
        return fields;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public Task.Category getCategory() {
        return category;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public String getCompletedBy() {
        return completedBy;
    }

    public int getTaskId() {
        return taskId;
    }
}
