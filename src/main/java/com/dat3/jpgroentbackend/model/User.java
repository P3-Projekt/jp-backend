package com.dat3.jpgroentbackend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

// Represents a user in the system
@Entity // Marks this class as an entity that maps to a database table
@Table(name = "\"user\"")
public class User {
    // Enum to define the role of a user in the system
    public enum Role{
        Gardener,
        Administrator,
        Manager
    }

    @Id
    private String name;
    private Role role;
    private boolean active = true;
    private String password;

    // Default constructor for storing in the database
    public User(){}

    /**
     * Constructor to create a User with the specified name, role, and password.
     * @param name     The name of the user.
     * @param role     The role of the user.
     * @param password The password of the user.
     */
    public User(String name, Role role, String password){
        this.name = name;
        this.role = role;
        this.password = password;
    }

    // Getters and setters
    public String getName() {
        return name;
    }

    public String getPassword() {return password;}

    public Role getRole() {
        return role;
    }
  
    public void setRole(Role role) {
        this.role = role;
    }

    public boolean isActive() {
        return active;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
