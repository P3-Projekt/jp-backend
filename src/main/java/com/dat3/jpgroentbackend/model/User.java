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
    private String password;

    public User(){}
    public User(String name, Role role, String password){
        this.name = name;
        this.role = role;
        this.password = password;
    }

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

    public void setInactive() {
        active = false;
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

    public void setActive() {
        active = true;
    }


}
