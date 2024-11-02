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
        Middle,
        High,
        NoPreferred
    }

    @Id
    public String name;
    public int preGerminationDays;
    public int growthTimeDays;
    public PreferredPosition preferredPosition;
    @Convert(converter = IntArrayToJsonConverter.class)
    public int[] wateringSchedule;

    public PlantType(String name, int preGerminationDays, int growthTimeDays, PreferredPosition preferredPosition, int[] wateringSchedule) {
        this.name = name;
        this.preGerminationDays = preGerminationDays;
        this.growthTimeDays = growthTimeDays;
        this.preferredPosition = preferredPosition;
        this.wateringSchedule = Arrays.stream(wateringSchedule).distinct().sorted().toArray();
    }

    public PlantType() {}
}
