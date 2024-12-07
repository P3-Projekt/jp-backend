package com.dat3.jpgroentbackend.model.repositories;

import com.dat3.jpgroentbackend.model.PlantType;
import org.springframework.data.repository.CrudRepository;
/**
 * Repository interface for managing PlantType entities.
 * Extends CrudRepository to provide basic CRUD operations.
 */
public interface PlantTypeRepository extends CrudRepository<PlantType, String> {

}