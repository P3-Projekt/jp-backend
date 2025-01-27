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
public class UserTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    private String authToken;

    private final String invalidUsername = "InvalidUsername";

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
    void createUser_NameBlank() throws Exception {
        assumeTrue(authToken != null && !authToken.isEmpty(), "Authentication failed, skipping createUser test.");

        mockMvc.perform(post("/User")
                        .contentType("application/json")
                        .content("{\"name\": \"\", \"role\": \"Administrator\", \"password\": \"12345\"}")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("about:blank"))
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.detail").value("'name': must not be blank"))
                .andExpect(jsonPath("$.instance").value("/User"));
    }

//    @Order(3)
//    @Test
//    void createUser_NameNull() throws Exception {
//        assumeTrue(authToken != null && !authToken.isEmpty(), "Authentication failed, skipping createUser test.");
//
//        mockMvc.perform(post("/User")
//                        .contentType("application/json")
//                        .content("{\"role\": \"Administrator\", \"password\": \"12345\"}")
//                        .header("Authorization", "Bearer " + authToken))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.type").value("about:blank"))
//                .andExpect(jsonPath("$.title").value("Bad Request"))
//                .andExpect(jsonPath("$.status").value(400))
//                .andExpect(jsonPath("$.detail").value("'name': must not be null"))
//                .andExpect(jsonPath("$.instance").value("/User"));
//    }

    @Order(4)
    @Test
    void createUser_RoleInvalid() throws Exception {
        assumeTrue(authToken != null && !authToken.isEmpty(), "Authentication failed, skipping createUser test.");

        // Valid roles: Gardener, Manager, Administrator
        mockMvc.perform(post("/User")
                        .contentType("application/json")
                        .content("{\"name\": \"TestRoleInvalid\", \"role\": \"\", \"password\": \"12345\"}")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isBadRequest());
    }

    @Order(5)
    @Test
    void createUser_RoleNull() throws Exception {
        assumeTrue(authToken != null && !authToken.isEmpty(), "Authentication failed, skipping createUser test.");

        mockMvc.perform(post("/User")
                        .contentType("application/json")
                        .content("{\"name\": \"TestRoleNull\", \"password\": \"12345\"}")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("about:blank"))
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.detail").value("'role': must not be null"))
                .andExpect(jsonPath("$.instance").value("/User"));
    }

    @Order(6)
    @Test
    void createUser_PasswordBlank() throws Exception {
        assumeTrue(authToken != null && !authToken.isEmpty(), "Authentication failed, skipping createUser test.");

        mockMvc.perform(post("/User")
                        .contentType("application/json")
                        .content("{\"name\": \"TestRoleNull\", \"role\": \"Administrator\", \"password,\": \"12345\"}")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("about:blank"))
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.detail").value("'password': must not be blank"))
                .andExpect(jsonPath("$.instance").value("/User"));
    }

//    @Order(7)
//    @Test
//    void createUser_PasswordNull() throws Exception {
//        assumeTrue(authToken != null && !authToken.isEmpty(), "Authentication failed, skipping createUser test.");
//
//        mockMvc.perform(post("/User")
//                        .contentType("application/json")
//                        .content("{\"name\": \"TestRoleNull\", \"role\": \"Administrator\"}")
//                        .header("Authorization", "Bearer " + authToken))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.type").value("about:blank"))
//                .andExpect(jsonPath("$.title").value("Bad Request"))
//                .andExpect(jsonPath("$.status").value(400))
//                .andExpect(jsonPath("$.detail").value("'password': must not be null"))
//                .andExpect(jsonPath("$.instance").value("/User"));
//    }

    @Order(8)
    @Test
    void setUserInactive_UserInvalid() throws Exception {
        assumeTrue(authToken != null && !authToken.isEmpty(), "Authentication failed, skipping createUser test.");

        mockMvc.perform(put("/Users/{name}", invalidUsername))
                .andExpect(status().isForbidden());
    }

    @Order(9)
    @Test
    void setUserInactive_UserInactive() throws Exception {
        assumeTrue(authToken != null && !authToken.isEmpty(), "Authentication failed, skipping createUser test.");

        String username = "InactiveUser";
        User inactiveUser = new User(username, User.Role.Administrator, "1234");
        inactiveUser.setActive(false);
        userRepository.save(inactiveUser);

        mockMvc.perform(put("/Users/{name}", username))
                .andExpect(status().isForbidden());
    }

    @Order(10)
    @Test
    void setUserActive_UserInvalid() throws Exception {
        assumeTrue(authToken != null && !authToken.isEmpty(), "Authentication failed, skipping createUser test.");

        mockMvc.perform(put("/Users/{name}/activate", invalidUsername))
                .andExpect(status().isForbidden());
    }

    @Order(11)
    @Test
    void setUserActive_UserActive() throws Exception {
        String username = "ActiveUser";
        User inactiveUser = new User(username, User.Role.Administrator, "1234");
        inactiveUser.setActive(true);
        userRepository.save(inactiveUser);

        mockMvc.perform(put("/Users/{name}", username))
                .andExpect(status().isForbidden());
    }
}
