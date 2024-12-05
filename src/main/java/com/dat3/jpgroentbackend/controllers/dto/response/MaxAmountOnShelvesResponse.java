package com.dat3.jpgroentbackend.controllers.dto.response;

import com.dat3.jpgroentbackend.model.Rack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// A response DTO that maps the maximum amount of items that can be placed on shelves, organized by rack IDs.
public class MaxAmountOnShelvesResponse {

    // Stores the maximum amount of items per shelf for each rack ID
    private final Map<Integer, List<Integer>> maxAmountOnShelves = new HashMap<>();

    /**
     * Constructs a MaxAmountOnShelvesResponse by transforming a map keyed by Rack objects
     * to a map keyed by Rack IDs.
     *
     * @param maxAmountOnShelvesByRack Map of Rack objects to lists of maximum amounts per shelf
     */
    public MaxAmountOnShelvesResponse(Map<Rack, List<Integer>> maxAmountOnShelvesByRack) {
        // Iterate through the map entries and transform the keys (Rack) to their IDs
        for(Map.Entry<Rack, List<Integer>> entry : maxAmountOnShelvesByRack.entrySet()) {
            maxAmountOnShelves.put(entry.getKey().getId(), entry.getValue());
        }
    }

    // Getters
    public Map<Integer, List<Integer>> getMaxAmountOnShelves() {
        return maxAmountOnShelves;
    }

}
