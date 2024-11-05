package com.dat3.jpgroentbackend.model.repositories;

import com.dat3.jpgroentbackend.model.Task;
import org.springframework.data.repository.CrudRepository;

public interface TaskRepository extends CrudRepository<Task, Integer> {
}
