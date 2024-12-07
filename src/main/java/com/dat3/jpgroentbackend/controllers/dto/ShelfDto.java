package com.dat3.jpgroentbackend.controllers.dto;

import com.dat3.jpgroentbackend.model.Shelf;

import java.util.List;

// A Data Transfer Object (DTO) for representing Shelf details in API requests or responses.
// Simplifies the Shelf model for external use, including its batch locations.
public class ShelfDto {

    // Properties of the Shelf DTO
    private final int shelfId;
    private final int rackId;
    private final int position;
    private final List<BatchLocationDto> batchLocations;

    /**
     * Constructs a ShelfDto using a Shelf object.
     * @param shelf The Shelf object to be transformed into a DTO.
     */
    public ShelfDto(Shelf shelf) {
        this.shelfId = shelf.getId();
        this.rackId = shelf.getRack().getId();
        this.position = shelf.getPosition();
        this.batchLocations = shelf.getBatchLocations().stream().map(BatchLocationDto::new).toList();
    }
}
