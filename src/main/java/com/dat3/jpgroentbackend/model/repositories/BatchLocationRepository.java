package com.dat3.jpgroentbackend.model.repositories;

import com.dat3.jpgroentbackend.model.BatchLocation;
import com.dat3.jpgroentbackend.model.Shelf;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface BatchLocationRepository extends CrudRepository<BatchLocation, Integer> {
    List<BatchLocation> findByShelf(Shelf shelf);
}
