package com.dat3.jpgroentbackend.model.repositories.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

// Converts int[] to JSON for database storage and vice versa.
@Converter(autoApply = true)
public class IntArrayToJsonConverter implements AttributeConverter<int[], String> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    // Converts an int array to a JSON string for storage.
    @Override
    public String convertToDatabaseColumn(int[] attribute) {
        try{
            return objectMapper.writeValueAsString(attribute); // Serialize array to JSON
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Error converting array JSON", e);
        }
    }

    // Converts a JSON string from the database back to an int array.
    @Override
    public int[] convertToEntityAttribute(String dbData) {
        //System.out.println(dbData);
        try {
            return objectMapper.readValue(dbData, int[].class); // Deserialize JSON to array
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Error converting JSON to array", e);
        }
    }
}
