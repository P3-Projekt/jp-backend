package com.dat3.jpgroentbackend.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class Rack {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    public int id;
    public int xCoordinate;
    public int yCoordinate;

    @OneToMany(mappedBy = "rack")
    public List<Shelf> shelves;

    public Rack(){}
    public Rack(int xCoordinate, int yCoordinate) {
        this.xCoordinate = xCoordinate;
        this.yCoordinate = yCoordinate;
    }

}
