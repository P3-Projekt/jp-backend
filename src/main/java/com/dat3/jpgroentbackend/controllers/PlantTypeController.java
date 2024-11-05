package com.dat3.jpgroentbackend.controllers;

import com.dat3.jpgroentbackend.controllers.dto.request.CreatePlantTypeRequest;
import com.dat3.jpgroentbackend.model.PlantType;
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

    @PostMapping("/PlantType")
    @Operation(
            summary = "Create new PlantType"
    )
    public PlantType createPlantType(
            @Valid
            @RequestBody CreatePlantTypeRequest request
    ) {
        //Check if name i not already used
        if(plantTypeRepository.existsById(request.name)){
            throw new ResponseStatusException(HttpStatus.CONFLICT ,"A PlantType with id '" + request.name + "' already exists"); //IdAlreadyExistInDB(name);
        }

        //Validate logic
        if(request.growthTimeDays < request.preGerminationDays) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Growth time must be equal to or larger than germination time");
        if(request.growthTimeDays < Arrays.stream(request.wateringSchedule).max().orElse(0)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "It's not possible to have a watering schedule date higher than the growth time");

        PlantType plantType = new PlantType(request.name, request.preGerminationDays, request.growthTimeDays, request.preferredPosition, request.wateringSchedule);
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

}
