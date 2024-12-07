package com.dat3.jpgroentbackend.controllers;

import com.dat3.jpgroentbackend.controllers.dto.request.CompleteTaskRequest;
import com.dat3.jpgroentbackend.controllers.dto.response.TaskResponse;
import com.dat3.jpgroentbackend.model.Batch;
import com.dat3.jpgroentbackend.model.Task;
import com.dat3.jpgroentbackend.model.User;
import com.dat3.jpgroentbackend.model.repositories.BatchRepository;
import com.dat3.jpgroentbackend.model.repositories.TaskRepository;
import com.dat3.jpgroentbackend.model.repositories.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

//REST controller for handling Task-related operations.
@RestController
@Tag(name = "Task") // Adds a Swagger tag for grouping Task-related endpoints in API documentation
@Validated // Enables validation of request parameters and request bodies
public class TaskController {

    @Autowired
    private TaskRepository taskRepository; // Repository for managing Task entities

    @Autowired
    private BatchRepository batchRepository; // Repository for managing Batch entities

    @Autowired
    private UserRepository userRepository; // Repository for managing User entities

    /**
     * Retrieves a list of tasks.
     * Optionally filters tasks by a specific week number.
     * @param weekNumber Optional query parameter for filtering tasks by week.
     * @return A list of TaskResponse objects representing the tasks.
     */
    @GetMapping("/Tasks")
    @Operation(
            summary = "List all Tasks"
    )
    public List<TaskResponse> getTasks(
            @RequestParam(required = false) @Parameter(description = "Optional week number to filter by") Integer weekNumber)
    {
        // Fetch all batches from the repository
        List<Batch> batches = new ArrayList<>();
        batchRepository.findAll().forEach(batches::add);

        // Retrieve tasks from batches, optionally filtered by the provided week number
        List<Task> tasks = Batch.getTasks(batches, weekNumber);

        // Map tasks to response DTOs and return the list
        return tasks.stream().map(TaskResponse::new).toList();
    }

    /**
     * Marks a task as completed.
     * @param taskId  The ID of the task to complete.
     * @param request Request body containing the username of the user completing the task.
     */
    @PutMapping("/Task/{taskId}/Complete")
    @Operation(
            summary = "Completes the task with the given id"
    )
    public void completeTask(
            @PathVariable Integer taskId,
            @RequestBody CompleteTaskRequest request
    ){
        // Retrieve the task by its ID or throw a NOT_FOUND exception if it doesn't exist
        Task task = taskRepository.findById(taskId).orElseThrow(
                ()->new ResponseStatusException(HttpStatus.NOT_FOUND, "Task with id '" + taskId + "' was not found")
        );

        // Retrieve the user by their username or throw a NOT_FOUND exception if they don't exist
        User user = userRepository.findById(request.username).orElseThrow(
                ()->new ResponseStatusException(HttpStatus.NOT_FOUND, "User with id '" + request.username + "' was not found")
        );
        // Mark the task as completed by the user
        task.complete(user);

        // Save the updated task in the repository
        taskRepository.save(task);
    }
}
