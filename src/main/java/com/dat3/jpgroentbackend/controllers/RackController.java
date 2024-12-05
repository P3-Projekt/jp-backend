package com.dat3.jpgroentbackend.controllers;

import com.dat3.jpgroentbackend.controllers.dto.RackDto;
import com.dat3.jpgroentbackend.controllers.dto.ShelfDto;
import com.dat3.jpgroentbackend.controllers.dto.request.CreateRackRequest;
import com.dat3.jpgroentbackend.controllers.dto.response.RacksResponse;
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
import java.util.NoSuchElementException;
import java.util.Optional;

// REST controller for managing racks and shelves
@RestController
@Tag(name = "Rack") // Adds a Swagger tag for documentation purposes
public class RackController{

    @Autowired
    private RackRepository rackRepository; // Repository for Rack entities

    @Autowired
    private ShelfRepository shelfRepository; // Repository for Shelf entities

    @Autowired
    private BatchLocationRepository batchLocationRepository; // Repository for BatchLocation entities


    /**
     * Creates a new rack.
     * @param request DTO containing rack details (coordinates).
     * @return A RackDto representing the created rack.
     */
    @PostMapping("/Rack")
    @Operation(
            summary = "Create new Rack"
    )
    public RackDto createRack(
            @RequestBody CreateRackRequest request
    ){
        // Create a new rack instance and save it to the database
        Rack rack = new Rack(request.getxCoordinate(), request.getyCoordinate());
        Rack savedRack = rackRepository.save(rack);
        return new RackDto(savedRack); // Return a DTO representation of the saved rack
    }

    /**
     * Lists all racks.
     * @return A list of RacksResponse objects representing all racks.
     */
    @GetMapping("/Racks")
    @Operation(
            summary = "List all racks",
            responses = {
            @ApiResponse(content = {@Content(array = @ArraySchema(schema = @Schema(implementation = RackDto.class)))})
        }
    )
    public List<RacksResponse> getAllRacks() {
        // Fetch all racks from the repository and map them to response DTOs
        return rackRepository.findAll().stream().map(RacksResponse::new).toList();
    }

    /**
     * Updates the position of a rack.
     * @param rackId  The ID of the rack to update.
     * @param request DTO containing new coordinates for the rack.
     * @return A RackDto representing the updated rack.
     */
    @PutMapping("/Rack/{rackId}/Position")
    @Operation(
            summary = "Update the position of a rack"
    )
    public RackDto updateRackPosition(
            @PathVariable int rackId,
            @Valid @RequestBody CreateRackRequest request
    ){
        // Retrieve the rack by ID or throw a NOT_FOUND exception
        Rack rack = rackRepository.findById(rackId)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "No rack with id " + rackId + " was found"));

        // Update the rack's position and save it to the database
        rack.setPosition(request.getxCoordinate(), request.getyCoordinate());
        Rack savedRack = rackRepository.save(rack);
        return new RackDto(savedRack);
    }

    /**
     * Creates a new shelf in a rack.
     * @param rackId The ID of the rack where the shelf should be added.
     * @return A RacksResponse object representing the updated rack.
     */
    @PostMapping("/Rack/{rackId}/Shelf")
    @Operation(
            summary = "Create new Shelf"
    )
    public RacksResponse createShelf(
            @PathVariable int rackId
    ){
        // Retrieve the rack by ID or throw a NOT_FOUND exception
        Rack rack = rackRepository.findById(rackId).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "No rack with id " + rackId + " was found"));
        //Get the highest shelf position or default to 0
        int highestShelfLevel = shelfRepository.findFirstByRackOrderByPositionDesc(rack).map(Shelf::getPosition).orElse(0);
        Shelf shelf = new Shelf(rack, highestShelfLevel + 1);

        // Save the new shelf and return the updated rack response
        shelfRepository.save(shelf);
        return new RacksResponse(rack);
    }


    /**
     * Lists all batches stored on a specific rack.
     * @param rackId The ID of the rack.
     * @return A list of lists, where each inner list represents batches on a specific shelf.
     */
    @GetMapping("/Rack/{rackId}/Batches")
    @Operation(
            summary = "List all batches on rack"
    )
    public List<List<Batch>> getBatches(
            @PathVariable int rackId
    ){
        // Retrieve the rack by ID or throw a NOT_FOUND exception
        Rack rack = rackRepository.findById(rackId)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "No rack with id " + rackId + " was found"));
        List<List<Batch>> batches = new ArrayList<>();
        // Iterate through each shelf in the rack to collect batches
        for (Shelf shelf : rack.getShelves()){
            List<BatchLocation> batchLocations = batchLocationRepository.findByShelf(shelf);
            List<Batch> batchesOnShelf = batchLocations.stream().map((location) -> location.batch).toList();
            batches.add(batchesOnShelf);
        }
        return batches;
    }

    /**
     * Deletes a specific rack.
     * @param rackId The ID of the rack to delete.
     */
    @DeleteMapping("/Rack/{rackId}")
    @Operation(
            summary = "Delete a rack"
    )
    public void deleteRack(
            @PathVariable int rackId
    ) {
        // Retrieve the rack by ID or throw a NOT_FOUND exception
        Rack rack = rackRepository.findById(rackId)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "No rack with id " + rackId + " was found"));

        // Check all shelves in the rack
        if (!rack.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "The rack with id '" + rackId + "' contains one or more batches");
        }
        // Delete the rack
        rackRepository.deleteById(rackId);
    }

    /**
     * Deletes the topmost shelf from a rack.
     * @param rackId The ID of the rack.
     * @return A RacksResponse object representing the updated rack.
     */
    @DeleteMapping("/Rack/{rackId}/Shelf")
    @Operation(
            summary = "Delete a shelf from a rack"
    )public RacksResponse deleteShelf(@PathVariable int rackId) {
        // Retrieve the rack by ID or throw a NOT_FOUND exception
        Rack rack = rackRepository.findById(rackId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "A Rack with id '" + rackId + "' does not exist"));

        try {
            // Check if the topmost shelf is empty and remove it if true
            if (rack.getShelves().getLast().isEmpty()) {
                rack.removeShelf();
            } else {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "The top most shelf is not empty on rack with id '" + rackId + "'");
            }
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "The rack has no shelves to remove");
        }
        // Save the updated rack and return a response DTO
        rackRepository.save(rack);
        return new RacksResponse(rack);
    }
}