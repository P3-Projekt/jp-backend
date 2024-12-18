package com.dat3.jpgroentbackend;

import com.dat3.jpgroentbackend.model.Batch;
import com.dat3.jpgroentbackend.model.PlantType;
import com.dat3.jpgroentbackend.model.TrayType;
import com.dat3.jpgroentbackend.model.User;
import com.dat3.jpgroentbackend.model.repositories.BatchRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Iterator;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class BatchUnitTest {

    @Mock
    private BatchRepository batchRepository;

    @Mock
    private PlantType plantType;

    @Mock
    private TrayType trayType;

    @Mock
    private User createdBy;

    private Batch batch;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        plantType = new PlantType("Tomat", 3, 7, PlantType.PreferredPosition.Low, new int[]{2, 3, 1, 0});
        trayType = new TrayType("Stor", 15, 15);
        createdBy = new User("demo", User.Role.Administrator, "test123");

        batch = new Batch(100, plantType, trayType, createdBy);
    }


    @Test
    void testCreateBatch() {
        when(batchRepository.save(any(Batch.class))).thenReturn(batch);

        Batch savedBatch = batchRepository.save(batch);

        assertNotNull(savedBatch);
        assertEquals(100, savedBatch.getAmount());
        assertEquals(plantType, savedBatch.getPlantType());
        assertEquals(trayType, savedBatch.getTrayType());
        assertEquals(createdBy, savedBatch.getCreatedBy());

        verify(batchRepository, times(1)).save(any(Batch.class));
    }

    @Test
    void testGetBatchById() {
        // Når batchRepository.findById kaldes, returneres batch
        when(batchRepository.findById(1)).thenReturn(java.util.Optional.of(batch));

        Batch foundBatch = batchRepository.findById(1).get();

        assertNotNull(foundBatch);
        assertEquals(100, foundBatch.getAmount());
        assertEquals(plantType, foundBatch.getPlantType());
        assertEquals(trayType, foundBatch.getTrayType());
        assertEquals(createdBy, foundBatch.getCreatedBy());

        verify(batchRepository, times(1)).findById(1);
    }

    @Test
    void testGetAllBatches() {
        when(batchRepository.findAll()).thenReturn(java.util.List.of(batch));

        Iterable<Batch> batches = batchRepository.findAll();

        Iterator<Batch> batchIterator = batches.iterator();

        assertTrue(batchIterator.hasNext(), "Batch list should not be empty");

        Batch firstBatch = batchIterator.next();

        assertEquals(100, firstBatch.getAmount());
        assertEquals(plantType, firstBatch.getPlantType());
        assertEquals(trayType, firstBatch.getTrayType());
        assertEquals(createdBy, firstBatch.getCreatedBy());

        verify(batchRepository, times(1)).findAll();
    }

}
