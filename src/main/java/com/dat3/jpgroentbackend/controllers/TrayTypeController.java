package com.dat3.jpgroentbackend.controllers;

import com.dat3.jpgroentbackend.controllers.dto.request.CreateTrayTypeRequest;
import com.dat3.jpgroentbackend.model.TrayType;
import com.dat3.jpgroentbackend.model.repositories.TrayTypeRepository;
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
            @Valid
            @RequestBody CreateTrayTypeRequest request
    ) {

        //Disallow creating tray type with already existing name
        if(trayTypeRepository.existsById(request.name)) throw new ResponseStatusException(HttpStatus.CONFLICT ,"A TrayType with id '" + request.name + "' already exists"); //IdAlreadyExistInDB(name);

        TrayType trayType = new TrayType(request.name, request.widthCm, request.lengthCm);

        return trayTypeRepository.save(trayType);
    }

    @DeleteMapping("TrayType/{trayTypeId}")
    @Operation(
            summary = "Delete a TrayType"
    )
    public void DeleteTrayType(
            @PathVariable String trayTypeId
    ){
        if (!trayTypeRepository.existsById(trayTypeId)) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "A TrayType with id '" + trayTypeId + "' does not exist");
        trayTypeRepository.deleteById(trayTypeId);
    }

    @GetMapping("/TrayTypes")
    @Operation(
            summary = "List all TrayTypes",
            responses = {
                    @ApiResponse(content = {@Content(array = @ArraySchema(schema = @Schema(implementation = TrayType.class)))})
            }
    )
    public Iterable<TrayType> GetAllTrayTypes() {

        return trayTypeRepository.findAll();
    }

}
