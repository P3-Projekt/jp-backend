package com.dat3.jpgroentbackend.model.repositories.utils;

import jakarta.persistence.Embeddable;

// Represents a compact 2D vector for storing rack positions.
//  Marked as embeddable for JPA to include it within another entity.
@Embeddable
public class Vector2 {

    private int x;
    private int y;

    // Default constructor for JPA
    public Vector2() {}

    /**
     * Constructs a Vector2 with given x and y coordinates.
     * @param x X-coordinate.
     * @param y Y-coordinate.
     */
    public Vector2(int x, int y) {
        this.x = x;
        this.y = y;
    }

    // Getters for the x and y coordinates
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    /**
     * Converts the vector to a string representation.
     * @return String representation in the format "Vector2(x, y)".
     */
    @Override
    public String toString() {
        return "Vector2(" + x + ", " + y + ")";
    }
}
