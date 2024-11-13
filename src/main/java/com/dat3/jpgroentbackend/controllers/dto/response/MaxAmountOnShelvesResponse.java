package com.dat3.jpgroentbackend.controllers.dto.response;

import com.dat3.jpgroentbackend.model.Rack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MaxAmountOnShelvesResponse {
    private final Map<Integer, List<Integer>> maxAmountOnShelves = new HashMap<>();

    public MaxAmountOnShelvesResponse(Map<Rack, List<Integer>> maxAmountOnShelvesByRack) {
        for(Map.Entry<Rack, List<Integer>> entry : maxAmountOnShelvesByRack.entrySet()) {
            maxAmountOnShelves.put(entry.getKey().id, entry.getValue());
        }
    }

    public Map<Integer, List<Integer>> getMaxAmountOnShelves() {
        return maxAmountOnShelves;
    }

}
