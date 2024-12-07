package com.dat3.jpgroentbackend.model;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;

// Represents the tray type.
@Entity // Marks this class as an entity that maps to a database table
public class TrayType {

    @Id
    private String name;

    private int lengthCm;

    private int widthCm;

    /**
     * Constructor to create a TrayType with a given name, length, and width.
     * @param name      The name of the tray type.
     * @param lengthCm  The length of the tray in centimeters.
     * @param widthCm   The width of the tray in centimeters.
     */
    public TrayType(String name, int lengthCm, int widthCm) {
        this.name = name;
        this.lengthCm = lengthCm;
        this.widthCm = widthCm;
    }

    // Default constructor for storing in the database
    public TrayType() {}

    // getters
    public String getName() {
        return name;
    }

    public int getLengthCm() {
        return lengthCm;
    }

    public int getWidthCm() {
        return widthCm;
    }

    public int getArea() {
        return getLengthCm() * getWidthCm();
    }
}