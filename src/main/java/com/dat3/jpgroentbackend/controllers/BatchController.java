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
import org.springframework.security.access.expression.SecurityExpressionHandler;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import com.dat3.jpgroentbackend.model.PlantType.PreferredPosition;

import java.time.LocalDate;
import java.util.*;


@RestController // REST controller for managing batch-related operations
@Tag(name = "Batch") // Swagger tag for the Batch controller
public class BatchController {

    // Dependencies are injected for accessing various repositories
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
    @Autowired
    private SecurityExpressionHandler securityExpressionHandler;

    /**
     * Creates a new batch based on the request data.
     * @param request CreateBatchRequest object containing details for the new batch
     * @return BatchDto representation of the newly created batch
     */
    @PostMapping("/Batch")
    @Operation(
            summary = "Create new Batch"
    )
    public BatchDto createBatch(
            @Valid
            @RequestBody CreateBatchRequest request // Validates request payload
            ) {

        // Fetch PlantType, TrayType, and User; throw error if not found
        PlantType plantType = plantTypeRepository.findById(request.getPlantTypeId())
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "PlantType with id '" + request.getPlantTypeId() + "' was not found"));
        TrayType trayType = trayTypeRepository.findById(request.getTrayTypeId())
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "TrayType with id '" + request.getTrayTypeId() + "' was not found"));
        User createdBy = userRepository.findById(request.getCreatedByUsername())
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "User with username '" + request.getCreatedByUsername() + "' was not found"));

        //Create batch
        Batch batch = new Batch(request.getAmount(), plantType, trayType, createdBy);

        //Save batch to database
        Batch batchSaved = batchRepository.save(batch);

        // Return the saved batch as a DTO
        return new BatchDto(batchSaved);
    }

    /**
     * Fetches all pre-germinating batches.
     * @return PreGerminatingBatchesResponse containing a list of pre-germinating batches
     */
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

    /**
     * Retrieves all batches.
     * @return List of BatchResponse objects representing all batches
     */
    @GetMapping("/Batches")
    @Operation(
            summary = "Get a list of batches"
    )
    public List<BatchResponse> getBatches(){
        List<Batch> batches = new ArrayList<>();
        batchRepository.findAll().forEach(batches::add);
        return batches.stream().map(BatchResponse::new).toList();
    }

    /**
     * Calculates the maximum amount of a batch that can be added to each shelf.
     * @param batchId ID of the batch
     * @return A map of shelf positions to maximum batch amounts
     */
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

    /**
     * Updates the position of a batch.
     * @param batchId ID of the batch to update
     * @param request UpdateBatchLocationRequest containing new position details
     */
    @PutMapping("/Batch/{batchId}/Position")
    @Operation(
            summary = "Update the position of a batch"
    )
        public void updateBatchPosition(
            @PathVariable int batchId,
            @Valid
            @RequestBody UpdateBatchLocationRequest request
            ){

        // Check if the batch exists and get a batch object
        Batch batch = batchRepository.findById(batchId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "A batch with id '" + batchId + "' does not exist"));
        // Check if user exists
        User user = userRepository.findById(request.getUsername()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "A batch with id '" + batchId + "' does not exist"));


        // Get amount from empty batch location and remove it
        if(batch.batchLocations.size() != 1 || batch.batchLocations.getFirst().shelf != null){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Batch already has locations");
        }
        BatchLocation emptyBatchLocation = batch.batchLocations.getFirst();
        int batchAmount = emptyBatchLocation.amount;

        // Check if location amounts are complete
        int locatedAmount = request.getLocations().values().stream().reduce(0, Integer::sum);
        if(batchAmount != locatedAmount) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Located amount was " + locatedAmount + " but batch contains " + batchAmount + " fields");

        // Create batchLocations with the values from the request body
        List<BatchLocation> batchLocations = new ArrayList<>();
        for (Map.Entry<Integer, Integer> location : request.getLocations().entrySet()) {
            int shelfId = location.getKey();
            int amount = location.getValue();
            Shelf shelf = shelfRepository.findById(shelfId).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "Shelf with id '" + shelfId + "' was not found"));
            batchLocations.add(new BatchLocation(batch, shelf, amount));
        }


        // Populate batch with newly created locations
        batch.batchLocations.clear();
        batch.batchLocations.addAll(batchLocations);
        //batch.
        batch.getPlantTask().complete(user);
        batchRepository.save(batch);
        }

// Helper class for autolocateBatch

class ScoreObj {
        private final int position;
        private final int score;
        private final int amount;
        private final int rackId;

        // Constructor
        private ScoreObj(int position, int score, int amount, int rackId) {
            this.position = position;
            this.score = score;
            this.amount = amount;
            this.rackId = rackId;
        }

        // Getters
        private int getScore() {
            return score;
        }

        private int getAmount() {
            return amount;
        }

        private int getPosition() {
            return position;
        }

