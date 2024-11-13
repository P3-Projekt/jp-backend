package com.dat3.jpgroentbackend.model;

import com.dat3.jpgroentbackend.model.repositories.utils.Vector2;
import jakarta.persistence.*;
import java.util.Comparator;
import java.util.List;
import java.util.ArrayList;

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
        return shelves.stream().sorted(Comparator.comparingInt(Shelf::getPosition)).toList();
    }
    public void addShelf(Shelf shelf) {
        shelves.add(shelf);
    }

    public void removeShelf() {
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

    //Gets a list where every index corresponds to a shelf and the value corresponds to the maximum amount of a batch which can be added to that shelf
    public List<Integer> getMaxAmountOnShelves(Batch batch) {
        List<Integer> maxAmountOnShelves = new ArrayList<>();
        for(Shelf shelf : shelves) {
            int shelfArea = Shelf.length * Shelf.width;

            //Calculate occupied area
            int occupiedArea = shelf.getBatchLocations().stream().map(location -> {
                int amount = location.amount;
                int width = location.batch.trayType.getWidthCm();
                int length = location.batch.trayType.getLengthCm();
                return amount * width * length;
            }).reduce(0, Integer::sum);

            int batchArea = batch.trayType.getWidthCm() * batch.batchLocations.size();

            int maxAmountOfBatchOnShelf = (shelfArea - occupiedArea)/batchArea;
            maxAmountOnShelves.add(maxAmountOfBatchOnShelf);
        }
        return maxAmountOnShelves;
    }
}
