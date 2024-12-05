package com.dat3.jpgroentbackend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

// Handles exceptions globally for all REST controllers.
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Handles validation errors and returns a formatted error message.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseStatusException handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        // Collect field errors and their messages
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }

        // Format the error messages into a single string
        String errorMessage = errors.entrySet().stream()
                .map(entry -> String.format("'%s': %s", entry.getKey(), entry.getValue()))
                .reduce((msg1, msg2) -> msg1 + ", " + msg2)
                .orElse("Validation failed");

        // Return a BAD_REQUEST response with the error details
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessage);
    }
}
