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

    @OneToMany
    public List<Task> tasks = new ArrayList<>();

    public Batch() {}
    public Batch(PlantType plantType, TrayType trayType, User createdBy) {
        this.plantType = plantType;
        this.trayType = trayType;
        this.createdBy = createdBy;
        this.createdAt = LocalDateTime.now();
        tasks.add(new Task(Task.Category.Harvest, null, LocalDate.now().plusDays(1), this));
    }
}
