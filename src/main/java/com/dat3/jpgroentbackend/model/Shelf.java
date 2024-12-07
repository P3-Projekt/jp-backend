package com.dat3.jpgroentbackend.model;

import com.dat3.jpgroentbackend.exceptions.AreaExceededException;
import jakarta.persistence.*;

import java.util.*;

@Entity
public class Shelf {

    public static final int length = 100;
    public static final int width = 80;

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "rack_id")
    private Rack rack;

    @OneToMany(mappedBy = "shelf")
    private List<BatchLocation> batchLocations = new ArrayList<>();

    private int position;

    public Shelf(){}
    public Shelf(Rack rack, int position) {
        this.rack = rack;
        this.position = position;
    }

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

    public void removeBatchLocation(BatchLocation batchLocation) {
        batchLocations.remove(batchLocation);
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
        return getBatchLocations().stream().map((location) -> location.getBatch().getTrayType().getArea() * location.getAmount()).reduce(0, Integer::sum);
    }

    public int getMaxAmountOfBatch(Batch batch) {
        int totalArea = getTotalArea();
        int occupiedArea = getOccupiedArea();
        if (occupiedArea > totalArea) {
            throw new AreaExceededException(String.format("Occupied area %d is larger than total area %d on shelf %d", occupiedArea, totalArea, getId()));
        }
        return (getTotalArea() - getOccupiedArea()) / batch.getTrayType().getArea();
    }
}
