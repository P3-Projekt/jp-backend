package com.dat3.jpgroentbackend.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.WeekFields;
import java.util.Locale;

@Entity
public class Task {

    public enum Category {
        Water, Harvest, Plant
    }

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    public int id;

    public Category category;
    public LocalDate dueDate;

    @OneToOne
    public User completedBy;

    @ManyToOne
    private Batch batch;

    public LocalDateTime completedAt;

    public Task(){}
    public Task(Category category, LocalDate dueDate, Batch batch) {
        this.category = category;
        this.dueDate = dueDate;
        this.batch = batch;
    }

    public void complete(User completedBy) {
        this.completedBy = completedBy;
        this.completedAt = LocalDateTime.now();
    }

    public boolean isTaskDueInWeek(int weekNumber) {
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        int dueDateWeekNumber = this.dueDate.get(weekFields.weekOfWeekBasedYear());
        return dueDateWeekNumber == weekNumber;
    }

    public Batch getBatch() {
        return batch;
    }
}
