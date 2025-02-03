package com.dat3.jpgroentbackend.model.abstractions;

import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class AbstractPlantDetail extends DataType implements PlantDetail {

    @NotNull
    @Min(0)
    protected final short growthTimeDays;

    @NotNull
    @Min(0)
    protected final short pregerminationTimeDays;

    @NotNull
    @Min(0)
    protected final short[] wateringSchedule;

    protected AbstractPlantDetail() {
        super();
        growthTimeDays = 0;
        pregerminationTimeDays = 0;
        wateringSchedule = null;
    }

    protected AbstractPlantDetail(String name, short growthTimeDays, short pregerminationTimeDays, short[] wateringSchedule) {
        super(name);
        this.growthTimeDays = growthTimeDays;
        this.pregerminationTimeDays = pregerminationTimeDays;
        this.wateringSchedule = wateringSchedule;
    }

    @Override
    public short getGrowthDays() {
        return growthTimeDays;
    }

    @Override
    public short getPregerminationDays() {
        return growthTimeDays;
    }

    @Override
    public short[] getWaterSchedule() {
        return wateringSchedule;
    }
}
