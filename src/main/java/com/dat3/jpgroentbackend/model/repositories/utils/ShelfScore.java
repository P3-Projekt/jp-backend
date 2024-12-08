package com.dat3.jpgroentbackend.model.repositories.utils;

public class ShelfScore {
    private final int shelfId;
    private final int score;
    private final int amount;

    // Constructor
    public ShelfScore(int shelfId, int score, int amount) {
        this.shelfId = shelfId;
        this.score = score;
        this.amount = amount;
    }

    // Getters
    public int getScore() {
        return score;
    }

    public int getAmount() {
        return amount;
    }

    public int getShelfId() {
        return shelfId;
    }
}