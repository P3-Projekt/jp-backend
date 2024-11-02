package com.dat3.jpgroentbackend.controllers;

import com.dat3.jpgroentbackend.model.TrayType;
import com.dat3.jpgroentbackend.model.repositories.TrayTypeRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

//import javax.validation.constraints.Min;

@RestController
@Tag(name = "TrayType")
@Validated
public class TrayTypeController {
    @Autowired
    private TrayTypeRepository trayTypeRepository;

    @PostMapping("/TrayType")
    @Operation(
            summary = "Create new TrayType"
    )
    public TrayType CreateTrayType(
            @RequestParam String name,
            @RequestParam @Min(0) int widthCm,
            @RequestParam @Min(0) int lengthCm
    ) {

        //Disallow creating tray type with already existing name
        if(trayTypeRepository.existsById(name)) throw new ResponseStatusException(HttpStatus.CONFLICT ,"A TrayType with id '" + name + "' already exists"); //IdAlreadyExistInDB(name);

        TrayType trayType = new TrayType(name, widthCm, lengthCm);

        return trayTypeRepository.save(trayType);
    }

    @DeleteMapping("TrayType")
    @Operation(
            summary = "Delete a tray"
    )
    public void DeleteTrayType(
            @RequestParam String name
    ){
        trayTypeRepository.deleteById(name);
    }

    @GetMapping("/TrayTypes")
    @Operation(
            summary = "List of all trays",
            responses = {
                    @ApiResponse(content = {@Content(array = @ArraySchema(schema = @Schema(implementation = TrayType.class)))})
            }
    )
    public Iterable<TrayType> GetAllTrayTypes() {

        return trayTypeRepository.findAll();
    }

}
