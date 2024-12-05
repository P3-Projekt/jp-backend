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

@RestController
@Tag(name = "PlantType")
@Validated
public class PlantTypeController{
    @Autowired
    private PlantTypeRepository plantTypeRepository;
    @Autowired
    private BatchRepository batchRepository;

    @PostMapping("/PlantType")
    @Operation(
            summary = "Create new PlantType"
    )
    public PlantType createPlantType(
            @Valid
            @RequestBody CreatePlantTypeRequest request
    ) {
        //Check if name i not already used
        if(plantTypeRepository.existsById(request.getPlantTypeName())){
            throw new ResponseStatusException(HttpStatus.CONFLICT ,"A PlantType with id '" + request.getPlantTypeName() + "' already exists"); //IdAlreadyExistInDB(name);
        }

        //Validate logic
        if(request.getGrowthTimeDays() < request.getPreGerminationDays()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Growth time must be equal to or larger than germination time");
        if(request.getGrowthTimeDays() < Arrays.stream(request.getWateringSchedule()).max().orElse(0)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "It's not possible to have a watering schedule date higher than the growth time");

        PlantType plantType = new PlantType(request.getPlantTypeName(), request.getPreGerminationDays(), request.getGrowthTimeDays(), request.getPreferredPosition(), request.getWateringSchedule());
        return plantTypeRepository.save(plantType);
    }

    @GetMapping("/PlantTypes")
    @Operation(
            summary = "List all PlantTypes",
            responses = {
                    @ApiResponse(content = {@Content(array = @ArraySchema(schema = @Schema(implementation = PlantType.class)))})
            }
    )
    public Iterable<PlantType> getAllPlantTypes() {
        return plantTypeRepository.findAll();
    }

    @DeleteMapping("/PlantType/{plantTypeName}")
    @Operation(
            summary = "Delete a PlantType"
    )
    public void deletePlantType(
            @PathVariable String plantTypeName
    ) {
        if (!plantTypeRepository.existsById(plantTypeName)) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "A planttype with name '" + plantTypeName + "' does not exist");
        if (batchRepository.existsByPlantType_Name(plantTypeName)) throw new ResponseStatusException(HttpStatus.CONFLICT, "Cannot delete plant '" + plantTypeName + "' as at least one batch is using it");
        plantTypeRepository.deleteById(plantTypeName);
    }
}