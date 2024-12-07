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
    @Autowired
    private SecurityExpressionHandler securityExpressionHandler;


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

    private void printMap(Map<Integer, Integer> nestedMap) {
        System.out.println("Requested locations:");
        for (Map.Entry<Integer, Integer> entry :  nestedMap.entrySet()) {
            System.out.println(entry.getKey()+ ":" + entry.getValue());
        }
    }

    @PutMapping("/Batch/{batchId}/Position")
    @Operation(
            summary = "Update the position of a batch"
    )
        public void updateBatchPosition(
            @PathVariable int batchId,
            @Valid
            @RequestBody UpdateBatchLocationRequest request
        ){

        System.out.println("Updating batch positions");
        printMap(request.locations);

        // Check if the batch exists and get a batch object
        Batch batch = batchRepository.findById(batchId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "A batch with id '" + batchId + "' does not exist"));
        // Check if user exists
        User user = userRepository.findById(request.username).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "A batch with id '" + batchId + "' does not exist"));


        // Get amount from empty batch location and remove it
        if(batch.batchLocations.size() != 1 || batch.batchLocations.getFirst().shelf != null){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Batch already has locations");
        }
        BatchLocation emptyBatchLocation = batch.batchLocations.getFirst();
        int batchAmount = emptyBatchLocation.amount;
        System.out.printf("Batchamount: %d%n", batchAmount);

        // Check if location amounts are complete
        int locatedAmount = request.locations.values().stream().reduce(0, Integer::sum);
        if(batchAmount != locatedAmount) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Located amount was " + locatedAmount + " but batch contains " + batchAmount + " fields");


        // Create batchLocations with the values from the request body
        List<BatchLocation> batchLocations = new ArrayList<>();
        for (Map.Entry<Integer, Integer> location : request.locations.entrySet()) {
            int shelfId = location.getKey();
            int amount = location.getValue();
            Shelf shelf = shelfRepository.findById(shelfId).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "Shelf with id '" + shelfId + "' was not found"));
            System.out.printf("Putting amount '%d' of batch with id '%d' on shelf with id '%d' and position '%d' on rack with id '%d' rack id", amount, batchId, shelfId, shelf.getPosition(), shelf.getRack().getId());

            // Check if request wants to put more than possible on any of the shelves
            int maxAmountOfBatch = shelf.getMaxAmountOfBatch(batch);
            if (amount > maxAmountOfBatch) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Trying to place Batch with id '" + batch.getId() + "', but attempted to place " + amount + " units on shelf with max capacity " + maxAmountOfBatch);
            }
            batchLocations.add(new BatchLocation(batch, shelf, amount));
        }


        // Populate batch with newly created locations
        batch.batchLocations.clear();
        batch.batchLocations.addAll(batchLocations);
        //batch.
        batch.getPlantTask().complete(user);
        batchRepository.save(batch);
        }

    class ScoreObj {
        private final int shelfId;
        private final int score;
        private final int amount;
        
        private ScoreObj(int shelfId, int score, int amount) {
            this.shelfId = shelfId;
            this.score = score;
            this.amount = amount;
        }

        private int getScore() {
            return score;
        }

        private int getAmount() {
            return amount;
        }

        private int getShelfId() {
            return shelfId;
        }
    }

    @GetMapping("/Batch/{batchId}/Autolocate")
    @Operation(
            summary = "Calculate the optimal location(s) for a batch"
    )
    public Map<Integer, Integer> autolocateBatch(
            @PathVariable int batchId
    ) {
        System.out.println("Starting autolocate");
        // Check if the batch exists and get a batch object
        Batch batch = batchRepository.findById(batchId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "A batch with id '" + batchId + "' does not exist"));
        PlantType plantType = batch.getPlantType();
        PreferredPosition preferredPosition = plantType.getPreferredPosition();

        System.out.println("Got plant type and batch");
        
        // Throw an exception if the batch has already been placed
        if (batch.batchLocations.size() != 1 && batch.batchLocations.getFirst().shelf != null) {
            System.out.println("Batch is already placed");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Batch has already been placed");
        }

        System.out.println("creating scorelist");
        List<ScoreObj> scoreList = new ArrayList<>();
        System.out.println("Created score List");
        List<Rack> racks = rackRepository.findAll();
        System.out.println("Rack amount: " + racks.size());

        for (Rack rack : racks) {
            System.out.println("\nLooking at rack " + rack.getId());

            // If no shelves on this rack, continue to the next rack
            if (rack.getShelves() == null) {
                continue;
            }

            // Get a list of the maximum amount of this batch that can be on each shelf
            List<Integer> maxAmountOnShelves = rack.getMaxAmountOnShelves(batch);

            int highShelfIndex = rack.getShelves().size(); // Top position in the rack

            boolean rackContainsBatches = rackContainsBatches(rack); // True if the rack contains any batches at all

            // Go through all shelves in a rack, from the lowest one, as the positions are incremented each time; if we wanted to start from the top reversed() would be necessary
            for (Shelf shelf : rack.getShelves()) {

                // Get the max amount from this batch that can be placed on the current shelf, must be reversed as maxAmountOnShelves is also reversed
                int maxOnThisShelf = maxAmountOnShelves.reversed().get(shelf.getPosition() - 1);

                // If the shelf cant have any trays of the batch on it, continue
                if (maxOnThisShelf == 0) continue;

                // Sets the score to 10 or 0 depending on if the rack contains batches
                int score = rackContainsBatches ? 10 : 0;

                // Increases score if the plant type has a preferred position and the shelf matches the preferred position
                if ((shelf.getPosition() == 1 && preferredPosition == PreferredPosition.Low) ||
                        (shelf.getPosition() == highShelfIndex && preferredPosition == PreferredPosition.High)) {
                    score += 100;
                } else if (preferredPosition != PreferredPosition.NoPreferred) { // Otherwise if the preferredPosition is not NoPreferred
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

                scoreList.add(new ScoreObj(shelf.getId(), score, maxOnThisShelf));
            }
        }

        // Sort the score list by highest score, descending
        scoreList.sort(Comparator.comparingInt(ScoreObj::getScore).reversed());

        // The total amount of fields in the batch
        int amountToBePlaced = batch.getAmount();

        return getBatchesOnRack(scoreList, amountToBePlaced);
    }

    private Map<Integer, Integer> getBatchesOnRack(List<ScoreObj> scoreList, int amountToBePlaced) {
        // Create a new map to store the data in
        Map<Integer, Integer> batchesOnRacks = new HashMap<>();

        // Create an iterator to easily iterate through the list
        Iterator<ScoreObj> scoreIterator = scoreList.iterator();

        // Loop while we still need to place some and while there are still more score objects in the iterator
        while (amountToBePlaced > 0 && scoreIterator.hasNext()) {

            ScoreObj currentScoreObj = scoreIterator.next(); // Get the current score object
            int shelfId = currentScoreObj.getShelfId();
            int amount = currentScoreObj.getAmount();

            // Get the minimum between amountToBePlaced and the amount that can be placed on this one
            int amountToBePlacedOnThisShelf = Math.min(amount, amountToBePlaced);

            // Add a key value pair to the map
            batchesOnRacks.put(shelfId, amountToBePlacedOnThisShelf);

            // Reduce amount to be placed
            amountToBePlaced -= amountToBePlacedOnThisShelf;
        }
        return batchesOnRacks;
    }

    private Set<LocalDate> getHarvestDatesOnShelf(Shelf shelf) {
        Set<LocalDate> harvestDatesOnShelf = new HashSet<>();
        for (BatchLocation batchLocation : shelf.getBatchLocations()) {
            harvestDatesOnShelf.add(batchLocation.getBatch().getHarvestingTask().getDueDate());
        }
        return harvestDatesOnShelf;
    }
    
    private Set<PlantType> getPlantTypesOnShelf(Shelf shelf) {
        Set<PlantType> plantTypesOnShelf = new HashSet<>();
        for (BatchLocation batchLocation : shelf.getBatchLocations()) {
            plantTypesOnShelf.add(batchLocation.getBatch().getPlantType());
        }
        return plantTypesOnShelf;
    }

    private boolean rackContainsBatches(Rack rack) {
        for (Shelf shelf : rack.getShelves()) {
            if (!shelf.getBatchLocations().isEmpty()) {
                return true;
            }
        }
        return false;
    }

    private Rack getStartingRack () {
        int highestPercentage = 0;
        List<Rack> racks = rackRepository.findAll();
        Rack startingRack = racks.getFirst();
        for (Rack rack : racks) {
            int percentage = rack.getPercentageFilled();
            if (percentage > highestPercentage) {
                startingRack = rack;
                highestPercentage = percentage;
            }
        }
        return startingRack;
    }
}