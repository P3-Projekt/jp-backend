package com.dat3.jpgroentbackend.controllers.dto.response;


import com.dat3.jpgroentbackend.model.Task;

import java.time.LocalDate;
import java.time.LocalDateTime;

// A response DTO for representing a task and its associated details.
public class TaskResponse{
    public final int batchId;
    public final int taskId;
    public final String plantType;
    public final int fields;
    public final LocalDate dueDate;
    public final Task.Category category;
    public final LocalDateTime completedAt;
    public final boolean isPlaced;
    public String completedBy;

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
        this.isPlaced = task.getIsPlaced();
        // Populate completedBy if the task has a completedBy user
        if(task.getCompletedBy() != null) {
            this.completedBy = task.getCompletedBy().getName();
        }
    }
}
