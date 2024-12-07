package com.dat3.jpgroentbackend.exceptions;

public class AreaExceededException extends RuntimeException {
    public AreaExceededException(String message) {
        super(message);
    }
}
