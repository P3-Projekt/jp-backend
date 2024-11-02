package com.dat3.jpgroentbackend.model.repositories;

import com.dat3.jpgroentbackend.model.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, String> {}
