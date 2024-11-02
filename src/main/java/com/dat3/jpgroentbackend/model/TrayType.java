package com.dat3.jpgroentbackend.model;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class TrayType {

    @Id
    public String name;

    public int lengthCm;

    public int widthCm;

    public TrayType(String name, int lengthCm, int widthCm) {
        this.name = name;
        this.lengthCm = lengthCm;
        this.widthCm = widthCm;
    }

    public TrayType() {}
}
