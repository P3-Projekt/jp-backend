package com.dat3.jpgroentbackend.model.abstractions;

import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class AbstractTrayDetail extends DataType implements TrayDetail {

    @NotNull
    @Min(0)
    private final double length;

    @NotNull
    @Min(0)
    private final double width;

    protected AbstractTrayDetail(){
        super();
        length = 0;
        width = 0;
    }

    protected AbstractTrayDetail(String name, double length, double width) {
        super(name);
        this.length = length;
        this.width = width;
    }

    public double getLength() {
        return length;
    }

    public double getWidth() {
        return width;
    }
}
