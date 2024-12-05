package com.dat3.jpgroentbackend.model;

import jakarta.persistence.*;

@Entity
public class BatchLocation {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    public int id;

    private int amount;

    @ManyToOne
    private Batch batch;

    @ManyToOne
    private Shelf shelf;

    public BatchLocation() {}
    public BatchLocation(Batch batch, Shelf shelf, int amount) {
        this.batch = batch;
        this.shelf = shelf;
        this.amount = amount;
    }

    public Batch getBatch() {
        return batch;
    }

    public Shelf getShelf() {
        return shelf;
    }
}
