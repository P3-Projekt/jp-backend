package com.dat3.jpgroentbackend.model.abstractions;

public interface PlantDetail {
    short getGrowthDays();
    short getPregerminationDays();
    short[] getWaterSchedule();
}
