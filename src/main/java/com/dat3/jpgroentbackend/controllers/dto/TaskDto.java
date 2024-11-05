package com.dat3.jpgroentbackend.controllers.dto;

import com.dat3.jpgroentbackend.model.Task;

public class TaskDto {
    public int taskId;
    public Task.Category category;
    public String description;
    public String dueDate;
    public int batchId;
    public String completedBy;
    public String completedDate;


    public TaskDto(Task task) {
        this.taskId = task.id;
        this.category = task.category;
        this.description = task.description;
        this.dueDate = task.dueDate.toString();
        this.batchId = task.batch.id;

        /*
        if(task.completedBy != null) {
            this.completedBy = task.completedBy.name;
        }
         */
        if(task.completedAt != null) {
            this.completedDate = task.completedAt.toString();
        }
    }
}
