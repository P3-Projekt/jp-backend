package com.dat3.jpgroentbackend.controllers;

import com.dat3.jpgroentbackend.controllers.dto.request.CreateBatchRequest;
import com.dat3.jpgroentbackend.controllers.dto.request.UpdateBatchLocationRequest;
import com.dat3.jpgroentbackend.model.*;
import com.dat3.jpgroentbackend.model.repositories.*;
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
import java.util.Map;

@RestController
@Tag(name = "Batch")
public class BatchController {
    @Autowired
    private BatchRepository batchRepository;
    @Autowired
    private PlantTypeRepository plantTypeRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TrayTypeRepository trayTypeRepository;
    @Autowired
    private BatchLocationRepository batchLocationRepository;
    @Autowired
    private ShelfRepository shelfRepository;
    @Autowired
    private TaskRepository taskRepository;

    @PostMapping("/Batch")
    @Operation(
            summary = "Create new Batch"
    )
    public Batch createBatch(
            @Valid
            @RequestBody CreateBatchRequest request
            ) {

        //Check valid parameters
        PlantType plantType = plantTypeRepository.findById(request.plantTypeId)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "PlantType with id '" + request.plantTypeId + "' was not found"));
        TrayType trayType = trayTypeRepository.findById(request.trayTypeId)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "TrayType with id '" + request.trayTypeId + "' was not found"));
        User createdBy = userRepository.findById(request.createdByUsername)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "User with username '" + request.createdByUsername + "' was not found"));

        //Create batch
        Batch batch = new Batch(plantType, trayType, createdBy);

        //Map batch to locations
        List<BatchLocation> batchLocations = new ArrayList<>();
        for(Map.Entry<Integer, Integer> location : request.locations.entrySet()) {
            int shelfId = location.getKey();
            int amount = location.getValue();
            Shelf shelf = shelfRepository.findById(shelfId).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "Shelf with id '" + shelfId + "' was not found"));
            batchLocations.add(new BatchLocation(batch, shelf, amount));
        }

        //Save tasks to database
        taskRepository.saveAll(batch.tasks);
        //Save batch to database
        Batch batchSaved = batchRepository.save(batch);
        //Save locations to database
        batchLocationRepository.saveAll(batchLocations);

        return batchSaved;
    }

    @GetMapping("/Batch")
    @Operation(
            summary = "Get all batches",
            responses = {
            @ApiResponse(content = {@Content(array = @ArraySchema(schema = @Schema(implementation = Batch.class)))})
    }
    )
    public Iterable<Batch> getAllBatches(
    ){
        return batchRepository.findAll();
    }

    @PutMapping("/Batch/{batchId}/Position")
    @Operation(
            summary = "Update the position of a batch"
    )
        public List<BatchLocation> updateBatchPosition(
            @PathVariable int batchId,
            @Valid
            @RequestBody UpdateBatchLocationRequest request
            ){

        // Check if the batch exists and get a batch object
        Batch batch = batchRepository.findById(batchId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "A batch with id '" + batchId + "' does not exist"));

        // Check if the total amount in the request matches the total amount in the batch currently
        if (request.locations.values().stream().mapToInt(Integer::intValue).sum() != batchLocationRepository.findAmountByBatchId(batchId).stream().mapToInt(Integer::intValue).sum()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "The total amount provided in the request does not equal the current amount for the batch");
        }

        // Delete previous batchLocations for this batch
        batchLocationRepository.deleteAllByBatch(batch);

        // Create batchLocations with the values from the request body
        List<BatchLocation> batchLocations = new ArrayList<>();
        for (Map.Entry<Integer, Integer> location : request.locations.entrySet()) {
            int shelfId = location.getKey();
            int amount = location.getValue();
            Shelf shelf = shelfRepository.findById(shelfId).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "Shelf with id '" + shelfId + "' was not found"));
            batchLocations.add(new BatchLocation(batch, shelf, amount));
            }

        batchLocationRepository.saveAll(batchLocations);

        return batchLocations;
        }

}