        private int getRackId() {
            return rackId;
        }
}

    /**
     * Suggests optimal locations for placing a batch based on predefined criteria.
     * @param batchId ID of the batch
     * @return Map of rack IDs to shelf positions and their respective suggested amounts
     */
    @GetMapping("/Batch/{batchId}/Autolocate")
    @Operation(
            summary = "Calculate the optimal location(s) for a batch"
    )
    public Map<Integer, Map<Integer, Integer>> autolocateBatch(
            @PathVariable int batchId
    ) {
        // Check if the batch exists and get a batch object
        Batch batch = batchRepository.findById(batchId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "A batch with id '" + batchId + "' does not exist"));
        PlantType plantType = batch.getPlantType();
        PreferredPosition preferredPosition = plantType.getPreferredPosition();
        
        // Throw an exception if the batch has already been placed
        if (!batch.batchLocations.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Batch has already been placed");
        }
        
        List<ScoreObj> scoreList = new ArrayList<>();
        List<Rack> racks = rackRepository.findAll();

        for (Rack rack : racks) {
            List<Integer> maxAmountOnShelves = rack.getMaxAmountOnShelves(batch);

            int highShelfIndex = rack.getShelves().size() - 1; // Top position in the rack
            boolean rackContainsBatches = rackContainsBatches(rack);
            for (Shelf shelf : rack.getShelves()) {
                // Get the max amount from this batch that can be placed on the current shelf
                int maxOnThisShelf = maxAmountOnShelves.get(shelf.getPosition());

                // If the shelf cant have any trays of the batch on it, continue
                if (maxOnThisShelf == 0) continue;

                // Sets the score to 10 or 0 depending on if the rack contains batches
                int score = rackContainsBatches ? 10 : 0;

                // Increases score if the plant type has a preferred position and the shelf matches the preferred position
                if ((shelf.getPosition() == 0 && preferredPosition == PreferredPosition.Low) ||
                        (shelf.getPosition() == highShelfIndex && preferredPosition == PreferredPosition.High)) {
                    score += 100;
                } else if (preferredPosition != PreferredPosition.NoPreferred) { //
                    continue;
                }

                // Get all the plant types on the current shelf
                Set<PlantType> plantTypesOnShelf = getPlantTypesOnShelf(shelf);

                // If the shelf does NOT already contain the plant type which the batch has, increase score
                if (!plantTypesOnShelf.contains(plantType)) {
                    score += 50;
                }

                // Get all the harvest dates of batches on the current shelf
                Set<LocalDate> harvestDatesOnShelf = getHarvestDatesOnShelf(shelf);

                // If there is any batch with the same harvest date on the current shelf, increase score
                if (harvestDatesOnShelf.contains(batch.getHarvestingTask().getDueDate())) {
                    score += 25;
                }

                scoreList.add(new ScoreObj(shelf.getPosition(),  score, maxOnThisShelf, rack.getId()));
            }
        }

        scoreList.sort(Comparator.comparingInt(ScoreObj::getScore).reversed());

        Map<Integer, Map<Integer, Integer>> batchesOnRacks = new HashMap<>();

        int amountToBePlaced = batch.getAmount();

        // Create an iterator to easily iterate through the list
        Iterator<ScoreObj> scoreIterator = scoreList.iterator();

        // Loop while we still need to place some and while there are still more score objects in the iterator
        while (amountToBePlaced > 0 && scoreIterator.hasNext()) {
            ScoreObj currentScoreObj = scoreIterator.next(); // Get the current score object
            int rackId = currentScoreObj.getRackId();
            int position = currentScoreObj.getPosition();
            int amount = currentScoreObj.getAmount();

            // Get the minimum between amountToBePlaced and the amount that can be placed on this one
            int amountToBePlacedOnThisShelf = Math.min(amount, amountToBePlaced);

            // Check if the map contains the current score objects shelf's rack
            if (batchesOnRacks.containsKey(rackId)) {
                // Put a new key in the inner map, with the current score objects shelves position and the amount that can be placed on its shelf
                batchesOnRacks.get(rackId).put(position, amountToBePlacedOnThisShelf);
            } else {
                // Create a new key with the rack id, which has a new map, where the current score objects shelf position and amount that can be placed is input into
                batchesOnRacks.put(rackId, Map.ofEntries(
                        Map.entry(position, amountToBePlacedOnThisShelf)
                ));
            }

            // Reduce amount to be placed
            amountToBePlaced -= amountToBePlacedOnThisShelf;
        }

        return batchesOnRacks;
    }

    // Helper methods
    /**
     * Get all the harvest dates of batches on a shelf
     * @param shelf The shelf to get the harvest dates from
     * @return A set of all the harvest dates of batches on the shelf
     */
    private Set<LocalDate> getHarvestDatesOnShelf(Shelf shelf) {
        Set<LocalDate> harvestDatesOnShelf = new HashSet<>();
        for (BatchLocation batchLocation : shelf.getBatchLocations()) {
            harvestDatesOnShelf.add(batchLocation.getBatch().getHarvestingTask().getDueDate());
        }
        return harvestDatesOnShelf;
    }

    /**
     * Get all the plant types on a shelf
     * @param shelf The shelf to get the plant types from
     * @return A set of all the plant types on the shelf
     */
    private Set<PlantType> getPlantTypesOnShelf(Shelf shelf) {
        Set<PlantType> plantTypesOnShelf = new HashSet<>();
        for (BatchLocation batchLocation : shelf.getBatchLocations()) {
            plantTypesOnShelf.add(batchLocation.getBatch().getPlantType());
        }
        
        return plantTypesOnShelf;
    }

    /**
     * Check if a rack contains any batches
     * @param rack The rack to check
     * @return True if the rack contains any batches, false otherwise
     */
    private boolean rackContainsBatches(Rack rack) {
        for (Shelf shelf : rack.getShelves()) {
            if (!shelf.getBatchLocations().isEmpty()) {
                return true;
            }
        }
        return false;
    }
}
