package com.dat3.jpgroentbackend;

import com.dat3.jpgroentbackend.controllers.RackController;
import com.dat3.jpgroentbackend.controllers.UserController;
import com.dat3.jpgroentbackend.controllers.dto.RackDto;
import com.dat3.jpgroentbackend.controllers.dto.request.CreateRackRequest;
import com.dat3.jpgroentbackend.controllers.dto.request.CreateUserRequest;
import com.dat3.jpgroentbackend.controllers.dto.response.RacksResponse;
import com.dat3.jpgroentbackend.model.Rack;
import com.dat3.jpgroentbackend.model.Shelf;
import com.dat3.jpgroentbackend.model.User;
import com.dat3.jpgroentbackend.model.repositories.BatchLocationRepository;
import com.dat3.jpgroentbackend.model.repositories.RackRepository;
import com.dat3.jpgroentbackend.model.repositories.ShelfRepository;
import com.dat3.jpgroentbackend.model.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UnitTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserController userController;

    @InjectMocks
    private RackController rackController;

    @Mock
    private RackRepository rackRepository;

    @Mock
    private ShelfRepository shelfRepository;

    @Mock
    private BatchLocationRepository batchLocationRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Order(1)
    @Test
    void testCreateUser_Success() {
        CreateUserRequest request = new CreateUserRequest();
        request.name = "admin";
        request.password = "password123";
        request.role = User.Role.Administrator;

        when(userRepository.existsById(request.name)).thenReturn(false);
        when(passwordEncoder.encode(request.password)).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User createdUser = userController.createUser(request);

        assertNotNull(createdUser);
        assertEquals("admin", createdUser.getName());
        assertEquals(User.Role.Administrator, createdUser.getRole());
        assertEquals("hashedPassword", createdUser.getPassword());

        verify(userRepository, times(1)).existsById(request.name);
        verify(passwordEncoder, times(1)).encode(request.password);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Order(2)
    @Test
    void testCreateUser_Conflict() {
        CreateUserRequest request = new CreateUserRequest();
        request.name = "admin";
        request.password = "password123";
        request.role = User.Role.Administrator;

        when(userRepository.existsById(request.name)).thenReturn(true);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> userController.createUser(request)
        );

        assertEquals("409 CONFLICT \"A User with name 'admin' already exists\"", exception.getMessage());

        verify(userRepository, times(1)).existsById(request.name);
        verifyNoInteractions(passwordEncoder);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testCreateRack_Success() {
        CreateRackRequest request = new CreateRackRequest();
        request.xCoordinate = 10;
        request.yCoordinate = 20;

        Rack mockRack = new Rack(request.xCoordinate, request.yCoordinate);
        when(rackRepository.save(any(Rack.class))).thenReturn(mockRack);

        RackDto createdRack = rackController.createRack(request);

        assertNotNull(createdRack);
        assertEquals(10, createdRack.position.getX());
        assertEquals(20, createdRack.position.getY());

        verify(rackRepository, times(1)).save(any(Rack.class));
    }

    @Test
    void testGetAllRacks_Success() {
        Rack rack1 = new Rack(10, 20);
        Rack rack2 = new Rack(30, 40);
        when(rackRepository.findAll()).thenReturn(List.of(rack1, rack2));

        List<RacksResponse> racks = rackController.getAllRacks();

        assertNotNull(racks);
        assertEquals(2, racks.size());
        assertEquals(10, racks.get(0).position.x);
        assertEquals(30, racks.get(1).position.x);

        verify(rackRepository, times(1)).findAll();
    }

    @Test
    void testUpdateRackPosition_Success() {
        CreateRackRequest request = new CreateRackRequest();
        request.xCoordinate = 15;
        request.yCoordinate = 25;

        Rack mockRack = new Rack(10, 20);
        when(rackRepository.findById(1)).thenReturn(Optional.of(mockRack));
        when(rackRepository.save(any(Rack.class))).thenReturn(mockRack);

        RackDto updatedRack = rackController.updateRackPosition(1, request);

        assertNotNull(updatedRack);
        assertEquals(15, updatedRack.position.getX());
        assertEquals(25, updatedRack.position.getY());

        verify(rackRepository, times(1)).findById(1);
        verify(rackRepository, times(1)).save(any(Rack.class));
    }

    @Test
    void testDeleteRack_Success() {
        Rack mockRack = new Rack(10, 20);
        when(rackRepository.findById(1)).thenReturn(Optional.of(mockRack));

        rackController.deleteRack(1);

        verify(rackRepository, times(1)).findById(1);
        verify(rackRepository, times(1)).deleteById(1);
    }

    @Test
    void testDeleteRack_Conflict() {
        Rack mockRack = mock(Rack.class);
        when(rackRepository.findById(1)).thenReturn(Optional.of(mockRack));
        when(mockRack.isEmpty()).thenReturn(false);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> rackController.deleteRack(1)
        );

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        verify(rackRepository, times(1)).findById(1);
        verify(rackRepository, never()).deleteById(anyInt());
    }

    @Test
    void testCreateShelf_Success() {
        Rack mockRack = new Rack(10, 20);
        when(rackRepository.findById(1)).thenReturn(Optional.of(mockRack));
        when(shelfRepository.findFirstByRackOrderByPositionDesc(mockRack))
                .thenReturn(Optional.empty());
        when(shelfRepository.save(any(Shelf.class))).thenAnswer(invocation -> invocation.getArgument(0));

        RacksResponse response = rackController.createShelf(1);

        assertNotNull(response);
        assertEquals(mockRack.getPosition().getX(), response.position.x);
        assertEquals(mockRack.getPosition().getY(), response.position.y);

        verify(rackRepository, times(1)).findById(1);
        verify(shelfRepository, times(1)).save(any(Shelf.class));
    }

}