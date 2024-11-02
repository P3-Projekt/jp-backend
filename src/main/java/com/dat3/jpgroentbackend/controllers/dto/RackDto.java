package com.dat3.jpgroentbackend.controllers.dto;

import com.dat3.jpgroentbackend.model.Rack;

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
        this.shelves = rack.shelves.stream().map(ShelfDto::new).toList();
    }
}
