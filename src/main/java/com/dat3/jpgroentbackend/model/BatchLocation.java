package com.dat3.jpgroentbackend.model;

import jakarta.persistence.*;

@Entity
public class BatchLocation {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    public int id;

    public int amount;

    @ManyToOne
    public Batch batch;

    @ManyToOne
    public Shelf shelf;

    public BatchLocation() {}
    public BatchLocation(Batch batch, Shelf shelf, int amount) {
        this.batch = batch;
        this.shelf = shelf;
        this.amount = amount;
    }
}
