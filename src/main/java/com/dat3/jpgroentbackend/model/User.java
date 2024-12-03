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
    private String telephone;
    private String email;
    private String address;

    public User(){}

    public User(String name, Role role, String telephone, String email, String address){
        this.name = name;
        this.role = role;
        this.telephone = telephone;
        this.email = email;
        this.address = address;
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

    public void setActive() {
        active = true;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}