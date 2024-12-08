package com.dat3.jpgroentbackend.model.repositories;

import com.dat3.jpgroentbackend.model.PlantType;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
/**
 * Repository interface for managing PlantType entities.
 * Extends CrudRepository to provide basic CRUD operations.
 */
public interface PlantTypeRepository extends CrudRepository<PlantType, String> {
    /**
     * Finds a plant type by name and active status.
     * @param name the name of the plant type.
     * @param active the active status of the plant type.
     * @return an Optional containing the PlantType if found, or empty if not found.
     */
    Optional<PlantType> findByNameAndActive(String name, boolean active);
}