package com.dat3.jpgroentbackend.controllers;

import com.dat3.jpgroentbackend.controllers.dto.BatchDto;
import com.dat3.jpgroentbackend.controllers.dto.request.CreateBatchRequest;
import com.dat3.jpgroentbackend.controllers.dto.request.UpdateBatchLocationRequest;
import com.dat3.jpgroentbackend.controllers.dto.response.BatchResponse;
import com.dat3.jpgroentbackend.controllers.dto.response.MaxAmountOnShelvesResponse;
import com.dat3.jpgroentbackend.controllers.dto.response.PreGerminatingBatchesResponse;
import com.dat3.jpgroentbackend.model.*;
import com.dat3.jpgroentbackend.model.repositories.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.HashMap;
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
    private RackRepository rackRepository;


    @PostMapping("/Batch")
    @Operation(
            summary = "Create new Batch"
    )
    public BatchDto createBatch(
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
        Batch batch = new Batch(request.amount, plantType, trayType, createdBy);

        //Save batch to database
        Batch batchSaved = batchRepository.save(batch);

        return new BatchDto(batchSaved);
    }

    @GetMapping("/PreGerminatingBatches")
    @Operation(
            summary = "Get a list of pre-germinating batches"
    )
    public PreGerminatingBatchesResponse getPreGerminatingBatches(){
        List<Batch> batches = new ArrayList<>();
        batchRepository.findAll().forEach(batches::add);
        List<Batch> preGerminatingBatches = Batch.filterPreGerminatingBatches(batches);
        return new PreGerminatingBatchesResponse(preGerminatingBatches);
    }

    @GetMapping("/Batches")
    @Operation(
            summary = "Get a list of batches"
    )
    public List<BatchResponse> getBatches(){
        List<Batch> batches = new ArrayList<>();
        batchRepository.findAll().forEach(batches::add);
        return batches.stream().map(BatchResponse::new).toList();
    }

    @GetMapping("/Batch/{batchId}/MaxAmountOnShelves")
    @Operation(
            summary = "Get the max amount of batchId which can be added to every shelf"
    )
    public Map<Integer, List<Integer>> getMaxAmountOnShelves(
            @PathVariable Integer batchId
    ){

        //Find batch in database or throw error
        Batch batch = batchRepository.findById(batchId)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "Batch with id '" + batchId + "' was not found"));


        Map<Rack, List<Integer>> maxAmountOnShelvesByRack = new HashMap<>();
        //Populate maxAmountOnShelvesByRack
        rackRepository.findAll().forEach(rack -> maxAmountOnShelvesByRack.put(rack, rack.getMaxAmountOnShelves(batch)));

        return new MaxAmountOnShelvesResponse(maxAmountOnShelvesByRack).getMaxAmountOnShelves();
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
