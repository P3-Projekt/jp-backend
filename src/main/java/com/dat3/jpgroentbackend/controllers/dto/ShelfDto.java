package com.dat3.jpgroentbackend.controllers.dto;

import com.dat3.jpgroentbackend.model.Shelf;

public class ShelfDto {
    public int rackId;

    public ShelfDto(Shelf shelf) {
        this.rackId = shelf.rack.id;
    }
}
