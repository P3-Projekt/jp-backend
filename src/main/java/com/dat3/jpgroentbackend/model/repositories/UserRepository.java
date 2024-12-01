package com.dat3.jpgroentbackend.model.repositories;

import com.dat3.jpgroentbackend.model.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User, String> {

    Optional<User> findByNameAndActive(String name, boolean active);

    Optional<User> findByName(String name);
}

