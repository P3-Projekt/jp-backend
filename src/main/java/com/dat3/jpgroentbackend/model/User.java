package com.dat3.jpgroentbackend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class User {

    public enum Role{
        Gardener,
        Admin,
        Manager
    }

    @Id
    public String name;
    public Role role;

    public User(){}
    public User(String name, Role role){
        this.name = name;
        this.role = role;
    }
}
