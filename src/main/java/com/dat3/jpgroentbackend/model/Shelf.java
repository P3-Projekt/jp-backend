package com.dat3.jpgroentbackend.model;

import com.dat3.jpgroentbackend.exceptions.AreaExceededException;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.*;

// Represents a shelf.
@Entity // Database entity
public class Shelf {
    public static final int length = 100;
    public static final int width = 80;

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "rack_id")
    private Rack rack;

    @OneToMany(mappedBy = "shelf") // The "shelf" field in BatchLocation specifies this relationship
    private final List<BatchLocation> batchLocations = new ArrayList<>();

    private int position;

    // Default constructor
    public Shelf(){}

    /**
     * Constructs a new Shelf associated with a specific rack and position.
     * @param rack     The rack to which this shelf belongs.
     * @param position The position of the shelf within the rack.
     */
    public Shelf(Rack rack, int position) {
        this.rack = rack;
        this.position = position;
    }

    // Getters
    public int getId() {
        return id;
    }

    public boolean isEmpty() {
        return this.batchLocations.isEmpty();
    }

    public int getPosition() {
        return position;
    }

    public Rack getRack() {
        return rack;
    }

    public List<BatchLocation> getBatchLocations() {
        return batchLocations;
    }

    public void addBatchLocation(BatchLocation batchLocation) {
        batchLocations.add(batchLocation);
    }

    private int getLength() {
        return length;
    }

    private int getWidth() {
        return width;
    }

    public int getTotalArea() {
        return getLength() * getWidth();
    }

    public int getOccupiedArea() {
        return getBatchLocations().stream().map((location) -> location.getBatch().getTrayType().getArea() * location.getBatchAmount()).reduce(0, Integer::sum);
    }

    public int getMaxAmountOfBatch(Batch batch) {
        int totalArea = getTotalArea();
        int occupiedArea = getOccupiedArea();
        if (occupiedArea > totalArea) {
            throw new AreaExceededException(String.format("Occupied area %d is larger than total area %d on shelf %d", occupiedArea, totalArea, getId()));
        }
        return (getTotalArea() - getOccupiedArea()) / batch.getTrayType().getArea();
    }

    /**
     * Checks if the shelf has any batches
     * @return True if it contains atleast one batchLocation
     */
    public boolean containsBatches() {
        return !getBatchLocations().isEmpty();
    }

    /**
     * Get all the plant types on a shelf
     * @return A set of all the plant types on the shelf
     */
    public Set<PlantType> getPlantTypesOnShelf() {
        Set<PlantType> plantTypesOnShelf = new HashSet<>();
        for (BatchLocation batchLocation : getBatchLocations()) {
            plantTypesOnShelf.add(batchLocation.getBatch().getPlantType());
        }
        return plantTypesOnShelf;
    }

    /**
     * Get all the harvest dates of batches on a shelf
     * @return A set of all the harvest dates of batches on the shelf
     */
    public Set<LocalDate> getHarvestDatesOnShelf() {
        Set<LocalDate> harvestDatesOnShelf = new HashSet<>();
        for (BatchLocation batchLocation : getBatchLocations()) {
            harvestDatesOnShelf.add(batchLocation.getBatch().getHarvestingTask().getDueDate());
        }
        return harvestDatesOnShelf;
    }
}
