package com.dat3.jpgroentbackend.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class Rack {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    public int id;
    public int xCoordinate;
    public int yCoordinate;

    @OneToMany(mappedBy = "rack")
    private List<Shelf> shelves;

    public Rack(){}
    public Rack(int xCoordinate, int yCoordinate) {
        this.xCoordinate = xCoordinate;
        this.yCoordinate = yCoordinate;
    }

    public List<Shelf> getShelves() {
        return shelves;
    }

    public void addShelf(Shelf shelf) {
        shelves.add(shelf);
    }

    public void removeShelf(Shelf shelf) {
        shelves.remove(shelf);
    }

    public void removeShelves() {
        shelves.clear();
    }

    // Return true if all shelves in the rack is empty, false otherwise
    public boolean isEmpty() {
        for (Shelf shelf : getShelves()) {
            if (!shelf.batchLocations.isEmpty()) {
                return false;
            }
        }
        return true;
    }
}
