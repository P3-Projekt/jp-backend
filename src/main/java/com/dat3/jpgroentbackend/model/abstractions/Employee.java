package com.dat3.jpgroentbackend.model.abstractions;

import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Employee extends DataType {
    protected Employee(String name) {
        super(name);
    }

    public Employee() {
        super();
    }
}
