package com.dat3.jpgroentbackend.tests.endpoints;

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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.hamcrest.Matchers.contains;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class BatchTest {

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
    void createTestPlantType() throws Exception {
        assumeTrue(authToken != null && !authToken.isEmpty(), "Authentication failed, skipping createUser test.");

        mockMvc.perform(post("/PlantType")
                        .contentType("application/json")
                        .content("{\"name\": \"TestPlantType\", \"preGerminationDays\": 5, \"growthTimeDays\": 10, \"preferredPosition\": 1, \"wateringSchedule\": [1, 5, 7]}")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("TestPlantType"))
                .andExpect(jsonPath("$.preGerminationDays").value(5))
                .andExpect(jsonPath("$.growthTimeDays").value(10))
                .andExpect(jsonPath("$.preferredPosition").value("High"))
                .andExpect(jsonPath("$.wateringSchedule").value(contains(1, 5, 7)))
                .andExpect(jsonPath("$.active").value(true));
    }

    @Order(3)
    @Test
    void createTestTrayType() throws Exception {
        assumeTrue(authToken != null && !authToken.isEmpty(), "Authentication failed, skipping createUser test.");

        mockMvc.perform(post("/TrayType")
                        .contentType("application/json")
                        .content("{\"name\": \"TestTrayType\", \"widthCm\": 50, \"lengthCm\": 100}")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("TestTrayType"))
                .andExpect(jsonPath("$.lengthCm").value(100))
                .andExpect(jsonPath("$.widthCm").value(50))
                .andExpect(jsonPath("$.active").value(true));
    }

//    @Order(4)
//    @Test
//    void createBatch_PlantTypeIdNull() throws Exception {
//        assumeTrue(authToken != null && !authToken.isEmpty(), "Authentication failed, skipping createUser test.");
//
//        mockMvc.perform(post("/Batch")
//                        .contentType("application/json")
//                        .content("{\"trayTypeId\": \"TestTrayType\", \"createdByUsername\": \"Jens\", \"amount\": 1}")
//                        .header("Authorization", "Bearer " + authToken))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.type").value("about:blank"))
//                .andExpect(jsonPath("$.title").value("Bad Request"))
//                .andExpect(jsonPath("$.status").value(400))
//                .andExpect(jsonPath("$.detail").value("'plantTypeId': must not be null"))
//                .andExpect(jsonPath("$.instance").value("/Batch"));
//    }

    @Order(5)
    @Test
    void createBatch_PlantTypeIdBlank() throws Exception {
        assumeTrue(authToken != null && !authToken.isEmpty(), "Authentication failed, skipping createUser test.");

        mockMvc.perform(post("/Batch")
                        .contentType("application/json")
                        .content("{\"plantTypeId\": \"\", \"trayTypeId\": \"TestTrayType\", \"createdByUsername\": \"Jens\", \"amount\": 1}")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("about:blank"))
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.detail").value("'plantTypeId': must not be blank"))
                .andExpect(jsonPath("$.instance").value("/Batch"));
    }

    @Order(6)
    @Test
    void createBatch_PlantTypeIdInvalid() throws Exception {
        assumeTrue(authToken != null && !authToken.isEmpty(), "Authentication failed, skipping createUser test.");

        mockMvc.perform(post("/Batch")
                        .contentType("application/json")
                        .content("{\"plantTypeId\": \"InvalidPlantTypeId\", \"trayTypeId\": \"TestTrayType\", \"createdByUsername\": \"Jens\", \"amount\": 1}")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isNotFound());
    }

//    @Order(7)
//    @Test
//    void createBatch_TrayTypeIdNull() throws Exception {
//        assumeTrue(authToken != null && !authToken.isEmpty(), "Authentication failed, skipping createUser test.");
//
//        mockMvc.perform(post("/Batch")
//                        .contentType("application/json")
//                        .content("{\"plantTypeId\": \"TestPlantType\", \"createdByUsername\": \"Jens\", \"amount\": 1}")
//                        .header("Authorization", "Bearer " + authToken))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.type").value("about:blank"))
//                .andExpect(jsonPath("$.title").value("Bad Request"))
//                .andExpect(jsonPath("$.status").value(400))
//                .andExpect(jsonPath("$.detail").value("'trayTypeId': must not be null"))
//                .andExpect(jsonPath("$.instance").value("/Batch"));
//    }

    @Order(8)
    @Test
    void createBatch_TrayTypeIdBlank() throws Exception {
        assumeTrue(authToken != null && !authToken.isEmpty(), "Authentication failed, skipping createUser test.");

        mockMvc.perform(post("/Batch")
                        .contentType("application/json")
                        .content("{\"plantTypeId\": \"TestPlantType\", \"trayTypeId\": \"\", \"createdByUsername\": \"Jens\", \"amount\": 1}")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("about:blank"))
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.detail").value("'trayTypeId': must not be blank"))
                .andExpect(jsonPath("$.instance").value("/Batch"));
    }

    @Order(9)
    @Test
    void createBatch_TrayTypeIdInvalid() throws Exception {
        assumeTrue(authToken != null && !authToken.isEmpty(), "Authentication failed, skipping createUser test.");

        mockMvc.perform(post("/Batch")
                        .contentType("application/json")
                        .content("{\"plantTypeId\": \"TestPlantType\", \"trayTypeId\": \"InvalidTrayTypeId\", \"createdByUsername\": \"Jens\", \"amount\": 1}")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isNotFound());
    }

//    @Order(10)
//    @Test
//    void createBatch_CreatedByUsernameNull() throws Exception {
//        assumeTrue(authToken != null && !authToken.isEmpty(), "Authentication failed, skipping createUser test.");
//
//        mockMvc.perform(post("/Batch")
//                        .contentType("application/json")
//                        .content("{\"plantTypeId\": \"TestPlantType\", \"trayTypeId\": \"TestTrayType\", \"amount\": 1}")
//                        .header("Authorization", "Bearer " + authToken))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.type").value("about:blank"))
//                .andExpect(jsonPath("$.title").value("Bad Request"))
//                .andExpect(jsonPath("$.status").value(400))
//                .andExpect(jsonPath("$.detail").value("'createdByUsername': must not be null"))
//                .andExpect(jsonPath("$.instance").value("/Batch"));
//    }

    @Order(11)
    @Test
    void createBatch_CreatedByUsernameBlank() throws Exception {
        assumeTrue(authToken != null && !authToken.isEmpty(), "Authentication failed, skipping createUser test.");

        mockMvc.perform(post("/Batch")
                        .contentType("application/json")
                        .content("{\"plantTypeId\": \"TestPlantType\", \"trayTypeId\": \"TestTrayType\", \"createdByUsername\": \"\", \"amount\": 1}")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("about:blank"))
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.detail").value("'createdByUsername': must not be blank"))
                .andExpect(jsonPath("$.instance").value("/Batch"));
    }

    @Order(12)
    @Test
    void createBatch_CreatedByUsernameInvalid() throws Exception {
        assumeTrue(authToken != null && !authToken.isEmpty(), "Authentication failed, skipping createUser test.");

        mockMvc.perform(post("/Batch")
                        .contentType("application/json")
                        .content("{\"plantTypeId\": \"TestPlantType\", \"trayTypeId\": \"TestTrayType\", \"createdByUsername\": \"InvalidCreatedByUsername\", \"amount\": 1}")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isNotFound());
    }

    @Order(13)
    @Test
    void createBatch_AmountNull() throws Exception {
        assumeTrue(authToken != null && !authToken.isEmpty(), "Authentication failed, skipping createUser test.");

        mockMvc.perform(post("/Batch")
                        .contentType("application/json")
                        .content("{\"plantTypeId\": \"TestPlantType\", \"trayTypeId\": \"TestTrayType\", \"createdByUsername\": \"Jens\"}")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("about:blank"))
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.detail").value("'amount': must not be null"))
                .andExpect(jsonPath("$.instance").value("/Batch"));
    }

    @Order(15)
    @Test
    void createBatch_AmountZero() throws Exception {
        assumeTrue(authToken != null && !authToken.isEmpty(), "Authentication failed, skipping createUser test.");

        mockMvc.perform(post("/Batch")
                        .contentType("application/json")
                        .content("{\"plantTypeId\": \"TestPlantType\", \"trayTypeId\": \"TestTrayType\", \"createdByUsername\": \"Jens\", \"amount\": 0}")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("about:blank"))
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.detail").value("'amount': must be greater than or equal to 1"))
                .andExpect(jsonPath("$.instance").value("/Batch"));
    }

    @Order(16)
    @Test
    void createBatch_AmountNegativeOverflow() throws Exception {
        assumeTrue(authToken != null && !authToken.isEmpty(), "Authentication failed, skipping createUser test.");

        mockMvc.perform(post("/Batch")
                        .contentType("application/json")
                        .content("{\"plantTypeId\": \"TestPlantType\", \"trayTypeId\": \"TestTrayType\", \"createdByUsername\": \"Jens\", \"amount\": -2147483649}")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isBadRequest());
    }

    @Order(17)
    @Test
    void createBatch_PositiveOverflow() throws Exception {
        assumeTrue(authToken != null && !authToken.isEmpty(), "Authentication failed, skipping createUser test.");

        mockMvc.perform(post("/Batch")
                        .contentType("application/json")
                        .content("{\"plantTypeId\": \"TestPlantType\", \"trayTypeId\": \"TestTrayType\", \"createdByUsername\": \"Jens\", \"amount\": 2147483649}")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isBadRequest());
    }

    @Order(18)
    @Test
    void createBatch_LowerLimit() throws Exception {
        assumeTrue(authToken != null && !authToken.isEmpty(), "Authentication failed, skipping createUser test.");

        mockMvc.perform(post("/Batch")
                        .contentType("application/json")
                        .content("{\"plantTypeId\": \"TestPlantType\", \"trayTypeId\": \"TestTrayType\", \"createdByUsername\": \"Jens\", \"amount\": 1}")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.plantTypeId").value("TestPlantType"))
                .andExpect(jsonPath("$.trayTypeId").value("TestTrayType"))
                .andExpect(jsonPath("$.createdBy").value("Jens"));
    }

    @Order(19)
    @Test
    void createBatch_UpperLimit() throws Exception {
        assumeTrue(authToken != null && !authToken.isEmpty(), "Authentication failed, skipping createUser test.");

        mockMvc.perform(post("/Batch")
                        .contentType("application/json")
                        .content("{\"plantTypeId\": \"TestPlantType\", \"trayTypeId\": \"TestTrayType\", \"createdByUsername\": \"Jens\", \"amount\": 2147483647}")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.plantTypeId").value("TestPlantType"))
                .andExpect(jsonPath("$.trayTypeId").value("TestTrayType"))
                .andExpect(jsonPath("$.createdBy").value("Jens"));
    }
}