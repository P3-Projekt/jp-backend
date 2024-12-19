package com.dat3.jpgroentbackend;

import com.dat3.jpgroentbackend.model.PlantType;
import com.dat3.jpgroentbackend.model.repositories.PlantTypeRepository;
import com.dat3.jpgroentbackend.model.PlantType.PreferredPosition; // Hvis PreferredPosition er en enum, importeres den her
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class PlantTypeUnitTest {

    @Mock
    private PlantTypeRepository plantTypeRepository;

    private PlantType plantType;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        PreferredPosition preferredPosition = PreferredPosition.Low;

        int[] wateringSchedule = {3, 5, 7};

        plantType = new PlantType("Tomat", 10, 30, preferredPosition, wateringSchedule);
    }

    @Test
    void testCreatePlantType() {
        when(plantTypeRepository.save(any(PlantType.class))).thenReturn(plantType);

        PlantType savedPlantType = plantTypeRepository.save(plantType);

        assertNotNull(savedPlantType);
        assertEquals("Tomat", savedPlantType.getName());
        assertEquals(10, savedPlantType.getPreGerminationDays());
        assertEquals(30, savedPlantType.getGrowthTimeDays());
        assertEquals(PreferredPosition.Low, savedPlantType.getPreferredPosition());
        assertArrayEquals(new int[]{3, 5, 7}, savedPlantType.getWateringSchedule());

        verify(plantTypeRepository, times(1)).save(any(PlantType.class));
    }

    @Test
    void testGetPlantTypeById() {
        when(plantTypeRepository.findById("Tomat")).thenReturn(java.util.Optional.of(plantType));

        PlantType foundPlantType = plantTypeRepository.findById("Tomat").get();

        assertNotNull(foundPlantType);
        assertEquals("Tomat", foundPlantType.getName());
        assertEquals(10, foundPlantType.getPreGerminationDays());
        assertEquals(30, foundPlantType.getGrowthTimeDays());
        assertEquals(PreferredPosition.Low, foundPlantType.getPreferredPosition());
        assertArrayEquals(new int[]{3, 5, 7}, foundPlantType.getWateringSchedule());

        verify(plantTypeRepository, times(1)).findById("Tomat");
    }

    @Test
    void testGetAllPlantTypes() {
        when(plantTypeRepository.findAll()).thenReturn(java.util.List.of(plantType));

        var plantTypes = plantTypeRepository.findAll();

        assertNotNull(plantTypes);

        var firstPlantType = plantTypes.iterator().next();

        assertEquals("Tomat", firstPlantType.getName());
        assertEquals(10, firstPlantType.getPreGerminationDays());
        assertEquals(30, firstPlantType.getGrowthTimeDays());
        assertEquals(PreferredPosition.Low, firstPlantType.getPreferredPosition());
        assertArrayEquals(new int[]{3, 5, 7}, firstPlantType.getWateringSchedule());

        verify(plantTypeRepository, times(1)).findAll();
    }
}
