package com.dat3.jpgroentbackend;

import com.dat3.jpgroentbackend.model.TrayType;
import com.dat3.jpgroentbackend.model.repositories.TrayTypeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class TrayTypeUnitTest {

    @Mock
    private TrayTypeRepository trayTypeRepository;

    private TrayType trayType;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        trayType = new TrayType("Stor", 15, 15);  // Initialisering af TrayType objektet
    }

    @Test
    void testCreateTrayType() {
        // Mocking af save metode, så den returnerer trayType
        when(trayTypeRepository.save(any(TrayType.class))).thenReturn(trayType);

        // Kalder save direkte på repository
        TrayType savedTrayType = trayTypeRepository.save(trayType);

        // Verificerer resultater
        assertNotNull(savedTrayType);
        assertEquals("Stor", savedTrayType.getName());
        assertEquals(15, savedTrayType.getWidthCm());
        assertEquals(15, savedTrayType.getLengthCm());

        // Verificerer at save blev kaldt én gang
        verify(trayTypeRepository, times(1)).save(any(TrayType.class));
    }

    @Test
    void testGetTrayTypeById() {
        // Mocking af findById metode til at returnere trayType
        when(trayTypeRepository.findById("Stor")).thenReturn(java.util.Optional.of(trayType));

        // Kalder findById direkte på repository
        TrayType foundTrayType = trayTypeRepository.findById("Stor").orElse(null);

        // Verificerer resultater
        assertNotNull(foundTrayType);
        assertEquals("Stor", foundTrayType.getName());
        assertEquals(15, foundTrayType.getWidthCm());
        assertEquals(15, foundTrayType.getLengthCm());

        // Verificerer at findById blev kaldt én gang
        verify(trayTypeRepository, times(1)).findById("Stor");
    }

    @Test
    void testGetAllTrayTypes() {
        when(trayTypeRepository.findAll()).thenReturn(java.util.List.of(trayType));

        var trayTypes = trayTypeRepository.findAll();

        assertNotNull(trayTypes);

        var firstTrayType = trayTypes.iterator().next();

        assertEquals("Stor", firstTrayType.getName());
        assertEquals(15, firstTrayType.getWidthCm());
        assertEquals(15, firstTrayType.getLengthCm());

        verify(trayTypeRepository, times(1)).findAll();
    }
}
