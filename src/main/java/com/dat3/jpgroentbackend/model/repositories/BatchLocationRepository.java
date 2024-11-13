package com.dat3.jpgroentbackend.model.repositories;

import com.dat3.jpgroentbackend.model.Batch;
import com.dat3.jpgroentbackend.model.BatchLocation;
import com.dat3.jpgroentbackend.model.Shelf;
import com.dat3.jpgroentbackend.model.Rack;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface BatchLocationRepository extends CrudRepository<BatchLocation, Integer> {
    List<BatchLocation> findByShelf(Shelf shelf);
    List<Integer> findAmountByBatchId(int batchId);
    void deleteAllByBatch(Batch batch);
}
