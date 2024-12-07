package com.dat3.jpgroentbackend.model;

import com.dat3.jpgroentbackend.model.repositories.utils.Vector2;
import jakarta.persistence.*;
import java.util.Comparator;
import java.util.List;
import java.util.ArrayList;

// Represents a rack in the system
@Entity
public class Rack {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private int id;

    // Embedded object to store the 2D position of the rack
    @Embedded
    private Vector2 position;

    @OneToMany(mappedBy = "rack", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<Shelf> shelves = new ArrayList<>();

    // Default constructor for storing in the database
    public Rack(){}

    /**
     * Constructor to initialize a rack with specific coordinates.
     * @param xCoordinate The x-coordinate of the rack's position.
     * @param yCoordinate The y-coordinate of the rack's position.
     */
    public Rack(int xCoordinate, int yCoordinate) {
        position = new Vector2(xCoordinate, yCoordinate);
    }

    // Getter for the rack ID
    public int getId() {
        return id;
    }

    // Getter for the position of the rack
    public Vector2 getPosition() {
        return position;
    }

    /**
     * Sets the position of the rack.
     * @param xCoordinate The new x-coordinate.
     * @param yCoordinate The new y-coordinate.
     */
    public void setPosition(int xCoordinate, int yCoordinate) {
        position = new Vector2(xCoordinate, yCoordinate);
    }

    /**
     * Gets the list of shelves associated with the rack.
     * The shelves are sorted by their position.
     * @return A sorted list of shelves or `null` if shelves are empty.
     */
    public List<Shelf> getShelves() {
        if (!shelves.isEmpty()) {
            return shelves.stream().sorted(Comparator.comparingInt(Shelf::getPosition)).toList();
        }
        return shelves;
    }

    // Adds a shelf
    public void addShelf(Shelf shelf) {
        shelves.add(shelf);
    }

    // Removes a shelf
    public void removeShelf() {
        shelves.removeLast();
    }

    /**
     * Checks whether all shelves in the rack are empty.
     * @return `true` if all shelves are empty, `false` otherwise.
     */
    public boolean isEmpty() {
        for (Shelf shelf : getShelves()) {
            if (!shelf.getBatchLocations().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Calculates the maximum amount of a batch that can be added to each shelf in the rack.
     * @param batch The batch to be placed on the shelves.
     * @return A list where each index corresponds to a shelf and its value represents the maximum amount of the batch that can fit.
     */
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
                .mapToInt(BatchLocation::getBatchAmount) // Map to their amounts
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
