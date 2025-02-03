package com.dat3.jpgroentbackend.model.entities;

import com.dat3.jpgroentbackend.model.abstractions.AbstractTrayDetail;
import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class TrayType extends AbstractTrayDetail {

    protected TrayType() {
        super();
    }

    public TrayType(String name, double length, double width){
        super(name, length, width);
    }
}
