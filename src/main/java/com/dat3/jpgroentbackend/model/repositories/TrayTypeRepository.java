package com.dat3.jpgroentbackend.model.repositories;

import com.dat3.jpgroentbackend.model.TrayType;
import org.springframework.data.repository.CrudRepository;

/**
 * Repository interface for managing TrayType entities.
 * Extends CrudRepository to provide basic CRUD operations.
 */
public interface TrayTypeRepository extends CrudRepository<TrayType, String> {}
