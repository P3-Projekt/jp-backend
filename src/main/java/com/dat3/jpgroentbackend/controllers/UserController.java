package com.dat3.jpgroentbackend.controllers;

import com.dat3.jpgroentbackend.model.User;
import com.dat3.jpgroentbackend.model.repositories.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@Tag(name = "User")
@Validated
public class UserController {
    @Autowired
    private UserRepository userRepository;

    @PostMapping("/User")
    @Operation(
            summary = "Create new User"
    )
    public User createUser(
            @RequestParam String name,
            @RequestParam User.Role role
            ) {

            //Check if name i not already used
            if(userRepository.existsById(name)){
                throw new ResponseStatusException(HttpStatus.CONFLICT ,"A User with name '" + name + "' already exists"); //IdAlreadyExistInDB(name);
            }
            User user = new User(name, role);
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
}
