package com.dat3.jpgroentbackend.model.repositories;

import com.dat3.jpgroentbackend.model.Task;
import org.springframework.data.repository.CrudRepository;

/**
 * Repository interface for managing Task entities.
 * Extends CrudRepository to provide basic CRUD operations.
 */
public interface TaskRepository extends CrudRepository<Task, Integer> {
}
