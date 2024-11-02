package com.dat3.jpgroentbackend.controllers;

import com.dat3.jpgroentbackend.controllers.dto.RackDto;
import com.dat3.jpgroentbackend.controllers.dto.ShelfDto;
import com.dat3.jpgroentbackend.model.Rack;
import com.dat3.jpgroentbackend.model.Shelf;
import com.dat3.jpgroentbackend.model.repositories.RackRepository;
import com.dat3.jpgroentbackend.model.repositories.ShelfRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@Tag(name = "Rack")
@Validated
public class RackController{
    @Autowired
    private RackRepository rackRepository;
    @Autowired
    private ShelfRepository shelfRepository;

    @PostMapping("/Rack")
    @Operation(
            summary = "Create new Rack"
    )
    public Rack createRack(
            @RequestParam int xCoordinate,
            @RequestParam int yCoordinate
    ){
        Rack rack = new Rack(xCoordinate, yCoordinate);
        return rackRepository.save(rack);
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

    @PutMapping("/Rack/Position")
    @Operation(
            summary = "Update the position of a rack"
    )
    public Rack updateRackPosition(
            @RequestParam int id,
            @RequestParam int newXCoordinate,
            @RequestParam int newYCoordinate
    ){
        //Get rack from database or throw exception if it does not exist
        Rack rack = rackRepository.findById(id).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "No rack with id " + id + " was found"));

        rack.xCoordinate = newXCoordinate;
        rack.yCoordinate = newYCoordinate;
        return rackRepository.save(rack);
    }

    @PostMapping("/Rack/{id}/Shelf")
    @Operation(
            summary = "Create new Shelf"
    )
    public ShelfDto createShelf(
            @PathVariable int id
    ){
        Rack rack = rackRepository.findById(id).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "No rack with id " + id + " was found"));
        Shelf shelf = new Shelf(rack);
        shelfRepository.save(shelf);
        return new ShelfDto(shelf);
    }

}