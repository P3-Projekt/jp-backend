package com.dat3.jpgroentbackend.controllers.dto;

import com.dat3.jpgroentbackend.model.User;

public class UserDto {

    public final String name;
    public final User.Role role;
    public final Boolean active;


    public UserDto(User user){
        this.active = user.isActive();
        this.role = user.getRole();
        this.name = user.getName();
    }
}
