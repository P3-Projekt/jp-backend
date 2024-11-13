package com.dat3.jpgroentbackend.model.repositories;

import com.dat3.jpgroentbackend.model.Batch;
import com.dat3.jpgroentbackend.model.PlantType;
import org.springframework.data.repository.CrudRepository;

public interface BatchRepository extends CrudRepository<Batch, Integer> {
    boolean existsByTrayType_Name(String trayTypeName);
    boolean existsByPlantType_Name(String plantTypename);
}
