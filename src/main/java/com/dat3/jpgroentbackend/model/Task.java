package com.dat3.jpgroentbackend.model.repositories;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Task {

    enum Category {
        Water, Harvest, Plant
    }

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    public int id;
}
