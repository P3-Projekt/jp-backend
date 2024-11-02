package com.dat3.jpgroentbackend.model;

import jakarta.persistence.*;

@Entity
public class Shelf {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    public int id;

    @ManyToOne
    @JoinColumn(name = "rack_id")
    public Rack rack;

    public Shelf(){}
    public Shelf(Rack rack) {
        this.rack = rack;
    }
}
