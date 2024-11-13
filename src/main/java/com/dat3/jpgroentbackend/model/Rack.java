package com.dat3.jpgroentbackend.model;

import com.dat3.jpgroentbackend.model.repositories.utils.Vector2;
import jakarta.persistence.*;
import java.util.Collections;
import java.util.List;

@Entity
public class Rack {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private int id;

    @Embedded
    private Vector2 position;

    @OneToMany(mappedBy = "rack", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Shelf> shelves;

    public Rack(){}
    public Rack(int xCoordinate, int yCoordinate) {
        position = new Vector2(xCoordinate, yCoordinate);
    }

    public int getId() {
        return id;
    }

    public Vector2 getPosition() {
        return position;
    }

    public void setPosition(int xCoordinate, int yCoordinate) {
        position = new Vector2(xCoordinate, yCoordinate);
    }

    public List<Shelf> getShelves() {
        return shelves;
    }
    public void pushShelf(Shelf shelf) {
        shelves.add(shelf);
    }

    public void popShelf() {
        shelves.removeLast();
    }

    // Return true if all shelves in the rack is empty, false otherwise
    public boolean isEmpty() {
        for (Shelf shelf : getShelves()) {
            if (!shelf.getBatchLocations().isEmpty()) {
                return false;
            }
        }
        return true;
    }
}


