package com.dat3.jpgroentbackend.model.repositories;

import com.dat3.jpgroentbackend.model.Batch;
import org.springframework.data.repository.CrudRepository;

/**
 * Repository interface for managing Batch entities.
 * Extends CrudRepository to provide basic CRUD operations.
 */
public interface BatchRepository extends CrudRepository<Batch, Integer> {
    /**
     * Checks if any batch exists with the specified tray type name.
     * @param trayTypeName the name of the tray type to check.
     * @return true if a batch with the specified tray type exists, false otherwise.
     */
    boolean existsByTrayType_Name(String trayTypeName);

    /**
     * Checks if any batch exists with the specified plant type name.
     * @param plantTypeName the name of the plant type to check.
     * @return true if a batch with the specified plant type exists, false otherwise.
     */
    boolean existsByPlantType_Name(String plantTypeName);
}
