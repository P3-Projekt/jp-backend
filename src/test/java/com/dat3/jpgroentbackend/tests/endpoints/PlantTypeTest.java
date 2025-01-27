package com.dat3.jpgroentbackend.tests.endpoints;

import com.dat3.jpgroentbackend.model.*;
import com.dat3.jpgroentbackend.model.repositories.PlantTypeRepository;
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
public class PlantTypeTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PlantTypeRepository plantTypeRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    private String authToken;

    private final String invalidPlantType = "InvalidPlantType";

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
    void createPlantType_NameBlank() throws Exception {
        assumeTrue(authToken != null && !authToken.isEmpty(), "Authentication failed, skipping createUser test.");

        mockMvc.perform(post("/PlantType")
                        .contentType("application/json")
                        .content("{\"name\": \"\", \"preGerminationDays\": 5, \"growthTimeDays\": 10, \"preferredPosition\": 1, \"wateringSchedule\": [1, 5, 7]}")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("about:blank"))
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.detail").value("'name': must not be blank"))
                .andExpect(jsonPath("$.instance").value("/PlantType"));
    }

//    @Order(3)
//    @Test
//    void createPlantType_NameNull() throws Exception {
//        assumeTrue(authToken != null && !authToken.isEmpty(), "Authentication failed, skipping createUser test.");
//
//        mockMvc.perform(post("/PlantType")
//                        .contentType("application/json")
//                        .content("{\"preGerminationDays\": 5, \"growthTimeDays\": 10, \"preferredPosition\": 1, \"wateringSchedule\": [1, 5, 7]}")
//                        .header("Authorization", "Bearer " + authToken))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.type").value("about:blank"))
//                .andExpect(jsonPath("$.title").value("Bad Request"))
//                .andExpect(jsonPath("$.status").value(400))
//                .andExpect(jsonPath("$.detail").value("'name': must not be null"))
//                .andExpect(jsonPath("$.instance").value("/PlantType"));
//    }

    @Order(4)
    @Test
    void createPlantType_PreGerminationDaysNull() throws Exception {
        assumeTrue(authToken != null && !authToken.isEmpty(), "Authentication failed, skipping createUser test.");

        mockMvc.perform(post("/PlantType")
                        .contentType("application/json")
                        .content("{\"name\": \"PreGerminationDaysNull\", \"growthTimeDays\": 10, \"preferredPosition\": 1, \"wateringSchedule\": [1, 5, 7]}")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("about:blank"))
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.detail").value("'preGerminationDays': must not be null"))
                .andExpect(jsonPath("$.instance").value("/PlantType"));
    }

    @Order(5)
    @Test
    void createPlantType_PreGerminationDaysNegative() throws Exception {
        assumeTrue(authToken != null && !authToken.isEmpty(), "Authentication failed, skipping createUser test.");

        mockMvc.perform(post("/PlantType")
                        .contentType("application/json")
                        .content("{\"name\": \"PreGerminationDaysNegative\", \"preGerminationDays\": -1, \"growthTimeDays\": 10, \"preferredPosition\": 1, \"wateringSchedule\": [1, 5, 7]}")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("about:blank"))
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.detail").value("'preGerminationDays': must be greater than or equal to 0"))
                .andExpect(jsonPath("$.instance").value("/PlantType"));
    }

    @Order(6)
    @Test
    void createPlantType_PreGerminationDaysNegativeOverflow() throws Exception {
        assumeTrue(authToken != null && !authToken.isEmpty(), "Authentication failed, skipping createUser test.");

        mockMvc.perform(post("/PlantType")
                        .contentType("application/json")
                        .content("{\"name\": \"PreGerminationDaysNegativeOverflow\", \"preGerminationDays\": -2147483649, \"growthTimeDays\": 10, \"preferredPosition\": 1, \"wateringSchedule\": [1, 5, 7]}")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isBadRequest());
    }

    @Order(7)
    @Test
    void createPlantType_PreGerminationDaysPositiveOverflow() throws Exception {
        assumeTrue(authToken != null && !authToken.isEmpty(), "Authentication failed, skipping createUser test.");

        mockMvc.perform(post("/PlantType")
                        .contentType("application/json")
                        .content("{\"name\": \"PreGerminationDaysNegative\", \"preGerminationDays\": 2147483649, \"growthTimeDays\": 10, \"preferredPosition\": 1, \"wateringSchedule\": [1, 5, 7]}")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isBadRequest());
    }

    @Order(8)
    @Test
    void createPlantType_PreGerminationDaysLowerLimit() throws Exception {
        assumeTrue(authToken != null && !authToken.isEmpty(), "Authentication failed, skipping createUser test.");

        mockMvc.perform(post("/PlantType")
                        .contentType("application/json")
                        .content("{\"name\": \"PreGerminationDaysLowerLimit\", \"preGerminationDays\": 0, \"growthTimeDays\": 10, \"preferredPosition\": 1, \"wateringSchedule\": [1, 5, 7]}")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("PreGerminationDaysLowerLimit"))
                .andExpect(jsonPath("$.preGerminationDays").value(0))
                .andExpect(jsonPath("$.growthTimeDays").value(10))
                .andExpect(jsonPath("$.preferredPosition").value("High"))
                .andExpect(jsonPath("$.wateringSchedule").value(contains(1, 5, 7)))
                .andExpect(jsonPath("$.active").value(true));
    }

    @Order(9)
    @Test
    void createPlantType_PreGerminationDaysUpperLimit() throws Exception {
        assumeTrue(authToken != null && !authToken.isEmpty(), "Authentication failed, skipping createUser test.");

        mockMvc.perform(post("/PlantType")
                        .contentType("application/json")
                        .content("{\"name\": \"PreGerminationDaysUpperLimit\", \"preGerminationDays\": 2147483647, \"growthTimeDays\": 10, \"preferredPosition\": 1, \"wateringSchedule\": [1, 5, 7]}")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("PreGerminationDaysUpperLimit"))
                .andExpect(jsonPath("$.preGerminationDays").value(2147483647))
                .andExpect(jsonPath("$.growthTimeDays").value(10))
                .andExpect(jsonPath("$.preferredPosition").value("High"))
                .andExpect(jsonPath("$.wateringSchedule").value(contains(1, 5, 7)))
                .andExpect(jsonPath("$.active").value(true));
    }

    @Order(10)
    @Test
    void createPlantType_GrowthTimeDaysNull() throws Exception {
        assumeTrue(authToken != null && !authToken.isEmpty(), "Authentication failed, skipping createUser test.");

        mockMvc.perform(post("/PlantType")
                        .contentType("application/json")
                        .content("{\"name\": \"GrowthTimeDaysNull\", \"preGerminationDays\": 5, \"preferredPosition\": 1, \"wateringSchedule\": [1, 5, 7]}")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("about:blank"))
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.detail").value("'growthTimeDays': must not be null"))
                .andExpect(jsonPath("$.instance").value("/PlantType"));
    }

    @Order(11)
    @Test
    void createPlantType_GrowthTimeDaysNegative() throws Exception {
        assumeTrue(authToken != null && !authToken.isEmpty(), "Authentication failed, skipping createUser test.");

        mockMvc.perform(post("/PlantType")
                        .contentType("application/json")
                        .content("{\"name\": \"GrowthTimeDaysNegative\", \"preGerminationDays\": 5, \"growthTimeDays\": -1, \"preferredPosition\": 1, \"wateringSchedule\": [1, 5, 7]}")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("about:blank"))
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.detail").value("'growthTimeDays': must be greater than or equal to 0"))
                .andExpect(jsonPath("$.instance").value("/PlantType"));
    }

    @Order(12)
    @Test
    void createPlantType_GrowthTimeDaysNegativeOverflow() throws Exception {
        assumeTrue(authToken != null && !authToken.isEmpty(), "Authentication failed, skipping createUser test.");

        mockMvc.perform(post("/PlantType")
                        .contentType("application/json")
                        .content("{\"name\": \"GrowthTimeDaysNegativeOverflow\", \"preGerminationDays\": 5, \"growthTimeDays\": -2147483649, \"preferredPosition\": 1, \"wateringSchedule\": [1, 5, 7]}")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isBadRequest());
    }

    @Order(13)
    @Test
    void createPlantType_GrowthTimeDaysPositiveOverflow() throws Exception {
        assumeTrue(authToken != null && !authToken.isEmpty(), "Authentication failed, skipping createUser test.");

        mockMvc.perform(post("/PlantType")
                        .contentType("application/json")
                        .content("{\"name\": \"GrowthTimeDaysPositiveOverflow\", \"preGerminationDays\": 5, \"growthTimeDays\": 2147483649, \"preferredPosition\": 1, \"wateringSchedule\": [1, 5, 7]}")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isBadRequest());
    }

    @Order(14)
    @Test
    void createPlantType_GrowthTimeDaysLowerLimit() throws Exception {
        assumeTrue(authToken != null && !authToken.isEmpty(), "Authentication failed, skipping createUser test.");

        mockMvc.perform(post("/PlantType")
                        .contentType("application/json")
                        .content("{\"name\": \"GrowthTimeDaysLowerLimit\", \"preGerminationDays\": 5, \"growthTimeDays\": 0, \"preferredPosition\": 1, \"wateringSchedule\": [1]}")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("PreGerminationDaysOne"))
                .andExpect(jsonPath("$.preGerminationDays").value(5))
                .andExpect(jsonPath("$.growthTimeDays").value(0))
                .andExpect(jsonPath("$.preferredPosition").value("High"))
                .andExpect(jsonPath("$.wateringSchedule").value(contains(1)))
                .andExpect(jsonPath("$.active").value(true));
    }

    @Order(15)
    @Test
    void createPlantType_GrowthTimeDaysUpperLimit() throws Exception {
        assumeTrue(authToken != null && !authToken.isEmpty(), "Authentication failed, skipping createUser test.");

        mockMvc.perform(post("/PlantType")
                        .contentType("application/json")
                        .content("{\"name\": \"GrowthTimeDaysUpperLimit\", \"preGerminationDays\": 5, \"growthTimeDays\": 2147483647, \"preferredPosition\": 1, \"wateringSchedule\": [1, 5, 7]}")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("GrowthTimeDaysUpperLimit"))
                .andExpect(jsonPath("$.preGerminationDays").value(5))
                .andExpect(jsonPath("$.growthTimeDays").value(2147483647))
                .andExpect(jsonPath("$.preferredPosition").value("High"))
                .andExpect(jsonPath("$.wateringSchedule").value(contains(1, 5, 7)))
                .andExpect(jsonPath("$.active").value(true));
    }

    @Order(16)
    @Test
    void createPlantType_PreferredPositionNull() throws Exception {
        assumeTrue(authToken != null && !authToken.isEmpty(), "Authentication failed, skipping createUser test.");

        mockMvc.perform(post("/PlantType")
                        .contentType("application/json")
                        .content("{\"name\": \"PreferredPositionNull\", \"preGerminationDays\": 5, \"growthTimeDays\": 10, \"wateringSchedule\": [1, 5, 7]}")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("about:blank"))
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.detail").value("'preferredPosition': must not be null"))
                .andExpect(jsonPath("$.instance").value("/PlantType"));
    }

    @Order(17)
    @Test
    void createPlantType_PreferredPositionNegative() throws Exception {
        assumeTrue(authToken != null && !authToken.isEmpty(), "Authentication failed, skipping createUser test.");

        mockMvc.perform(post("/PlantType")
                        .contentType("application/json")
                        .content("{\"name\": \"PreferredPositionNegative\", \"preGerminationDays\": 5, \"growthTimeDays\": 10, \"preferredPosition\": -1, \"wateringSchedule\": [1, 5, 7]}")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isBadRequest());
    }

    @Order(18)
    @Test
    void createPlantType_PreferredPositionNegativeOverflow() throws Exception {
        assumeTrue(authToken != null && !authToken.isEmpty(), "Authentication failed, skipping createUser test.");

        mockMvc.perform(post("/PlantType")
                        .contentType("application/json")
                        .content("{\"name\": \"PreGerminationDaysNegativeOverflow\", \"preGerminationDays\": 5, \"growthTimeDays\": 10, \"preferredPosition\": -2147483649, \"wateringSchedule\": [1, 5, 7]}")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isBadRequest());
    }

    @Order(19)
    @Test
    void createPlantType_PreferredPositionPositiveOverflow() throws Exception {
        assumeTrue(authToken != null && !authToken.isEmpty(), "Authentication failed, skipping createUser test.");

        mockMvc.perform(post("/PlantType")
                        .contentType("application/json")
                        .content("{\"name\": \"PreferredPositionPositiveOverflow\", \"preGerminationDays\": 5, \"growthTimeDays\": 10, \"preferredPosition\": 2147483649, \"wateringSchedule\": [1, 5, 7]}")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isBadRequest());
    }

    @Order(20)
    @Test
    void createPlantType_PreferredPositionLowerLimit() throws Exception {
        assumeTrue(authToken != null && !authToken.isEmpty(), "Authentication failed, skipping createUser test.");

        mockMvc.perform(post("/PlantType")
                        .contentType("application/json")
                        .content("{\"name\": \"PreferredPositionLowerLimit\", \"preGerminationDays\": 5, \"growthTimeDays\": 10, \"preferredPosition\": 0, \"wateringSchedule\": [1, 5, 7]}")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("PreferredPositionLowerLimit"))
                .andExpect(jsonPath("$.preGerminationDays").value(5))
                .andExpect(jsonPath("$.growthTimeDays").value(10))
                .andExpect(jsonPath("$.preferredPosition").value("Low"))
                .andExpect(jsonPath("$.wateringSchedule").value(contains(1, 5, 7)))
                .andExpect(jsonPath("$.active").value(true));
    }

    @Order(21)
    @Test
    void createPlantType_PreferredPositionUpperLimit() throws Exception {
        assumeTrue(authToken != null && !authToken.isEmpty(), "Authentication failed, skipping createUser test.");

        mockMvc.perform(post("/PlantType")
                        .contentType("application/json")
                        .content("{\"name\": \"PreferredPositionUpperLimit\", \"preGerminationDays\": 5, \"growthTimeDays\": 10, \"preferredPosition\": 2, \"wateringSchedule\": [1, 5, 7]}")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("PreferredPositionUpperLimit"))
                .andExpect(jsonPath("$.preGerminationDays").value(5))
                .andExpect(jsonPath("$.growthTimeDays").value(10))
                .andExpect(jsonPath("$.preferredPosition").value("NoPreferred"))
                .andExpect(jsonPath("$.wateringSchedule").value(contains(1, 5, 7)))
                .andExpect(jsonPath("$.active").value(true));
    }

    @Order(22)
    @Test
    void createPlantType_WateringScheduleNull() throws Exception {
        assumeTrue(authToken != null && !authToken.isEmpty(), "Authentication failed, skipping createUser test.");

        mockMvc.perform(post("/PlantType")
                        .contentType("application/json")
                        .content("{\"name\": \"WateringScheduleNull\", \"preGerminationDays\": 5, \"growthTimeDays\": 10, \"preferredPosition\": 1}")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("about:blank"))
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.detail").value("'wateringSchedule': must not be null"))
                .andExpect(jsonPath("$.instance").value("/PlantType"));
    }

    @Order(23)
    @Test
    void createPlantType_WateringScheduleNegativeOverflow() throws Exception {
        assumeTrue(authToken != null && !authToken.isEmpty(), "Authentication failed, skipping createUser test.");

        mockMvc.perform(post("/PlantType")
                        .contentType("application/json")
                        .content("{\"name\": \"WateringScheduleNegativeOverflow\", \"preGerminationDays\": 5, \"growthTimeDays\": 10, \"preferredPosition\": 1, \"wateringSchedule\": [-2147483649, 5, 7]}")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isBadRequest());
    }

    @Order(24)
    @Test
    void createPlantType_WateringSchedulePositiveOverflow() throws Exception {
        assumeTrue(authToken != null && !authToken.isEmpty(), "Authentication failed, skipping createUser test.");

        mockMvc.perform(post("/PlantType")
                        .contentType("application/json")
                        .content("{\"name\": \"WateringSchedulePositiveOverflow\", \"preGerminationDays\": 5, \"growthTimeDays\": 10, \"preferredPosition\": 1, \"wateringSchedule\": [2147483649, 5, 7]}")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isBadRequest());
    }

    @Order(25)
    @Test
    void createPlantType_WateringScheduleLowerLimit() throws Exception {
        assumeTrue(authToken != null && !authToken.isEmpty(), "Authentication failed, skipping createUser test.");

        mockMvc.perform(post("/PlantType")
                        .contentType("application/json")
                        .content("{\"name\": \"WateringScheduleLowerLimit\", \"preGerminationDays\": 5, \"growthTimeDays\": 10, \"preferredPosition\": 1, \"wateringSchedule\": [1, 5, 7]}")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("WateringScheduleLowerLimit"))
                .andExpect(jsonPath("$.preGerminationDays").value(5))
                .andExpect(jsonPath("$.growthTimeDays").value(10))
                .andExpect(jsonPath("$.preferredPosition").value("High"))
                .andExpect(jsonPath("$.wateringSchedule").value(contains(1, 5, 7)))
                .andExpect(jsonPath("$.active").value(true));
    }

    @Order(26)
    @Test
    void createPlantType_WateringScheduleUpperLimit() throws Exception {
        assumeTrue(authToken != null && !authToken.isEmpty(), "Authentication failed, skipping createUser test.");

        mockMvc.perform(post("/PlantType")
                        .contentType("application/json")
                        .content("{\"name\": \"WateringScheduleUpperLimit\", \"preGerminationDays\": 5, \"growthTimeDays\": 2147483647, \"preferredPosition\": 1, \"wateringSchedule\": [1, 5, 7, 2147483647]}")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("WateringScheduleUpperLimit"))
                .andExpect(jsonPath("$.preGerminationDays").value(5))
                .andExpect(jsonPath("$.growthTimeDays").value(2147483647))
                .andExpect(jsonPath("$.preferredPosition").value("High"))
                .andExpect(jsonPath("$.wateringSchedule").value(contains(1, 5, 7, 2147483647)))
                .andExpect(jsonPath("$.active").value(true));
    }

    @Order(27)
    @Test
    void createPlantType_WateringScheduleDuplicateEntries() throws Exception {
        assumeTrue(authToken != null && !authToken.isEmpty(), "Authentication failed, skipping createUser test.");

        mockMvc.perform(post("/PlantType")
                        .contentType("application/json")
                        .content("{\"name\": \"WateringScheduleDuplicateEntries\", \"preGerminationDays\": 5, \"growthTimeDays\": 10, \"preferredPosition\": 1, \"wateringSchedule\": [1, 5, 5, 7]}")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isBadRequest());
    }

    @Order(28)
    @Test
    void setPlantTypeInactive_PlantTypeInvalid() throws Exception {
        assumeTrue(authToken != null && !authToken.isEmpty(), "Authentication failed, skipping createUser test.");

        mockMvc.perform(put("/PlantType/{name}", invalidPlantType))
                .andExpect(status().isForbidden());
    }

    @Order(29)
    @Test
    void setPlantTypeInactive_PlantTypeInactive() throws Exception {
        assumeTrue(authToken != null && !authToken.isEmpty(), "Authentication failed, skipping createUser test.");

        String plantTypeName = "InactivePlantType";
        PlantType inactivePlantType = new PlantType(plantTypeName, 5, 10, PlantType.PreferredPosition.High, new int[]{1, 5, 7});
        inactivePlantType.setActive(false);
        plantTypeRepository.save(inactivePlantType);

        mockMvc.perform(put("/PlantType/{name}", plantTypeName))
                .andExpect(status().isForbidden());
    }

    @Order(30)
    @Test
    void setPlantTypeActive_PlantTypeInvalid() throws Exception {
        assumeTrue(authToken != null && !authToken.isEmpty(), "Authentication failed, skipping createUser test.");

        mockMvc.perform(put("/PlantType/{name}/activate", invalidPlantType))
                .andExpect(status().isForbidden());
    }

    @Order(31)
    @Test
    void setPlantTypeActive_PlantTypeActive() throws Exception {
        assumeTrue(authToken != null && !authToken.isEmpty(), "Authentication failed, skipping createUser test.");

        String plantTypeName = "ActivePlantType";
        PlantType inactivePlantType = new PlantType(plantTypeName, 5, 10, PlantType.PreferredPosition.High, new int[]{1, 5, 7});
        inactivePlantType.setActive(true);
        plantTypeRepository.save(inactivePlantType);

        mockMvc.perform(put("/PlantType/{name}/activate", plantTypeName))
                .andExpect(status().isForbidden());
    }
}
