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
    private String name;
    private Role role;
    private boolean active = true;

    public User(){}
    public User(String name, Role role){
        this.name = name;
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public Role getRole() {
        return role;
    }

    public boolean isActive() {
        return active;
    }

    public void setInactive() {
        active = false;
    }
}
