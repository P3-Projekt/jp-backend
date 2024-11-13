package com.dat3.jpgroentbackend.model.repositories.utils;

import jakarta.persistence.Embeddable;

// For storing rack positions compact
@Embeddable
public class Vector2 {
    private int x;
    private int y;

    public Vector2() {}

    public Vector2(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public String toString() {
        return "Vector2(" + x + ", " + y + ")";
    }
}
