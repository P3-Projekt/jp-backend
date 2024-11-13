package com.dat3.jpgroentbackend.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

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
}
