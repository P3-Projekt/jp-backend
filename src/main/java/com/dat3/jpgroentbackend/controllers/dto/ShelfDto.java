package com.dat3.jpgroentbackend.controllers.dto;

import com.dat3.jpgroentbackend.model.Shelf;

import java.util.List;


public class ShelfDto {
    public int shelfId;
    public int rackId;
    public int position;
    //public Map<Integer, Integer> batches = new HashMap<>();
    public List<BatchLocationDto> batchLocations;

    public ShelfDto(Shelf shelf) {
        this.shelfId = shelf.getId();
        this.rackId = shelf.getRack().getId();
        this.position = shelf.getPosition();

        this.batchLocations = shelf.getBatchLocations().stream().map(BatchLocationDto::new).toList();
    }
}
