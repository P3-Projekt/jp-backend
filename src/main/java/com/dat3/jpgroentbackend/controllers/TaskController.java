package com.dat3.jpgroentbackend.controllers;

import com.dat3.jpgroentbackend.controllers.dto.TaskDto;
import com.dat3.jpgroentbackend.model.PlantType;
import com.dat3.jpgroentbackend.model.Task;
import com.dat3.jpgroentbackend.model.repositories.TaskRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@Tag(name = "Task")
@Validated
public class TaskController {
    @Autowired
    private TaskRepository taskRepository;

    @GetMapping("/Tasks")
    @Operation(
            summary = "List all Tasks",
            responses = {
                    @ApiResponse(content = {@Content(array = @ArraySchema(schema = @Schema(implementation = TaskDto.class)))})
            }
    )

    public List<TaskDto> getTasks() {
        ArrayList<TaskDto> taskDtos = new ArrayList<>();
        taskRepository.findAll().forEach(task -> taskDtos.add(new TaskDto(task)));

        return taskDtos;
    }
}
