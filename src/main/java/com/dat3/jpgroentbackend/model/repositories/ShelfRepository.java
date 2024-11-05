package com.dat3.jpgroentbackend.model.repositories;

import com.dat3.jpgroentbackend.model.Rack;
import com.dat3.jpgroentbackend.model.Shelf;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ShelfRepository extends CrudRepository<Shelf, Integer> {
    Optional<Shelf> findFirstByRackOrderByPositionDesc(Rack rack);
}