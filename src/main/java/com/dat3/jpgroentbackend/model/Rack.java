package com.dat3.jpgroentbackend.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.Comparator;
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

    public List<Shelf> getShelves() {
        return shelves.stream().sorted(Comparator.comparingInt(Shelf::getPosition)).toList();
    }

    //Gets a list where every index corresponds to a shelf and the value corresponds to the maximum amount of a batch which can be added to that shelf
    public List<Integer> getMaxAmountOnShelves(Batch batch) {
        List<Integer> maxAmountOnShelves = new ArrayList<>();
        for(Shelf shelf : shelves) {
            int shelfArea = Shelf.length * Shelf.width;

            //Calculate occupied area
            int occupiedArea = shelf.batchLocations.stream().map(location -> {
                int amount = location.amount;
                int width = location.batch.trayType.widthCm;
                int length = location.batch.trayType.lengthCm;
                return amount * width * length;
            }).reduce(0, Integer::sum);

            int batchArea = batch.trayType.widthCm * batch.batchLocations.size();

            int maxAmountOfBatchOnShelf = (shelfArea - occupiedArea)/batchArea;
            maxAmountOnShelves.add(maxAmountOfBatchOnShelf);
        }
        return maxAmountOnShelves;
    }
}
