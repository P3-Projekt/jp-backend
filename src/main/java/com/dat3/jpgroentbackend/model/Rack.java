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
        if (shelves != null) {
            return shelves.stream().sorted(Comparator.comparingInt(Shelf::getPosition)).toList();
        }
        return null;
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
        for(Shelf shelf : shelves) {
            int shelfArea = Shelf.length * Shelf.width; // Total shelf area

            // Calculate occupied area on a shelf
            int occupiedArea = shelf.getBatchLocations().stream().map(location -> {
                int amount = location.getBatchAmount();
                int width = location.getBatch().getTrayType().getWidthCm();
                int length = location.getBatch().getTrayType().getLengthCm();
                return amount * width * length;
            }).reduce(0, Integer::sum);

            // Calculate the area required for the given batch
            int batchArea = batch.getTrayType().getWidthCm() * batch.getBatchLocations().size();

            // Calculate the maximum amount of the batch that can fit on the shelf
            int maxAmountOfBatchOnShelf = (shelfArea - occupiedArea)/batchArea;
            maxAmountOnShelves.add(maxAmountOfBatchOnShelf);
        }
        return maxAmountOnShelves;
    }
}
