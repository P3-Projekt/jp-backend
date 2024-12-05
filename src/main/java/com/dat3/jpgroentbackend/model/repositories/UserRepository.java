package com.dat3.jpgroentbackend.model.repositories;

import com.dat3.jpgroentbackend.model.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing User entities.
 * Extends CrudRepository to provide basic CRUD operations.
 */
public interface UserRepository extends CrudRepository<User, String> {

    /**
     * Finds a user by name and active status.
     * @param name the name of the user.
     * @param active the active status of the user.
     * @return an Optional containing the User if found, or empty if not found.
     */
    Optional<User> findByNameAndActive(String name, boolean active);

    /**
     * Finds a user by name.
     * @param name the name of the user.
     * @return an Optional containing the User if found, or empty if not found.
     */
    Optional<User> findByName(String name);

    /**
     * Finds all users by their active status.
     * @param active the active status of the users.
     * @return a list of users with the specified active status.
     */
    List<User> findByActive(boolean active);
}
