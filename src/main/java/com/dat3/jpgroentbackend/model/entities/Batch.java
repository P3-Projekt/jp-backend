package com.dat3.jpgroentbackend.model.entities;

import com.dat3.jpgroentbackend.model.abstractions.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.*;

// Represents a batch of plants, including tasks related to planting, watering, and harvesting.
@Entity
public class Batch {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "plant_type_name", nullable = false)
    @NotNull
    private final AbstractPlantDetail plantType;

    @ManyToOne
    @JoinColumn(name = "tray_type_name", nullable = false)
    @NotNull
    private final AbstractTrayDetail trayType;

    @NotNull
    private final LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "created_by_name")
    private final Employee createdBy;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    //@JoinColumn(name = "batch_id")
    private final List<LocationDetail> batchLocations;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    //@JoinColumn(name = "task_id", nullable = false)
    private final List<Task> tasks;

    protected Batch() {
        this.plantType = null;
        this.trayType = null;
        this.createdAt = null;
        this.createdBy = null;
        this.batchLocations = null;
        this.tasks = null;
    }

    public Batch(AbstractPlantDetail plantType, AbstractTrayDetail trayType, LocalDateTime createdAt, Employee createdBy, List<LocationDetail> batchLocations, List<Task> tasks) {
        this.plantType = plantType;
        this.trayType = trayType;
        this.createdAt = createdAt;
        this.createdBy = createdBy;
        this.batchLocations = batchLocations;
        this.tasks = tasks;
    }
}