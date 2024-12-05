package com.dat3.jpgroentbackend.model;

import com.dat3.jpgroentbackend.model.repositories.utils.IntArrayToJsonConverter;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.util.Arrays;

@Entity
public class PlantType {

    public enum PreferredPosition {
        Low,
        High,
        NoPreferred
    }

    @Id
    private String name;
    private int preGerminationDays;
    private int growthTimeDays;
    private PreferredPosition preferredPosition;
    @Convert(converter = IntArrayToJsonConverter.class)
    private int[] wateringSchedule;

    public PlantType(String name, int preGerminationDays, int growthTimeDays, PreferredPosition preferredPosition, int[] wateringSchedule) {
        this.name = name;
        this.preGerminationDays = preGerminationDays;
        this.growthTimeDays = growthTimeDays;
        this.preferredPosition = preferredPosition;
        this.wateringSchedule = Arrays.stream(wateringSchedule).distinct().sorted().toArray();
    }

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
}

