package com.dat3.jpgroentbackend.model.repositories;

import com.dat3.jpgroentbackend.model.TrayType;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

/**
 * Repository interface for managing TrayType entities.
 * Extends CrudRepository to provide basic CRUD operations.
 */
public interface TrayTypeRepository extends CrudRepository<TrayType, String> {
    /**
     * Finds a tray type by name and active status.
     * @param name the name of the tray type.
     * @param active the active status of the tray type.
     * @return an Optional containing the TrayType if found, or empty if not found.
     */
    Optional<TrayType> findByNameAndActive(String name, boolean active);
}
