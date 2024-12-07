package com.dat3.jpgroentbackend.controllers.dto;

import com.dat3.jpgroentbackend.model.Rack;
import com.dat3.jpgroentbackend.model.repositories.utils.Vector2;

import java.util.ArrayList;
import java.util.List;

public class RackDto {
    public int id;
    public Vector2 position;
    public List<ShelfDto> shelves;
    public RackDto(Rack rack) {
        this.id = rack.getId();
        this.position = rack.getPosition();
        if(rack.getShelves() != null) {
            shelves = rack.getShelves().stream().map(ShelfDto::new).toList();
        } else {
            shelves = new ArrayList<>();
        }
    }
}
