package com.dat3.jpgroentbackend.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class Shelf {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    public int id;

    @ManyToOne
    @JoinColumn(name = "rack_id")
    public Rack rack;

    @OneToMany(mappedBy = "shelf")
    public List<BatchLocation> batchLocations;

    public int position;

    public Shelf(){}
    public Shelf(Rack rack, int position) {
        this.rack = rack;
        this.position = position;
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
