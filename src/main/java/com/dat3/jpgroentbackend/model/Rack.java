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
    private final List<Shelf> shelves = new ArrayList<>();

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
        if (!shelves.isEmpty()) {
            return shelves.stream().sorted(Comparator.comparingInt(Shelf::getPosition)).toList();
        }
        return null;
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
        for(Shelf shelf : getShelves()) {
            int max = shelf.getMaxAmountOfBatch(batch);
            // System.out.printf("\nMax amount on shelf with position %d on rack %d is %d", shelf.getPosition(), shelf.getRack().getId(), max);
            maxAmountOnShelves.add(max);
        }
        return maxAmountOnShelves.reversed();
    }

    public int getTotalBatches() {
        return shelves.stream()
                .flatMap(shelf -> shelf.getBatchLocations().stream()) // Flatten all BatchLocations from all shelves
                .mapToInt(BatchLocation::getAmount) // Map to their amounts
                .sum(); // Sum them up
    }

    public int getPercentageFilled() {
        int numShelves = shelves.size();
        float rackFilledPercentage = 0;
        for (Shelf shelf : shelves) {
            float shelfFilledPercentage = (float)shelf.getOccupiedArea() / shelf.getTotalArea();
            rackFilledPercentage += (shelfFilledPercentage * 100) / numShelves;
        }
        return Math.round(rackFilledPercentage);
    }
}
