package com.dat3.jpgroentbackend.model.abstractions;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class DataType {

    @Id
    protected String name;

    protected boolean active = true;

    protected DataType() {
        name = null;
    }

    protected DataType(String name) {
        this.name = name;
    }

    public void setActive(boolean newStatus) throws IllegalArgumentException {
        if (newStatus == active) throw new IllegalArgumentException("Active already set to " + active);
        active = newStatus;
    }

    public String getId() {
        return name;
    }
}
