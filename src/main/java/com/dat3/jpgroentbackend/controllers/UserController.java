package com.dat3.jpgroentbackend.controllers;

import com.dat3.jpgroentbackend.controllers.dto.request.CreateUserRequest;
import com.dat3.jpgroentbackend.model.User;
import com.dat3.jpgroentbackend.model.repositories.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@RestController
@Tag(name = "User")
public class UserController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/User")
    @Operation(
            summary = "Create new User"
    )
    public User createUser(
            @Valid
            @RequestBody CreateUserRequest request
            ) {
            //Check if name i not already used
            if(userRepository.existsById(request.name)){
                throw new ResponseStatusException(HttpStatus.CONFLICT ,"A User with name '" + request.name + "' already exists"); //IdAlreadyExistInDB(name);
            }
        User user = new User(request.name, request.role, passwordEncoder.encode(request.password));
            return userRepository.save(user);
    }

    @GetMapping("/Users")
    @Operation(
            summary = "List all Users",
            responses = {
                    @ApiResponse(content = {@Content(array = @ArraySchema(schema = @Schema(implementation = User.class)))})
            }
    )
    public Iterable<User> getAllUsers() {
        return userRepository.findAll();
    }

    @DeleteMapping("/Users/{name}")
    @Operation(
            summary = "Set a user inactive"
    )
    public void setUserInactive(
            @PathVariable String name
    ) {
        User user = userRepository.findByNameAndActive(name, true).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User with name '" + name + "' was not found"));
        user.setInactive();
        userRepository.save(user);
    }
}
