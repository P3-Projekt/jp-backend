package com.dat3.jpgroentbackend.controllers;

import com.dat3.jpgroentbackend.controllers.dto.RackDto;
import com.dat3.jpgroentbackend.controllers.dto.ShelfDto;
import com.dat3.jpgroentbackend.controllers.dto.request.CreateRackRequest;
import com.dat3.jpgroentbackend.model.Batch;
import com.dat3.jpgroentbackend.model.BatchLocation;
import com.dat3.jpgroentbackend.model.Rack;
import com.dat3.jpgroentbackend.model.Shelf;
import com.dat3.jpgroentbackend.model.repositories.BatchLocationRepository;
import com.dat3.jpgroentbackend.model.repositories.RackRepository;
import com.dat3.jpgroentbackend.model.repositories.ShelfRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@Tag(name = "Rack")
public class RackController{
    @Autowired
    private RackRepository rackRepository;
    @Autowired
    private ShelfRepository shelfRepository;
    @Autowired
    private BatchLocationRepository batchLocationRepository;

    @PostMapping("/Rack")
    @Operation(
            summary = "Create new Rack"
    )
    public RackDto createRack(
            @Valid
            @RequestBody CreateRackRequest request
    ){
        Rack rack = new Rack(request.xCoordinate, request.yCoordinate);
        Rack savedRack = rackRepository.save(rack);
        return new RackDto(savedRack);
    }

    @GetMapping("/Racks")
    @Operation(
            summary = "List all racks",
            responses = {
            @ApiResponse(content = {@Content(array = @ArraySchema(schema = @Schema(implementation = RackDto.class)))})
        }
    )
    public List<RackDto> getAllRacks() {
        return rackRepository.findAll().stream().map(RackDto::new).toList();
    }

    @PutMapping("/Rack/{rackId}/Position")
    @Operation(
            summary = "Update the position of a rack"
    )
    public RackDto updateRackPosition(
            @PathVariable int rackId,
            @Valid @RequestBody CreateRackRequest request
    ){
        //Get rack from database or throw exception if it does not exist
        Rack rack = rackRepository.findById(rackId).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "No rack with id " + rackId + " was found"));

        rack.xCoordinate = request.xCoordinate;
        rack.yCoordinate = request.yCoordinate;
        Rack savedRack = rackRepository.save(rack);
        return new RackDto(savedRack);
    }

    @PostMapping("/Rack/{rackId}/Shelf")
    @Operation(
            summary = "Create new Shelf"
    )
    public ShelfDto createShelf(
            @PathVariable int rackId
    ){
        Rack rack = rackRepository.findById(rackId).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "No rack with id " + rackId + " was found"));
        //Get the highest shelf position or default to 0
        int highestShelfLevel = shelfRepository.findFirstByRackOrderByPositionDesc(rack).map(shelf -> shelf.position).orElse(0);

        Shelf shelf = new Shelf(rack, highestShelfLevel + 1);
        shelfRepository.save(shelf);
        return new ShelfDto(shelf);
    }

    @GetMapping("/Rack/{rackId}/Batches")
    @Operation(
            summary = "List all batches on rack"
    )
    public List<List<Batch>> getBatches(
            @PathVariable int rackId
    ){
        Rack rack = rackRepository.findById(rackId).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "No rack with id " + rackId + " was found"));
        List<List<Batch>> batches = new ArrayList<>();
        for (Shelf shelf : rack.getShelves()){
            List<BatchLocation> batchLocations = batchLocationRepository.findByShelf(shelf);
            List<Batch> batchesOnShelf = batchLocations.stream().map((location) -> location.batch).toList();
            batches.add(batchesOnShelf);
        }
        return batches;
    }

    @DeleteMapping("/Rack/{rackId}")
    @Operation(
            summary = "Delete a rack"
    )
    public void deleteRack(
            @PathVariable int rackId
    ) {
        Rack rack = rackRepository.findById(rackId).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "No rack with id " + rackId + " was found"));
        // Check all shelves in the rack
        for (Shelf shelf : rack.getShelves()) {
            List<BatchLocation> batchLocations = shelf.getBatchLocations();
            // Throw error if the shelf is not empty (cannot delete rack then)
            if (!batchLocations.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "The rack with id'" + rackId + "' has at least one batch on it and cannot be deleted");
            }
        }
        shelfRepository.deleteByRack_Id(rackId);
        rackRepository.deleteById(rackId);
    }

    @DeleteMapping("/Rack/{rackId}/{shelfId}")
    @Operation(
            summary = "Delete a shelf from a rack"
    )public void deleteShelf(
            @PathVariable int rackId,
            @PathVariable int shelfId
    ) {
        // Throw error if rack doesn't exist
        if (!rackRepository.existsById(rackId)) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "A Rack with id '" + rackId + "' does not exist");
        // Get the shelf from id
        Optional<Shelf> optionalShelf = shelfRepository.findById(shelfId);
        // Throw error if shelf doesn't exist
        if (optionalShelf.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "A shelf with id '" + shelfId + "' does not exist");
        Shelf shelf = optionalShelf.get();
        // Throw error if it is not on the rack
        if (shelf.rack.id != rackId) throw new ResponseStatusException(HttpStatus.CONFLICT, "The shelf with id '" + shelfId + "' is not on rack with id '" + rackId + "'");
        // Throw error if the shelf has at least one batch
        if (!shelf.batchLocations.isEmpty()) throw new ResponseStatusException(HttpStatus.CONFLICT, "The shelf with id '" + shelfId + "' contains at least one batch and cannot be deleted");
        shelfRepository.deleteById(shelfId);
    }
}