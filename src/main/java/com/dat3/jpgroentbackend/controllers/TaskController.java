package com.dat3.jpgroentbackend.controllers;

import com.dat3.jpgroentbackend.controllers.dto.request.CompleteTaskRequest;
import com.dat3.jpgroentbackend.controllers.dto.response.ListTasksResponse;
import com.dat3.jpgroentbackend.model.Batch;
import com.dat3.jpgroentbackend.model.BatchLocation;
import com.dat3.jpgroentbackend.model.Task;
import com.dat3.jpgroentbackend.model.User;
import com.dat3.jpgroentbackend.model.repositories.BatchLocationRepository;
import com.dat3.jpgroentbackend.model.repositories.BatchRepository;
import com.dat3.jpgroentbackend.model.repositories.TaskRepository;
import com.dat3.jpgroentbackend.model.repositories.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@Tag(name = "Task")
@Validated
public class TaskController {
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private BatchRepository batchRepository;
    @Autowired
    private BatchLocationRepository batchLocationRepository;
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/Tasks")
    @Operation(
            summary = "List all Tasks"
    )
    public List<ListTasksResponse.TaskResponse> getTasks(
            @RequestParam(required = false) @Parameter(description = "Optional week number to filter by") Integer weekNumber)
    {
        List<Batch> batches = new ArrayList<>();
        batchRepository.findAll().forEach(batches::add);

        Map<BatchLocation, List<Task>> tasksByLocation = Batch.getAllTasksByLocation(batches, weekNumber);

        //Response
        return new ListTasksResponse(tasksByLocation).getTaskResponses();
    }

    @PutMapping("/TaskAt/{batchLocationId}/Complete")
    @Operation(
            summary = "Completes the first task that can be completed for the batch location"
    )
    public void completeTaskAtBatchLocation(
            @PathVariable Integer batchLocationId,
            @RequestBody CompleteTaskRequest request
    ){
        BatchLocation batchLocation = batchLocationRepository.findById(batchLocationId).orElseThrow(
                ()->new ResponseStatusException(HttpStatus.NOT_FOUND, "BatchLocation with id '" + batchLocationId + "' was not found")
        );

        User user = userRepository.findById(request.getUsername()).orElseThrow(
                ()->new ResponseStatusException(HttpStatus.NOT_FOUND, "User with id '" + request.getUsername() + "' was not found")
        );

        Task nextTask = batchLocation.getNextTask();
        nextTask.complete(user);
        taskRepository.save(nextTask);
    }
}
