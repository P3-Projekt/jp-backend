package com.dat3.jpgroentbackend.model;

import jakarta.persistence.*;

// Represents a BatchLocation entity in the database.
// Maps the relationship between a batch and a shelf, along with the amount stored.
@Entity
public class BatchLocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int amount;

    @ManyToOne
    private Batch batch;

    @ManyToOne
    private Shelf shelf;

    // Default constructor for storing in the database
    public BatchLocation() {}

    /**
     * Constructor to initialize a BatchLocation object with specified batch, shelf, and amount.
     * @param batch  The batch associated with this location.
     * @param shelf  The shelf where the batch is stored.
     * @param amount The quantity of the batch stored at this location.
     */
    public BatchLocation(Batch batch, Shelf shelf, int amount) {
        this.batch = batch;
        this.shelf = shelf;
        this.amount = amount;
    }

    // Getters
    public int getId() {
        return id;
    }

    public int getBatchAmount() {
        return amount;
    }

    public Batch getBatch() {
        return batch;
    }

    public Shelf getShelf() {
        return shelf;
    }
}