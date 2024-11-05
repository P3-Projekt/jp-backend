package com.dat3.jpgroentbackend.model;

import jakarta.persistence.*;
import org.springframework.lang.Nullable;


import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
public class Task {

    public enum Category {
        Water, Harvest, Plant
    }

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    public int id;

    public Category category;
    public String description;
    public LocalDate dueDate;

    @ManyToOne
    public Batch batch;

    //@OneToOne
    //public User completedBy;

    public LocalDateTime completedAt;

    public Task(){};
    public Task(Category category, String description, LocalDate dueDate, Batch batch) {
        this.category = category;
        this.description = description;
        this.dueDate = dueDate;
        this.batch = batch;
    }

    public void complete(User completedBy) {
        //this.completedBy = completedBy;
        this.completedAt = LocalDateTime.now();
    }
}
