package com.dat3.jpgroentbackend.model;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class TrayType {

    @Id
    private String name;

    private int lengthCm;

    private int widthCm;

    public TrayType(String name, int lengthCm, int widthCm) {
        this.name = name;
        this.lengthCm = lengthCm;
        this.widthCm = widthCm;
    }

    public TrayType() {}

    public String getName() {
        return name;
    }

    public int getLengthCm() {
        return lengthCm;
    }

    public int getWidthCm() {
        return widthCm;
    }
}