package com.dat3.jpgroentbackend.model.repositories;

import com.dat3.jpgroentbackend.model.Rack;
import com.dat3.jpgroentbackend.model.Shelf;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

/**
 * Repository interface for managing Shelf entities.
 * Extends CrudRepository to provide basic CRUD operations.
 */
public interface ShelfRepository extends CrudRepository<Shelf, Integer> {

    /**
     * Finds the shelf with the highest position in the specified rack.
     * @param rack the rack to search within.
     * @return an Optional containing the shelf with the highest position, or empty if none exist.
     */
    Optional<Shelf> findFirstByRackOrderByPositionDesc(Rack rack);

}