package com.dat3.jpgroentbackend.model.entities;

import com.dat3.jpgroentbackend.model.abstractions.AbstractPlantDetail;
import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class PlantType extends AbstractPlantDetail {

    protected PlantType() {
        super();
    }

    public PlantType(String name, short growthTimeDays, short pregerminationTimeDays, short[] wateringSchedule) {
        super(name, growthTimeDays, pregerminationTimeDays, wateringSchedule);
    }
}
