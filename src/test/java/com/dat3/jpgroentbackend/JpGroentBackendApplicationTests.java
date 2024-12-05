package com.dat3.jpgroentbackend;

import com.dat3.jpgroentbackend.model.*;
import com.dat3.jpgroentbackend.model.repositories.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import static org.junit.jupiter.api.Assumptions.assumeTrue;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;


import java.lang.reflect.Array;
import java.util.ArrayList;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class JpGroentBackendApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    private String authToken;

    @BeforeEach
    void setUp() {
        User demoUser = new User();
        demoUser.setName("demo");
        demoUser.setPassword(passwordEncoder.encode("12345")); // Use a simple password for testing
        demoUser.setRole(User.Role.Administrator); // Set the role as needed
        demoUser.setActive(true); // Set the user as active

        userRepository.save(demoUser);

        // Create demoRack
        Rack demoRack = new Rack();
        demoRack.setPosition(1, 1);

        // Create demoShelf
        Shelf demoShelf = new Shelf();
        demoRack.addShelf(demoShelf);

        // Create Traytype & Planttype
        TrayType demoTrayType = new TrayType("lille", 10, 10);

        int[] waterSchedule = {3,3,3,3};
        PlantType demoPlantType = new PlantType("Solksikkeskud", 3, 3, PlantType.PreferredPosition.Low, waterSchedule);

        // Create Batch
        Batch demoBatch = new Batch(5, demoPlantType, demoTrayType, demoUser);

        // Create a BatchLocation
        BatchLocation demoBatchLocation = new BatchLocation(demoBatch, demoShelf, 5);

        demoShelf.addBatchLocation(demoBatchLocation);
    }

    @Order(1)
    @Test
    void userAuth() throws Exception {
        String responseBody = mockMvc.perform(post("/api/auth/login")
                        .contentType("application/json")
                        .content("{\"username\": \"demo\", \"password\": \"12345\"}"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        authToken = responseBody;

        // Assume token exists for further tests to run
        assumeTrue(!authToken.isEmpty(), "Auth token not received. Skipping further tests.");
    }


    //ALL POST REQUEST
    @Order(2)
    @Test
    void createUser() throws Exception {
        assumeTrue(authToken != null && !authToken.isEmpty(), "Authentication failed, skipping updateUserDetails test.");

        mockMvc.perform(post("/User")
                .contentType("application/json")
                .content("{\"name\": \"admin\", \"role\": \"Administrator\", \"password\": \"12345\"}")
                .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("admin"))
                .andExpect(jsonPath("$.role").value("Administrator"))
                .andExpect(jsonPath("$.active").value(true))
                .andExpect(jsonPath("$.password").isString());
    }

    // ALL GET REQUEST
    @Test
    void getUsers() throws Exception { // done
        assumeTrue(authToken != null && !authToken.isEmpty(), "Authentication failed, skipping updateUserDetails test.");

        mockMvc.perform(get("/Users")
                .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("demo"))
                .andExpect(jsonPath("$[0].role").value("Administrator"))
                .andExpect(jsonPath("$[0].active").value(true))
                .andExpect(jsonPath("$[0].password").isString());
    }

    @Test
    void getTask() throws Exception {
        assumeTrue(authToken != null && !authToken.isEmpty(), "Authentication failed, skipping updateUserDetails test.");

    }

    @Test
    void getRacks() throws Exception {
        assumeTrue(authToken != null && !authToken.isEmpty(), "Authentication failed, skipping updateUserDetails test.");

    }

    @Test
    void getBatchesOnRack() throws Exception {
        assumeTrue(authToken != null && !authToken.isEmpty(), "Authentication failed, skipping updateUserDetails test.");
    }

    @Test
    void getPregerminatingBatches() throws Exception {
        assumeTrue(authToken != null && !authToken.isEmpty(), "Authentication failed, skipping updateUserDetails test.");
    }

    @Test
    void getMaxBatchAmountOnShelf() throws Exception {
        assumeTrue(authToken != null && !authToken.isEmpty(), "Authentication failed, skipping updateUserDetails test.");
    }

    @Test
    void getTrayTypes() throws Exception {
        assumeTrue(authToken != null && !authToken.isEmpty(), "Authentication failed, skipping updateUserDetails test.");
    }

    @Test
    void getPlantTypes() throws Exception {
        assumeTrue(authToken != null && !authToken.isEmpty(), "Authentication failed, skipping updateUserDetails test.");
    }

























    // USER!
    @Test
    void updateUserDetails() throws Exception {
        assumeTrue(authToken != null && !authToken.isEmpty(), "Authentication failed, skipping updateUserDetails test.");

    }

    @Test
    void deleteUser() throws Exception {
        assumeTrue(authToken != null && !authToken.isEmpty(), "Authentication failed, skipping updateUserDetails test.");
    }

    @Test
    void makeUserActive() throws Exception {
        assumeTrue(authToken != null && !authToken.isEmpty(), "Authentication failed, skipping updateUserDetails test.");
    }

    // TASK
    @Test
    void completeTask() throws Exception {
        assumeTrue(authToken != null && !authToken.isEmpty(), "Authentication failed, skipping updateUserDetails test.");

    }



    // RACK
    @Test
    void updateRackPos() throws Exception {
        assumeTrue(authToken != null && !authToken.isEmpty(), "Authentication failed, skipping updateUserDetails test.");

    }

    @Test
    void createRack() throws Exception {
        assumeTrue(authToken != null && !authToken.isEmpty(), "Authentication failed, skipping updateUserDetails test.");

    }

    @Test
    void createShelf() throws Exception {
        assumeTrue(authToken != null && !authToken.isEmpty(), "Authentication failed, skipping updateUserDetails test.");
    }

    @Test
    void deleteShelf() throws Exception {
        assumeTrue(authToken != null && !authToken.isEmpty(), "Authentication failed, skipping updateUserDetails test.");
    }


    @Test
    void deleteRack() throws Exception {
        assumeTrue(authToken != null && !authToken.isEmpty(), "Authentication failed, skipping updateUserDetails test.");
    }

    // BATCH!
    @Test
    void updateBatchPos() throws Exception {
        assumeTrue(authToken != null && !authToken.isEmpty(), "Authentication failed, skipping updateUserDetails test.");
    }

    @Test
    void createBatch() throws Exception {
        assumeTrue(authToken != null && !authToken.isEmpty(), "Authentication failed, skipping updateUserDetails test.");
    }


    // TRAYTYPE
    @Test
    void createTrayType() throws Exception {
        assumeTrue(authToken != null && !authToken.isEmpty(), "Authentication failed, skipping updateUserDetails test.");
    }

    @Test
    void deleteTrayType() throws Exception {
        assumeTrue(authToken != null && !authToken.isEmpty(), "Authentication failed, skipping updateUserDetails test.");
    }

    // PLANTTYPE
    @Test
    void createPlantType() throws Exception {
        assumeTrue(authToken != null && !authToken.isEmpty(), "Authentication failed, skipping updateUserDetails test.");
    }

    @Test
    void deletePlantType() throws Exception {
        assumeTrue(authToken != null && !authToken.isEmpty(), "Authentication failed, skipping updateUserDetails test.");
    }

}
