package com.dat3.jpgroentbackend.controllers;

import com.dat3.jpgroentbackend.controllers.dto.request.CreateTrayTypeRequest;
import com.dat3.jpgroentbackend.model.TrayType;
import com.dat3.jpgroentbackend.model.repositories.BatchRepository;
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

// REST controller for managing tray types.
@RestController
@Tag(name = "TrayType") // Adds a Swagger tag to group TrayType-related endpoints in API documentation.
@Validated // Enables validation of request parameters and bodies.
public class TrayTypeController {

    @Autowired
    private TrayTypeRepository trayTypeRepository;// Repository for handling TrayType entities.

    @Autowired
    private BatchRepository batchRepository;// Repository for managing Batch entities, used to check dependencies.

    /**
     * Creates a new tray type.
     * @param request The request body containing the details of the tray type to be created.
     * @return The created TrayType entity.
     */
    @PostMapping("/TrayType")
    @Operation(
            summary = "Create new TrayType" // Describes the purpose of the endpoint in Swagger.
    )
    public TrayType CreateTrayType(
            @Valid
            @RequestBody CreateTrayTypeRequest request
    ) {

        // Prevent creating a TrayType if one with the same name already exists.
        if(trayTypeRepository.existsById(request.name)) throw new ResponseStatusException(HttpStatus.CONFLICT ,"A TrayType with id '" + request.name + "' already exists"); //IdAlreadyExistInDB(name);

        // Create a new TrayType entity using the provided request data.
        TrayType trayType = new TrayType(request.name, request.widthCm, request.lengthCm);

        // Save the TrayType to the repository and return the saved entity.
        return trayTypeRepository.save(trayType);
    }

    /**
     * Retrieves all tray types.
     * @return An iterable list of all TrayType entities.
     */
    @GetMapping("/TrayTypes")
    @Operation(
            summary = "List all TrayTypes", // Describes the purpose of the endpoint in Swagger.
            responses = {
                    @ApiResponse(content = {@Content(array = @ArraySchema(schema = @Schema(implementation = TrayType.class)))})
            }
    )
    public Iterable<TrayType> GetAllTrayTypes() {
        // Return all TrayType entities from the repository.
        return trayTypeRepository.findAll();
    }

    /**
     * Inactivates a tray type.
     * @param name The name of the tray type to inactivate.
     * @return The inactivated TrayType entity.
     */
    @PutMapping("/TrayType/{name}/inactivate")
    @Operation(
            summary = "Inactivate a TrayType"
    )
    public TrayType InactivateTrayType(
            @PathVariable String name
    ) {
        // Find the TrayType
        TrayType trayType = trayTypeRepository.findById(name)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "A TrayType with id '" + name + "' does not exist"
                ));

        // Check if the TrayType is already inactive
        if (!trayType.isActive()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "The TrayType '" + name + "' is already inactive"
            );
        }

        // Inactivate the TrayType
        trayType.setInactive();
        return trayTypeRepository.save(trayType);
    }

    /**
     * Reactivates a tray type.
     * @param name The name of the tray type to reactivate.
     * @return The reactivated TrayType entity.
     */
    @PutMapping("/TrayType/{name}/activate")
    @Operation(
            summary = "Activate a TrayType"
    )
    public TrayType ActivateTrayType(
            @PathVariable String name
    ) {
        // Find the TrayType
        TrayType trayType = trayTypeRepository.findById(name)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "A TrayType with id '" + name + "' does not exist"
                ));

        // Check if the TrayType is already active
        if (trayType.isActive()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "The TrayType '" + name + "' is already active"
            );
        }

        // Reactivate the TrayType
        trayType.setActive();
        return trayTypeRepository.save(trayType);
    }
}
