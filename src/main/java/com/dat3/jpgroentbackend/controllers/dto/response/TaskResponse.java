package com.dat3.jpgroentbackend.controllers.dto.response;


import com.dat3.jpgroentbackend.model.Task;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class TaskResponse{
    private final int batchId;
    private final int taskId;
    private final String plantType;
    private final int fields;
    private final LocalDate dueDate;
    private final Task.Category category;
    private final LocalDateTime completedAt;
    private String completedBy;

    public TaskResponse(Task task) {
        this.batchId = task.getBatch().id;
        this.taskId = task.id;
        this.plantType = task.getBatch().plantType.getName();
        this.fields = task.getBatch().getAmount();
        this.dueDate = task.dueDate;
        this.category = task.category;
        this.completedAt = task.completedAt;
        if(task.completedBy != null) {
            this.completedBy = task.completedBy.getName();
        }
    }

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
