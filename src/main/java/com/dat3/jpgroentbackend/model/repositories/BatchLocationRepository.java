package com.dat3.jpgroentbackend.model.repositories;

import com.dat3.jpgroentbackend.model.Batch;
import com.dat3.jpgroentbackend.model.BatchLocation;
import com.dat3.jpgroentbackend.model.Shelf;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Repository interface for managing BatchLocation entities.
 * Extends CrudRepository to provide basic CRUD operations.
 */
public interface BatchLocationRepository extends CrudRepository<BatchLocation, Integer> {
    /**
     * Finds all batch locations for a specific shelf.
     * @param shelf the shelf to filter by.
     * @return a list of BatchLocation entities on the given shelf.
     */
    List<BatchLocation> findByShelf(Shelf shelf);
}
