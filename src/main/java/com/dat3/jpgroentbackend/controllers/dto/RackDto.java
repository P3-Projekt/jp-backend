package com.dat3.jpgroentbackend.controllers.dto;

import com.dat3.jpgroentbackend.model.Rack;

import java.util.ArrayList;
import java.util.List;

public class RackDto {
    public int id;
    public int xCoordinate;
    public int yCoordinate;
    public List<ShelfDto> shelves;
    public RackDto(Rack rack) {
        this.id = rack.id;
        this.xCoordinate = rack.xCoordinate;
        this.yCoordinate = rack.yCoordinate;
        if(rack.getShelves() != null) {
            this.shelves = rack.getShelves().stream().map(ShelfDto::new).toList();
        } else {
            this.shelves = new ArrayList<>();
        }

    }
}
