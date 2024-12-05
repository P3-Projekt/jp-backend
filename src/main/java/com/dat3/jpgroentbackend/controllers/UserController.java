package com.dat3.jpgroentbackend.controllers;

import com.dat3.jpgroentbackend.controllers.dto.request.CreateUserRequest;
import com.dat3.jpgroentbackend.controllers.dto.request.UpdateUserRequest;
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

// REST controller for managing user entities.
@RestController
@Tag(name = "User") // Adds a Swagger tag to group User-related endpoints in API documentation.
public class UserController {

    @Autowired
    private UserRepository userRepository; // Repository for accessing and managing User entities.

    @Autowired
    private PasswordEncoder passwordEncoder; // Encoder for securely storing passwords.


    /**
     * Creates a new user.
     * @param request The request body containing user details.
     * @return The newly created User entity.
     */
    @PostMapping("/User")
    @Operation(
            summary = "Create new User" // Describes the purpose of the endpoint in Swagger.
    )
    public User createUser(
            @Valid
            @RequestBody CreateUserRequest request // Validates the request body.
    ) {
            // Check if a user with the given username already exists.
            if(userRepository.existsById(request.getUsername())) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT ,
                        "A User with name '" + request.getUsername() + "' already exists"
                );
            }

        // Create a new User entity with encoded password.
        User user = new User(request.getUsername(), request.getRole(), passwordEncoder.encode(request.getPassword()));

        // Save the user to the repository and return the created entity.
        return userRepository.save(user);
    }

    /**
     * Retrieves all users.
     * @return An iterable list of all User entities.
     */
    @GetMapping("/Users")
    @Operation(
            summary = "List all Users", // Describes the purpose of the endpoint in Swagger.
            responses = {
                    @ApiResponse(content = {@Content(array = @ArraySchema(schema = @Schema(implementation = User.class)))})
            }
    )

    public Iterable<User> getAllUsers() {
        // Return all users from the repository.
        return userRepository.findAll();
    }

    /**
     * Marks a user as inactive.
     * @param name The username of the user to be marked inactive.
     */
    @DeleteMapping("/Users/{name}")
    @Operation(
            summary = "Set a user inactive" // Describes the purpose of the endpoint in Swagger.
    )
    public void setUserInactive(
            @PathVariable String name
    ) {
        // Find the active user by name; throw an exception if not found.
        User user = userRepository.findByNameAndActive(name, true)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User with name '" + name + "' was not found")
                );

        // Set the user's active status to false and save the updated entity.
        user.setInactive();
        userRepository.save(user);
    }

    /**
     * Reactivates a previously inactive user.
     * @param name The username of the user to be reactivated.
     */
    @PutMapping("/Users/{name}/activate")
    @Operation(
            summary = "Reactivate a user" // Describes the purpose of the endpoint in Swagger.
    )
    public void reactivateUser(
            @PathVariable String name
    ) {
        // Find the user by name; throw an exception if not found.
        User user = userRepository.findById(name)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User with name '" + name + "' was not found")
                );

        // If the user is already active, throw a BAD_REQUEST exception.
        if (user.isActive()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "User is already active"
            );
        }

        // Set the user's active status to true and save the updated entity.
        user.setActive(true);
        userRepository.save(user);
    }

    /**
     * Updates the details of an existing user.
     * @param name    The username of the user to be updated.
     * @param request The request body containing the updated user details.
     * @return The updated User entity.
     */
    @PutMapping("/Users/{name}")
    @Operation(
            summary = "Update user details" // Describes the purpose of the endpoint in Swagger.
    )
    public User updateUser(
            @PathVariable String name,
            @Valid @RequestBody UpdateUserRequest request
    ) {
        // Find the user by name; throw an exception if not found.
        User user = userRepository.findById(name)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User with name '" + name + "' was not found")
                );

        // Update the user's role if provided in the request.
        if (request.getRole() != null) {
            user.setRole(request.getRole());
        }

        // Save the updated user entity and return it.
        return userRepository.save(user);
    }

}