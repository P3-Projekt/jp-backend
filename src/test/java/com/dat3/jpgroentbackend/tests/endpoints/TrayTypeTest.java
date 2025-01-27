package com.dat3.jpgroentbackend.tests.endpoints;

import com.dat3.jpgroentbackend.model.*;
import com.dat3.jpgroentbackend.model.repositories.TrayTypeRepository;
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
public class TrayTypeTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TrayTypeRepository trayTypeRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    private String authToken;

    private final String invalidTrayType = "InvalidTrayType";

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
    void createTrayType_NameBlank() throws Exception {
        assumeTrue(authToken != null && !authToken.isEmpty(), "Authentication failed, skipping createUser test.");

        mockMvc.perform(post("/TrayType")
                .contentType("application/json")
                .content("{\"name\": \"\", \"widthCm\": 50, \"lengthCm\": 100}")
                .header("Authorization", "Bearer " + authToken))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.type").value("about:blank"))
            .andExpect(jsonPath("$.title").value("Bad Request"))
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.detail").value("'name': must not be blank"))
            .andExpect(jsonPath("$.instance").value("/TrayType"));
    }

    @Order(3)
    @Test
    void createTrayType_NameNull() throws Exception {
        assumeTrue(authToken != null && !authToken.isEmpty(), "Authentication failed, skipping createUser test.");

        mockMvc.perform(post("/TrayType")
                        .contentType("application/json")
                        .content("{\"widthCm\": 50, \"lengthCm\": 100}")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("about:blank"))
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.detail").value("'name': must not be null"))
                .andExpect(jsonPath("$.instance").value("/TrayType"));
    }

    @Test
    void createTrayType_WidthNull() throws Exception {
        assumeTrue(authToken != null && !authToken.isEmpty(), "Authentication failed, skipping createUser test.");

        mockMvc.perform(post("/TrayType")
                        .contentType("application/json")
                        .content("{\"name\": \"WidthNull\", \"lengthCm\": 100}")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("about:blank"))
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.detail").value("'widthCm': must not be null"))
                .andExpect(jsonPath("$.instance").value("/TrayType"));
    }

    @Order(4)
    @Test
    void createTrayType_WidthNegative() throws Exception {
        assumeTrue(authToken != null && !authToken.isEmpty(), "Authentication failed, skipping createUser test.");

        mockMvc.perform(post("/TrayType")
                        .contentType("application/json")
                        .content("{\"name\": \"WidthNegative\", \"widthCm\": -1, \"lengthCm\": 100}")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("about:blank"))
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.detail").value("'widthCm': must be greater than or equal to 1"))
                .andExpect(jsonPath("$.instance").value("/TrayType"));
    }

    @Order(5)
    @Test
    void createTrayType_WidthNegativeOverflow() throws Exception {
        assumeTrue(authToken != null && !authToken.isEmpty(), "Authentication failed, skipping createUser test.");

        mockMvc.perform(post("/TrayType")
                        .contentType("application/json")
                        .content("{\"name\": \"WidthNegativeOverflow\", \"widthCm\": -2147483649, \"lengthCm\": 100}")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isBadRequest());
    }

    @Order(6)
    @Test
    void createTrayType_WidthZero() throws Exception {
        assumeTrue(authToken != null && !authToken.isEmpty(), "Authentication failed, skipping createUser test.");

        mockMvc.perform(post("/TrayType")
                        .contentType("application/json")
                        .content("{\"name\": \"WidthZero\", \"widthCm\": 0, \"lengthCm\": 100}")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("about:blank"))
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.detail").value("'widthCm': must be greater than or equal to 1"))
                .andExpect(jsonPath("$.instance").value("/TrayType"));
    }

    @Order(7)
    @Test
    void createTrayType_WidthPositiveOverflow() throws Exception {
        assumeTrue(authToken != null && !authToken.isEmpty(), "Authentication failed, skipping createUser test.");

        mockMvc.perform(post("/TrayType")
                        .contentType("application/json")
                        .content("{\"name\": \"WidthPositiveOverflow\", \"widthCm\": 2147483648, \"lengthCm\": 100}")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createTrayType_LengthNull() throws Exception {
        assumeTrue(authToken != null && !authToken.isEmpty(), "Authentication failed, skipping createUser test.");

        mockMvc.perform(post("/TrayType")
                        .contentType("application/json")
                        .content("{\"name\": \"LengthNull\", \"widthCm\": 50}")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("about:blank"))
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.detail").value("'lengthCm': must not be null"))
                .andExpect(jsonPath("$.instance").value("/TrayType"));
    }

    @Order(8)
    @Test
    void createTrayType_LengthNegative() throws Exception {
        assumeTrue(authToken != null && !authToken.isEmpty(), "Authentication failed, skipping createUser test.");

        mockMvc.perform(post("/TrayType")
                        .contentType("application/json")
                        .content("{\"name\": \"LengthNegative\", \"widthCm\": 50, \"lengthCm\": -1}")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("about:blank"))
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.detail").value("'lengthCm': must be greater than or equal to 1"))
                .andExpect(jsonPath("$.instance").value("/TrayType"));
    }

    @Order(9)
    @Test
    void createTrayType_LengthNegativeOverflow() throws Exception {
        assumeTrue(authToken != null && !authToken.isEmpty(), "Authentication failed, skipping createUser test.");

        mockMvc.perform(post("/TrayType")
                        .contentType("application/json")
                        .content("{\"name\": \"LengthNegativeOverflow\", \"widthCm\": 50, \"lengthCm\": -2147483649}")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isBadRequest());
    }

    @Order(10)
    @Test
    void createTrayType_LengthZero() throws Exception {
        assumeTrue(authToken != null && !authToken.isEmpty(), "Authentication failed, skipping createUser test.");

        mockMvc.perform(post("/TrayType")
                        .contentType("application/json")
                        .content("{\"name\": \"LengthZero\", \"widthCm\": 50, \"lengthCm\": 0}")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("about:blank"))
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.detail").value("'lengthCm': must be greater than or equal to 1"))
                .andExpect(jsonPath("$.instance").value("/TrayType"));
    }

    @Order(11)
    @Test
    void createTrayType_LengthPositiveOverflow() throws Exception {
        assumeTrue(authToken != null && !authToken.isEmpty(), "Authentication failed, skipping createUser test.");

        mockMvc.perform(post("/TrayType")
                        .contentType("application/json")
                        .content("{\"name\": \"LengthPositiveOverflow\", \"widthCm\": 50, \"lengthCm\": 2147483648}")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isBadRequest());
    }

    @Order(12)
    @Test
    void setTrayTypeInactive_TrayTypeInvalid() throws Exception {
        assumeTrue(authToken != null && !authToken.isEmpty(), "Authentication failed, skipping createUser test.");

        mockMvc.perform(put("/TrayType/{name}/inactivate", invalidTrayType))
                .andExpect(status().isForbidden());
    }

    @Order(13)
    @Test
    void setTrayTypeInactive_TrayTypeInactive() throws Exception {
        assumeTrue(authToken != null && !authToken.isEmpty(), "Authentication failed, skipping createUser test.");

        String trayTypeName = "InactiveTrayType";
        TrayType inactiveTrayType = new TrayType(trayTypeName, 50, 100);
        inactiveTrayType.setActive(false);
        trayTypeRepository.save(inactiveTrayType);

        mockMvc.perform(put("/TrayType/{name}/inactivate", trayTypeName))
                .andExpect(status().isForbidden());
    }

    @Order(14)
    @Test
    void setTrayTypeActive_TrayTypeInvalid() throws Exception {
        assumeTrue(authToken != null && !authToken.isEmpty(), "Authentication failed, skipping createUser test.");

        mockMvc.perform(put("/TrayType/{name}/inactivate", invalidTrayType))
                .andExpect(status().isForbidden());
    }

    @Order(15)
    @Test
    void setTrayTypeActive_TrayTypeActive() throws Exception {
        assumeTrue(authToken != null && !authToken.isEmpty(), "Authentication failed, skipping createUser test.");

        String trayTypeName = "ActiveTrayType";
        TrayType inactiveTrayType = new TrayType(trayTypeName, 50, 100);
        inactiveTrayType.setActive(true);
        trayTypeRepository.save(inactiveTrayType);

        mockMvc.perform(put("/TrayType/{name}/inactivate", trayTypeName))
                .andExpect(status().isForbidden());
    }
}
