package com.dat3.jpgroentbackend;

import com.dat3.jpgroentbackend.model.*;
import com.dat3.jpgroentbackend.model.repositories.UserRepository;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import static org.junit.jupiter.api.Assumptions.assumeTrue;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class EndpointTest {

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
        demoUser.setName("Jens");
        demoUser.setPassword(passwordEncoder.encode("Jens9876")); // Use a simple password for testing
        demoUser.setRole(User.Role.Administrator); // Set the role as needed
        demoUser.setActive(true); // Set the user as active

        userRepository.save(demoUser);
    }

    @Order(1)
    @Test
    void userAuth() throws Exception {

        authToken = mockMvc.perform(post("/api/auth/login")
                        .contentType("application/json")
                        .content("{\"username\": \"Jens\", \"password\": \"Jens9876\"}"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Assume token exists for further tests to run
        assumeTrue(!authToken.isEmpty(), "Auth token not received. Skipping further tests.");
    }

    @Order(2)
    @Test
    void createUser() throws Exception {
        assumeTrue(authToken != null && !authToken.isEmpty(), "Authentication failed, skipping createUser test.");

        mockMvc.perform(post("/User")
                .contentType("application/json")
                .content("{\"name\": \"admin\", \"role\": \"Administrator\", \"password\": \"12345\"}")
                .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("admin"))
                .andExpect(jsonPath("$.role").value("Administrator"))
                .andExpect(jsonPath("$.active").value(true));
    }
    @Order(3)
    @Test
    void createRack() throws Exception {
        assumeTrue(authToken != null && !authToken.isEmpty(), "Authentication failed, skipping createRack test.");

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
        assumeTrue(authToken != null && !authToken.isEmpty(), "Authentication failed, skipping createShelf test.");

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
        assumeTrue(authToken != null && !authToken.isEmpty(), "Authentication failed, skipping createPlantType test.");

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
        assumeTrue(authToken != null && !authToken.isEmpty(), "Authentication failed, skipping createTrayType test.");

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
        assumeTrue(authToken != null && !authToken.isEmpty(), "Authentication failed, skipping createBatch test.");

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

    @Order(8)
    @Test
    void getUsers() throws Exception { // done
        assumeTrue(authToken != null && !authToken.isEmpty(), "Authentication failed, skipping getUsers test.");

        mockMvc.perform(get("/Users")
                .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Jens"))
                .andExpect(jsonPath("$[0].role").value("Administrator"))
                .andExpect(jsonPath("$[0].active").value(true));
    }

    @Order(9)
    @Test
    void getTask() throws Exception {
        assumeTrue(authToken != null && !authToken.isEmpty(), "Authentication failed, skipping getTask test.");


        mockMvc.perform(get("/Tasks")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].batchId").value(1))
                .andExpect(jsonPath("$[0].taskId").value(2))
                .andExpect(jsonPath("$[0].plantType").value("Solsikke"))
                .andExpect(jsonPath("$[0].fields").value(10))
                .andExpect(jsonPath("$[0].dueDate").value(Matchers.matchesPattern("^\\d{4}-\\d{2}-\\d{2}")))
                .andExpect(jsonPath("$[0].category").value("Plant"))
                .andExpect(jsonPath("$[0].completedAt").doesNotExist())
                .andExpect(jsonPath("$[0].completedBy").doesNotExist());
    }

    @Order(10)
    @Test
    void getRacks() throws Exception {
        assumeTrue(authToken != null && !authToken.isEmpty(), "Authentication failed, skipping getRacks test.");

        mockMvc.perform(get("/Racks") // Endpoint for at hente racks
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].position.x").value(10))
                .andExpect(jsonPath("$[0].position.y").value(10))
                .andExpect(jsonPath("$[0].shelves").isArray())
                .andExpect(jsonPath("$[0].shelves[0].id").value(1))
                .andExpect(jsonPath("$[0].shelves[0].batches").isArray())
                .andExpect(jsonPath("$[0].shelves[0].batches").isEmpty());
    }

    @Order(11)
    @Test
    void PreGerminatingBatchesBefore() throws Exception {
        assumeTrue(authToken != null && !authToken.isEmpty(), "Authentication failed, skipping PreGerminatingBatches test.");

        mockMvc.perform(get("/PreGerminatingBatches")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.needsMorePreGermination[0].batchId").value(1))
                .andExpect(jsonPath("$.needsMorePreGermination[0].amount").value(10))
                .andExpect(jsonPath("$.needsMorePreGermination[0].plantName").value("Solsikke"))
                .andExpect(jsonPath("$.needsMorePreGermination[0].dueDate").value("2024-12-19"))
                .andExpect(jsonPath("$.canBePlaced").isEmpty());
    }

    @Order(12)
    @Test
    void updateBatchPos() throws Exception {
        assumeTrue(authToken != null && !authToken.isEmpty(), "Authentication failed, skipping updateBatchPos test.");

        int batchId = 1;
        String requestBody = """
        {
            "locations": {
                "1": 10
            },
            "username": "admin"
        }
        """;

        mockMvc.perform(put("/Batch/{batchId}/Position", batchId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + authToken)
                        .content(requestBody))
                .andExpect(status().isOk());
    }

    @Order(13)
    @Test
    void PreGerminatingBatchesAfter() throws Exception {
        assumeTrue(authToken != null && !authToken.isEmpty(), "Authentication failed, skipping PreGerminatingBatches test.");

        mockMvc.perform(get("/PreGerminatingBatches")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.canBePlaced").isEmpty())
                .andExpect(jsonPath("$.needsMorePreGermination").isEmpty());
    }


    @Order(14)
    @Test
    void getMaxBatchAmountOnShelf() throws Exception {
        assumeTrue(authToken != null && !authToken.isEmpty(), "Authentication failed, skipping getMaxBatchAmountOnShelf test.");

        String batchId = "1";

        mockMvc.perform(get("/Batch/{batchId}/MaxAmountOnShelves", batchId)
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.['1'][0]").value(25));
    }

    @Order(15)
    @Test
    void getTrayTypes() throws Exception {
        assumeTrue(authToken != null && !authToken.isEmpty(), "Authentication failed, skipping getTrayTypes test.");

        mockMvc.perform(get("/TrayTypes")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Stor"))
                .andExpect(jsonPath("$[0].widthCm").value(15))
                .andExpect(jsonPath("$[0].lengthCm").value(15));
    }

    @Order(16)
    @Test
    void getPlantTypes() throws Exception {
        assumeTrue(authToken != null && !authToken.isEmpty(), "Authentication failed, skipping getPlantTypes test.");

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

    @Order(17)
    @Test
    void setUserAsInactive() throws Exception {
        assumeTrue(authToken != null && !authToken.isEmpty(), "Authentication failed, skipping deleteUser test.");

        String name = "admin";
        mockMvc.perform(put("/Users/{name}", name)
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk());
    }

    @Order(18)
    @Test
    void setUserAsActive() throws Exception {
        assumeTrue(authToken != null && !authToken.isEmpty(), "Authentication failed, skipping makeUserActive test.");

        String name = "admin";
        mockMvc.perform(put("/Users/{name}/activate",name)
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk());
    }

    @Order(19)
    @Test
    void completeTask() throws Exception {
        assumeTrue(authToken != null && !authToken.isEmpty(), "Authentication failed, skipping completeTask test.");

        String taskId = "1";

        mockMvc.perform(put("/Task/{taskId}/Complete", taskId)
                        .contentType("application/json")
                        .content("""
                            {
                              "username": "admin"
                            }""")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk());
    }



    @Order(20)
    @Test
    void updateRackPos() throws Exception {
        assumeTrue(authToken != null && !authToken.isEmpty(), "Authentication failed, skipping updateRackPos test.");

        mockMvc.perform(put("/Rack/1/Position")
                        .contentType("application/json")
                        .content("""
                            {
                              "xCoordinate": 20,
                              "yCoordinate": 20
                            }""")
                        .header("Authorization", "Bearer " + authToken))

                .andExpect(jsonPath("$.position.x").value(20))
                .andExpect(jsonPath("$.position.y").value(20))
        .andExpect(status().isOk());
    }

    @Order(21)
    @Test
    void deleteShelf() throws Exception {
        assumeTrue(authToken != null && !authToken.isEmpty(), "Authentication failed, skipping deleteShelf test.");

        String rackId = "1";

        mockMvc.perform(delete("/Rack/{rackId}/Shelf", rackId)
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk());

    }

    @Order(22)
    @Test
    void deleteRack() throws Exception {
        assumeTrue(authToken != null && !authToken.isEmpty(), "Authentication failed, skipping deleteRack test.");

        int rackId = 1;
        mockMvc.perform(delete("/Rack/{rackId}", rackId)
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk());
    }

    @Order(23)
    @Test
    void setTrayTypeAsInactive() throws Exception {
        assumeTrue(authToken != null && !authToken.isEmpty(), "Authentication failed, skipping setTrayTypeAsInactive test.");

        String name = "Stor";
        mockMvc.perform(put("/TrayType/{name}/inactivate", name)
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk());
    }

    @Order(24)
    @Test
    void setTrayTypeAsActive() throws Exception {
        assumeTrue(authToken != null && !authToken.isEmpty(), "Authentication failed, skipping setTrayTypeAsActive test.");

        String name = "Stor";
        mockMvc.perform(put("/TrayType/{name}/activate",name)
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk());
    }

    @Order(25)
    @Test
    void setPlantTypeAsInactive() throws Exception {
        assumeTrue(authToken != null && !authToken.isEmpty(), "Authentication failed, skipping setPlantTypeAsInactive test.");

        String name = "Solsikke";
        mockMvc.perform(put("/PlantType/{name}", name)
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk());
    }

    @Order(26)
    @Test
    void setPlantTypeAsActive() throws Exception {
        assumeTrue(authToken != null && !authToken.isEmpty(), "Authentication failed, skipping setPlantTypeAsActive test.");

        String name = "Solsikke";
        mockMvc.perform(put("/PlantType/{name}/activate",name)
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk());
    }


}
