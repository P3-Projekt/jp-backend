package com.dat3.jpgroentbackend.model;

import com.dat3.jpgroentbackend.model.repositories.utils.IntArrayToJsonConverter;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.util.Arrays;


// Represents the plant type and its growth-related attributes.
@Entity
public class PlantType {

    // Enum to define the preferred position of the plant for optimal growth.
    public enum PreferredPosition {
        Low,
        High,
        NoPreferred
    }

    @Id // Specifies the primary key for the entity
    private String name;
    private int preGerminationDays;
    private int growthTimeDays;
    private PreferredPosition preferredPosition;
    // Custom converter to store and retrieve int arrays as JSON in the database
    @Convert(converter = IntArrayToJsonConverter.class)
    private int[] wateringSchedule;
    private boolean active = true;

    /**
     * Constructor to initialize a PlantType object.
     * @param name               The name of the plant type.
     * @param preGerminationDays Days required for pre-germination.
     * @param growthTimeDays     Total growth time in days.
     * @param preferredPosition  The preferred position of the plant.
     * @param wateringSchedule   The watering schedule as an array of integers (e.g., days of the week).
     */
    public PlantType(String name, int preGerminationDays, int growthTimeDays, PreferredPosition preferredPosition, int[] wateringSchedule) {
        this.name = name;
        this.preGerminationDays = preGerminationDays;
        this.growthTimeDays = growthTimeDays;
        this.preferredPosition = preferredPosition;
        // Ensures the watering schedule is unique and sorted for consistency
        this.wateringSchedule = Arrays.stream(wateringSchedule).distinct().sorted().toArray();
    }

    // Default constructor for storing in the database
    public PlantType() {}

    // Getters for the PlantType properties
    public String getName() {
        return name;
    }

    public int getPreGerminationDays() {
        return preGerminationDays;
    }

    public int getGrowthTimeDays() {
        return growthTimeDays;
    }

    public PreferredPosition getPreferredPosition() {
        return preferredPosition;
    }

    public int[] getWateringSchedule() {
        return wateringSchedule;
    }

    public boolean isActive() {
        return active;
    }

    public void setInactive() {
        active = false;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}

