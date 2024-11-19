package com.dat3.jpgroentbackend.controllers;

import com.dat3.jpgroentbackend.controllers.dto.request.CompleteTaskRequest;
import com.dat3.jpgroentbackend.controllers.dto.response.TaskResponse;
import com.dat3.jpgroentbackend.model.Batch;
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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@RestController
@Tag(name = "Task")
@Validated
public class TaskController {
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private BatchRepository batchRepository;
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/Tasks")
    @Operation(
            summary = "List all Tasks"
    )
    public List<TaskResponse> getTasks(
            @RequestParam(required = false) @Parameter(description = "Optional week number to filter by") Integer weekNumber)
    {
        List<Batch> batches = new ArrayList<>();
        batchRepository.findAll().forEach(batches::add);

        List<Task> tasks = Batch.getTasks(batches, weekNumber);

        //Construct response
        return tasks.stream().map(TaskResponse::new).toList();
    }

    @PutMapping("/Task/{taskId}/Complete")
    @Operation(
            summary = "Completes the task with the given id"
    )
    public void completeTask(
            @PathVariable Integer taskId,
            @RequestBody CompleteTaskRequest request
    ){
        Task task = taskRepository.findById(taskId).orElseThrow(
                ()->new ResponseStatusException(HttpStatus.NOT_FOUND, "Task with id '" + taskId + "' was not found")
        );

        User user = userRepository.findById(request.getUsername()).orElseThrow(
                ()->new ResponseStatusException(HttpStatus.NOT_FOUND, "User with id '" + request.getUsername() + "' was not found")
        );
        task.complete(user);
        taskRepository.save(task);
    }
}
