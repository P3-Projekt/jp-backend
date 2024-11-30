package com.dat3.jpgroentbackend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class User {

    public enum Role{
        Gardener,
        Administrator,
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

    public void setActive(boolean active) {
        this.active = active;
    }

    // Add a method to set active (since you currently only have setInactive())
    public void setActive() {
        active = true;
    }
}
