package com.dat3.jpgroentbackend.controllers;

import com.dat3.jpgroentbackend.model.PlantType;
import com.dat3.jpgroentbackend.model.repositories.PlantTypeRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

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
            @RequestParam String name,
            @RequestParam @Parameter(description = "The number of days it takes for the plant to pre-germinate") @Min(0) int preGerminationDays,
            @RequestParam @Parameter(description = "The number of days it takes for the plant to be ready to harvest") @Min(0) int growthTimeDays,
            @RequestParam PlantType.PreferredPosition preferredPosition,
            @Parameter(description = "An list of days after the creation date, where the plant should be watered", example = "[1,5,7]") @RequestParam int[] wateringSchedule
            ) {

        //Check if name i not already used
        if(plantTypeRepository.existsById(name)){
            throw new ResponseStatusException(HttpStatus.CONFLICT ,"A PlantType with id '" + name + "' already exists"); //IdAlreadyExistInDB(name);
        }

        PlantType plantType = new PlantType(name, preGerminationDays, growthTimeDays, preferredPosition, wateringSchedule);
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
