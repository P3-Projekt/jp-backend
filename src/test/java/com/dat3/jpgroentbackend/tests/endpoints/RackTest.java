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

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class RackTest {

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
    void createRack_xCoordinateNull() throws Exception {
        assumeTrue(authToken != null && !authToken.isEmpty(), "Authentication failed, skipping createUser test.");

        mockMvc.perform(post("/Rack")
                    .contentType("application/json")
                    .content("{\"yCoordinate\": 10}")
                    .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("about:blank"))
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.detail").value("'xCoordinate': must not be null"))
                .andExpect(jsonPath("$.instance").value("/Rack"));
    }

//    @Order(3)
//    @Test
//    void createRack_xCoordinateBlank() throws Exception {
//        assumeTrue(authToken != null && !authToken.isEmpty(), "Authentication failed, skipping createUser test.");
//
//        mockMvc.perform(post("/Rack")
//                        .contentType("application/json")
//                        .content("{\"xCoordinate\": \"\", \"yCoordinate\": 10}")
//                        .header("Authorization", "Bearer " + authToken))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.type").value("about:blank"))
//                .andExpect(jsonPath("$.title").value("Bad Request"))
//                .andExpect(jsonPath("$.status").value(400))
//                .andExpect(jsonPath("$.detail").value("'xCoordinate': must not be blank"))
//                .andExpect(jsonPath("$.instance").value("/Rack"));
//    }

    @Order(4)
    @Test
    void createRack_xCoordinateNegativeOverflow() throws Exception {
        assumeTrue(authToken != null && !authToken.isEmpty(), "Authentication failed, skipping createUser test.");

        mockMvc.perform(post("/Rack")
                        .contentType("application/json")
                        .content("{\"xCoordinate\": -2147483649, \"yCoordinate\": 10}")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isBadRequest());
    }

    @Order(5)
    @Test
    void createRack_xCoordinateNegative() throws Exception {
        assumeTrue(authToken != null && !authToken.isEmpty(), "Authentication failed, skipping createUser test.");

        mockMvc.perform(post("/Rack")
                        .contentType("application/json")
                        .content("{\"xCoordinate\": -1, \"yCoordinate\": 10}")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("about:blank"))
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.detail").value("'xCoordinate': must be greater than or equal to 0"))
                .andExpect(jsonPath("$.instance").value("/Rack"));
    }

    @Order(6)
    @Test
    void createRack_xCoordinatePositiveOverflow() throws Exception {
        assumeTrue(authToken != null && !authToken.isEmpty(), "Authentication failed, skipping createUser test.");

        mockMvc.perform(post("/Rack")
                        .contentType("application/json")
                        .content("{\"xCoordinate\": 2147483649, \"yCoordinate\": 10}")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isBadRequest());
    }

    @Order(7)
    @Test
    void createRack_xCoordinateLowerLimit() throws Exception {
        assumeTrue(authToken != null && !authToken.isEmpty(), "Authentication failed, skipping createUser test.");

        mockMvc.perform(post("/Rack")
                        .contentType("application/json")
                        .content("{\"xCoordinate\": 0, \"yCoordinate\": 10}")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk());
    }

    @Order(8)
    @Test
    void createRack_xCoordinateUpperLimit() throws Exception {
        assumeTrue(authToken != null && !authToken.isEmpty(), "Authentication failed, skipping createUser test.");

        mockMvc.perform(post("/Rack")
                        .contentType("application/json")
                        .content("{\"xCoordinate\": 2147483647, \"yCoordinate\": 10}")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk());
    }

    @Order(9)
    @Test
    void createRack_yCoordinateNull() throws Exception {
        assumeTrue(authToken != null && !authToken.isEmpty(), "Authentication failed, skipping createUser test.");

        mockMvc.perform(post("/Rack")
                        .contentType("application/json")
                        .content("{\"xCoordinate\": 5}")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("about:blank"))
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.detail").value("'yCoordinate': must not be null"))
                .andExpect(jsonPath("$.instance").value("/Rack"));
    }

//    @Order(10)
//    @Test
//    void createRack_yCoordinateBlank() throws Exception {
//        assumeTrue(authToken != null && !authToken.isEmpty(), "Authentication failed, skipping createUser test.");
//
//        mockMvc.perform(post("/Rack")
//                        .contentType("application/json")
//                        .content("{\"xCoordinate\": 5, \"yCoordinate\": \"\"}")
//                        .header("Authorization", "Bearer " + authToken))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.type").value("about:blank"))
//                .andExpect(jsonPath("$.title").value("Bad Request"))
//                .andExpect(jsonPath("$.status").value(400))
//                .andExpect(jsonPath("$.detail").value("'yCoordinate': must not be blank"))
//                .andExpect(jsonPath("$.instance").value("/Rack"));
//    }

    @Order(11)
    @Test
    void createRack_yCoordinateNegativeOverflow() throws Exception {
        assumeTrue(authToken != null && !authToken.isEmpty(), "Authentication failed, skipping createUser test.");

        mockMvc.perform(post("/Rack")
                        .contentType("application/json")
                        .content("{\"xCoordinate\": 5, \"yCoordinate\": -2147483649}")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isBadRequest());
    }

    @Order(12)
    @Test
    void createRack_yCoordinateNegative() throws Exception {
        assumeTrue(authToken != null && !authToken.isEmpty(), "Authentication failed, skipping createUser test.");

        mockMvc.perform(post("/Rack")
                        .contentType("application/json")
                        .content("{\"xCoordinate\": 5, \"yCoordinate\": -1}")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("about:blank"))
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.detail").value("'yCoordinate': must be greater than or equal to 0"))
                .andExpect(jsonPath("$.instance").value("/Rack"));
    }

    @Order(13)
    @Test
    void createRack_yCoordinatePositiveOverflow() throws Exception {
        assumeTrue(authToken != null && !authToken.isEmpty(), "Authentication failed, skipping createUser test.");

        mockMvc.perform(post("/Rack")
                        .contentType("application/json")
                        .content("{\"xCoordinate\": 5, \"yCoordinate\": 2147483649}")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isBadRequest());
    }

    @Order(14)
    @Test
    void createRack_yCoordinateLowerLimit() throws Exception {
        assumeTrue(authToken != null && !authToken.isEmpty(), "Authentication failed, skipping createUser test.");

        mockMvc.perform(post("/Rack")
                        .contentType("application/json")
                        .content("{\"xCoordinate\": 5, \"yCoordinate\": 0}")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk());
    }

    @Order(15)
    @Test
    void createRack_yCoordinateUpperLimit() throws Exception {
        assumeTrue(authToken != null && !authToken.isEmpty(), "Authentication failed, skipping createUser test.");

        mockMvc.perform(post("/Rack")
                        .contentType("application/json")
                        .content("{\"xCoordinate\": 5, \"yCoordinate\": 2147483647}")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk());
    }
}