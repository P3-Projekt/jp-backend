package com.dat3.jpgroentbackend.model.repositories;

import com.dat3.jpgroentbackend.model.Rack;
import org.springframework.data.repository.ListCrudRepository;
/**
 * Repository interface for managing Rack entities.
 * Extends ListCrudRepository to provide basic CRUD operations.
 */
public interface RackRepository extends ListCrudRepository<Rack, Integer> {}