package com.dat3.jpgroentbackend.model.repositories.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class IntArrayToJsonConverter implements AttributeConverter<int[], String> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(int[] attribute) {
        try{
            return objectMapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Error converting array JSON", e);
        }
    }

    @Override
    public int[] convertToEntityAttribute(String dbData) {
        System.out.println(dbData);
        try {
            return objectMapper.readValue(dbData, int[].class);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Error converting JSON to array", e);
        }
    }
}
