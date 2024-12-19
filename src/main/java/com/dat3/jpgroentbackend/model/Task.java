package com.dat3.jpgroentbackend.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.WeekFields;
import java.util.Locale;

// Represents a task.
// A task is associated with a batch, has a category, a due date, and can be completed by a user.
@Entity // Marks this class as an entity that maps to a database table
public class Task {
    // Enum to categorize the type of task
    public enum Category {
        Water, Harvest, Plant
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private Category category;

    private LocalDate dueDate;

    @ManyToOne
    private User completedBy;

    @ManyToOne
    private Batch batch;

    private LocalDateTime completedAt;

    // Default constructor
    public Task(){}

    /**
     * Constructs a new Task with the given category, due date, and batch.
     * @param category The category of the task.
     * @param dueDate  The due date of the task.
     * @param batch    The batch associated with this task.
     */
    public Task(Category category, LocalDate dueDate, Batch batch) {
        this.category = category;
        this.dueDate = dueDate;
        this.batch = batch;
    }

    /**
     * Marks the task as completed by a user and records the completion timestamp.
     * @param completedBy The user who completed the task.
     */
    public void complete(User completedBy) {
        this.completedBy = completedBy;
        this.completedAt = LocalDateTime.now();
        if(this.category == Category.Harvest) {
            batch.removeBatchLocations();
        }
    }

    /**
     * Checks if the task's due date falls within a specific week of the year.
     * @param weekNumber The week number to check.
     * @return `true` if the task is due in the specified week, `false` otherwise.
     */
    public boolean isTaskDueInWeek(int weekNumber) {
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        int dueDateWeekNumber = this.dueDate.get(weekFields.weekOfWeekBasedYear());
        return dueDateWeekNumber == weekNumber;
    }

    // Getters
    public int getId() {
        return id;
    }

    public Category getCategory() {
        return category;
    }

    public User getCompletedBy() {
        return completedBy;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public Batch getBatch() {
        return batch;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }
}
