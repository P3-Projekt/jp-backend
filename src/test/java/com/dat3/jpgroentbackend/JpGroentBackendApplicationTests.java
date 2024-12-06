package com.dat3.jpgroentbackend;

import com.dat3.jpgroentbackend.model.*;
import com.dat3.jpgroentbackend.model.repositories.UserRepository;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import static org.junit.jupiter.api.Assumptions.assumeTrue;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;

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

        authToken = mockMvc.perform(post("/api/auth/login")
                        .contentType("application/json")
                        .content("{\"username\": \"demo\", \"password\": \"12345\"}"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

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
    @Order(3)
    @Test
    void createRack() throws Exception {
        assumeTrue(authToken != null && !authToken.isEmpty(), "Authentication failed, skipping updateUserDetails test.");

        mockMvc.perform(post("/Rack")
                        .contentType("application/json")
                        .content("{\"xCoordinate\": 10, \"yCoordinate\": 10}")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.position.y").value(10))
                .andExpect(jsonPath("$.position.x").value(10))
                .andExpect(jsonPath("$.shelves").isArray())
                .andExpect(jsonPath("$.shelves").isEmpty());
    }

    @Order(4)
    @Test
    void createShelf() throws Exception {
        assumeTrue(authToken != null && !authToken.isEmpty(), "Authentication failed, skipping updateUserDetails test.");

        String rackId = "1";

        mockMvc.perform(post("/Rack/{rackId}/Shelf", rackId)
                        .contentType("application/json")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.position.y").value(10))
                .andExpect(jsonPath("$.position.x").value(10))
                .andExpect(jsonPath("$.shelves[0].id").value(1))
                .andExpect(jsonPath("$.shelves[0].batches").isArray())
                .andExpect(jsonPath("$.shelves[0].batches").isEmpty());
    }

    @Order(5)
    @Test
    void createPlantType() throws Exception {
        assumeTrue(authToken != null && !authToken.isEmpty(), "Authentication failed, skipping updateUserDetails test.");

        mockMvc.perform(post("/PlantType")
                        .contentType("application/json")
                        .content("""
                                {
                                  "name": "Solsikke",
                                  "preGerminationDays": 1,
                                  "growthTimeDays": 10,
                                  "preferredPosition": "Low",
                                  "wateringSchedule": [
                                    1,
                                    5,
                                    7
                                  ]
                                }""")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Solsikke"))
                .andExpect(jsonPath("$.preGerminationDays").value(1))
                .andExpect(jsonPath("$.growthTimeDays").value(10))
                .andExpect(jsonPath("$.preferredPosition").value("Low"))
                .andExpect(jsonPath("$.wateringSchedule").isArray())
                .andExpect(jsonPath("$.wateringSchedule[0]").value(1))
                .andExpect(jsonPath("$.wateringSchedule[1]").value(5))
                .andExpect(jsonPath("$.wateringSchedule[2]").value(7));
    }

    @Order(6)
    @Test
    void createTrayType() throws Exception {
        assumeTrue(authToken != null && !authToken.isEmpty(), "Authentication failed, skipping updateUserDetails test.");

        mockMvc.perform(post("/TrayType")
                        .contentType("application/json")
                        .content("""
                                {
                                  "name": "Stor",
                                  "widthCm": 15,
                                  "lengthCm": 15
                                }""")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Stor"))
                .andExpect(jsonPath("$.widthCm").value(15))
                .andExpect(jsonPath("$.lengthCm").value(15));
    }

    @Order(7)
    @Test
    void createBatch() throws Exception {
        assumeTrue(authToken != null && !authToken.isEmpty(), "Authentication failed, skipping updateUserDetails test.");

        mockMvc.perform(post("/Batch")
                        .contentType("application/json")
                        .content("""
                                {
                                  "plantTypeId": "Solsikke",
                                  "trayTypeId": "Stor",
                                  "createdByUsername": "admin",
                                  "amount": 10
                                }""")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.plantTypeId").value("Solsikke"))
                .andExpect(jsonPath("$.trayTypeId").value("Stor"))
                .andExpect(jsonPath("$.createdAt").value(Matchers.matchesPattern("^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}.*$")))
                .andExpect(jsonPath("$.createdBy").value("admin"));
        }

    // ALL GET REQUEST
    @Order(8)
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

    @Order(9)
    @Test
    void getTask() throws Exception {
        assumeTrue(authToken != null && !authToken.isEmpty(), "Authentication failed, skipping updateUserDetails test.");

    }

    @Order(10)
    @Test
    void getRacks() throws Exception {
        assumeTrue(authToken != null && !authToken.isEmpty(), "Authentication failed, skipping updateUserDetails test.");

    }

    @Order(11)
    @Test
    void getBatchesOnRack() throws Exception {
        assumeTrue(authToken != null && !authToken.isEmpty(), "Authentication failed, skipping updateUserDetails test.");
    }

    @Order(12)
    @Test
    void getPregerminatingBatches() throws Exception {
        assumeTrue(authToken != null && !authToken.isEmpty(), "Authentication failed, skipping updateUserDetails test.");
    }

    @Order(13)
    @Test
    void getMaxBatchAmountOnShelf() throws Exception {
        assumeTrue(authToken != null && !authToken.isEmpty(), "Authentication failed, skipping updateUserDetails test.");

        String batchId = "1";

        mockMvc.perform(get("/Batch/{batchId}/MaxAmountOnShelves", batchId)
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Stor"))
                .andExpect(jsonPath("$.1[0]").value(1600)) // Forventer at "1" indeholder 1600 i den første position
                .andExpect(jsonPath("$.2").isEmpty()); // Forventer at "2" er en tom liste
    }

    @Order(14)
    @Test
    void getTrayTypes() throws Exception {
        assumeTrue(authToken != null && !authToken.isEmpty(), "Authentication failed, skipping updateUserDetails test.");

        mockMvc.perform(get("/TrayTypes")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Stor"))
                .andExpect(jsonPath("$[0].widthCm").value(15))
                .andExpect(jsonPath("$[0].lengthCm").value(15));
    }

    @Order(15)
    @Test
    void getPlantTypes() throws Exception {
        assumeTrue(authToken != null && !authToken.isEmpty(), "Authentication failed, skipping updateUserDetails test.");

        mockMvc.perform(get("/PlantTypes")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Solsikke"))
                .andExpect(jsonPath("$[0].preGerminationDays").value(1))
                .andExpect(jsonPath("$[0].growthTimeDays").value(10))
                .andExpect(jsonPath("$[0].preferredPosition").value("Low"))
                .andExpect(jsonPath("$[0].wateringSchedule").isArray())
                .andExpect(jsonPath("$[0].wateringSchedule[0]").value(1))
                .andExpect(jsonPath("$[0].wateringSchedule[1]").value(5))
                .andExpect(jsonPath("$[0].wateringSchedule[2]").value(7));
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




    // TRAYTYPE

    @Test
    void deleteTrayType() throws Exception {
        assumeTrue(authToken != null && !authToken.isEmpty(), "Authentication failed, skipping updateUserDetails test.");
    }

    // PLANTTYPE

    @Test
    void deletePlantType() throws Exception {
        assumeTrue(authToken != null && !authToken.isEmpty(), "Authentication failed, skipping updateUserDetails test.");
    }

}
