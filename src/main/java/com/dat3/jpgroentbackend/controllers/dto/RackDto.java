package com.dat3.jpgroentbackend.controllers.dto;

import com.dat3.jpgroentbackend.model.Rack;
import com.dat3.jpgroentbackend.model.repositories.utils.Vector2;

import java.util.ArrayList;
import java.util.List;

// A Data Transfer Object (DTO) for representing Rack details in API requests or responses.
// Converts a Rack model into a simplified structure for external use.
public class RackDto {

    private int id; // Unique identifier for the rack
    private Vector2 position; // The 2D position of the rack in the facility
    private List<ShelfDto> shelves; // List of shelves contained within the rack

    /**
     * Constructs a RackDto using a Rack object.
     * @param rack The Rack object to be transformed into a DTO.
     */
    public RackDto(Rack rack) {
        this.id = rack.getId();
        this.position = rack.getPosition();
        if(rack.getShelves() != null) {
            this.shelves = rack.getShelves().stream().map(ShelfDto::new).toList();
        } else {
            this.shelves = new ArrayList<>();
        }
    }
}
