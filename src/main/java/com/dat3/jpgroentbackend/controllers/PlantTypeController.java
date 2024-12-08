package com.dat3.jpgroentbackend.controllers;

import com.dat3.jpgroentbackend.controllers.dto.request.CreatePlantTypeRequest;
import com.dat3.jpgroentbackend.model.PlantType;
import com.dat3.jpgroentbackend.model.repositories.BatchRepository;
import com.dat3.jpgroentbackend.model.repositories.PlantTypeRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;

// REST controller for managing batch-related operations
@RestController
@Tag(name = "PlantType") // Swagger tag for the PlantType controller
@Validated // Validate request bodies
public class PlantTypeController{

    // Injects the PlantType repository for database operations
    @Autowired
    private PlantTypeRepository plantTypeRepository;

    // Injects the Batch repository to handle operations related to batches
    @Autowired
    private BatchRepository batchRepository;

    /**
     * Endpoint to create a new PlantType.
     * @param request The request body containing PlantType details.
     * @return The created PlantType.
     */
    @PostMapping("/PlantType")
    @Operation(
            summary = "Create new PlantType"
    )
    public PlantType createPlantType(
            @Valid
            @RequestBody CreatePlantTypeRequest request
    ) {
        // Check if a PlantType with the given name already exists
        if(plantTypeRepository.existsById(request.name)){
            throw new ResponseStatusException(HttpStatus.CONFLICT ,"A PlantType with id '" + request.name + "' already exists"); //IdAlreadyExistInDB(name);
        }

        // Validate business logic for the PlantType creation
        if(request.growthTimeDays < request.preGerminationDays)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Growth time must be equal to or larger than germination time");
        // Ensure no watering schedule date exceeds the total growth time
        if(request.growthTimeDays < Arrays.stream(request.wateringSchedule).max().orElse(0))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "It's not possible to have a watering schedule date higher than the growth time");

        // Create a new PlantType entity from the request and save it to the database
        PlantType plantType = new PlantType(
                request.name,
                request.preGerminationDays,
                request.growthTimeDays,
                request.preferredPosition,
                request.wateringSchedule
        );

        return plantTypeRepository.save(plantType); // Return the saved entity
    }

    /**
     * Endpoint to retrieve all PlantType entities.
     * @return An iterable collection of PlantTypes.
     */
    @GetMapping("/PlantTypes")
    @Operation(
            summary = "List all PlantTypes",
            responses = {
                    @ApiResponse(content = {@Content(array = @ArraySchema(schema = @Schema(implementation = PlantType.class)))})
            }
    )
    public Iterable<PlantType> getAllPlantTypes() {
        // Fetch all PlantType entities from the repository
        return plantTypeRepository.findAll();
    }

    @PutMapping("/PlantType/{name}")
    @Operation(summary = "Set a PlantType inactive")
    public void setPlantTypeInactive(
            @PathVariable String name
    ) {
        // Find the active PlantType by name; throw an exception if not found
        PlantType plantType = plantTypeRepository.findByNameAndActive(name, true)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "PlantType with name '" + name + "' was not found or is already inactive")
                );

        // Set the PlantType's active status to false and save the updated entity
        plantType.setInactive();
        plantTypeRepository.save(plantType);
    }

    @PutMapping("/PlantType/{name}/activate")
    @Operation(summary = "Reactivate a PlantType")
    public void reactivatePlantType(
            @PathVariable String name
    ) {
        // Find the PlantType by name; throw an exception if not found
        PlantType plantType = plantTypeRepository.findById(name)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "PlantType with name '" + name + "' was not found")
                );

        // If the PlantType is already active, throw a BAD_REQUEST exception
        if (plantType.isActive()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "PlantType is already active"
            );
        }

        // Set the PlantType's active status to true and save the updated entity
        plantType.setActive(true);
        plantTypeRepository.save(plantType);
    }
}