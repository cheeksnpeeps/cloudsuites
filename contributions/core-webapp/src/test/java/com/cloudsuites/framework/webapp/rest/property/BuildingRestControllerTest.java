package com.cloudsuites.framework.webapp.rest.property;

import com.cloudsuites.framework.modules.property.features.repository.BuildingRepository;
import com.cloudsuites.framework.modules.property.features.repository.CompanyRepository;
import com.cloudsuites.framework.services.property.features.entities.Building;
import com.cloudsuites.framework.services.property.features.entities.Company;
import com.cloudsuites.framework.webapp.rest.property.dto.BuildingDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for the BuildingRestController.
 * This class tests the REST endpoints for managing buildings under specific companies.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class BuildingRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BuildingRepository buildingRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private String validCompanyId;
    private String validBuildingId1;
    private String validBuildingId2;

    /**
     * Set up test data before each test.
     * This method clears the database and initializes test data for a company and its buildings.
     */
    @BeforeEach
    void setUp() {
        clearDatabase();

        // Initialize test data
        validCompanyId = createCompany("Test Company").getCompanyId();
        validBuildingId1 = createBuilding("Building 1", validCompanyId).getBuildingId();
        validBuildingId2 = createBuilding("Building 2", validCompanyId).getBuildingId();
    }

    // -------------------- GET Requests --------------------

    /**
     * Test the retrieval of all buildings for a valid company.
     * This test verifies that all buildings are returned correctly.
     */
    @Test
    void testGetAllBuildings() throws Exception {
        mockMvc.perform(get("/api/v1/companies/{companyId}/buildings", validCompanyId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    List<BuildingDto> buildingDtos = objectMapper.readValue(jsonResponse, objectMapper.getTypeFactory().constructCollectionType(List.class, BuildingDto.class));
                    assertThat(buildingDtos).hasSize(2);
                });
    }

    /**
     * Test the retrieval of a building by a valid building ID.
     * This test checks if the correct building details are returned.
     */
    @Test
    void testGetBuildingById_ValidId() throws Exception {
        mockMvc.perform(get("/api/v1/companies/{companyId}/buildings/{buildingId}", validCompanyId, validBuildingId1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    BuildingDto buildingDto = objectMapper.readValue(jsonResponse, BuildingDto.class);
                    assertThat(buildingDto.getName()).isEqualTo("Building 1");
                });
    }

    /**
     * Test the retrieval of a building by an invalid building ID.
     * This test verifies that a not found status is returned.
     */
    @Test
    void testGetBuildingById_InvalidId() throws Exception {
        mockMvc.perform(get("/api/v1/companies/{companyId}/buildings/{buildingId}", validCompanyId, "invalidBuildingId"))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Building not found"))); // Example message check
    }

    // -------------------- POST Requests --------------------

    /**
     * Test saving a new building with valid data.
     * This test verifies that a building is created successfully and returns the correct response.
     */
    @Test
    void testSaveBuilding_ValidData() throws Exception {
        String newBuildingJson = createBuildingJson("New Building");
        mockMvc.perform(post("/api/v1/companies/{companyId}/buildings", validCompanyId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newBuildingJson))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    BuildingDto buildingDto = objectMapper.readValue(jsonResponse, BuildingDto.class);
                    assertThat(buildingDto.getName()).isEqualTo("New Building");
                });
    }

    /**
     * Parameterized test for saving buildings with invalid names.
     * This test checks various invalid building names to ensure that appropriate error responses are returned.
     *
     * @param buildingName The building name to test.
     */
    @ParameterizedTest
    @ValueSource(strings = {"", "a", "VeryVeryVeryLongBuildingNameThatExceedsTheExpectedLength"})
    void testSaveBuilding_InvalidData_BuildingNameLength(String buildingName) throws Exception {
        String newBuildingJson = createBuildingJson(buildingName);
        mockMvc.perform(post("/api/v1/companies/{companyId}/buildings", validCompanyId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newBuildingJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Building must be between"))); // Example message check
    }

    /**
     * Test saving a building with an invalid company ID.
     * This test verifies that a not found status is returned when the company ID does not exist.
     */
    @Test
    void testSaveBuilding_InvalidData_CompanyNotFound() throws Exception {
        String newBuildingJson = createBuildingJson("Valid Building");
        String invalidCompanyId = "nonExistentCompanyId"; // Invalid company ID
        mockMvc.perform(post("/api/v1/companies/{companyId}/buildings", invalidCompanyId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newBuildingJson))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Company not found"))); // Example message check
    }

    /**
     * Test saving a building with a null company ID.
     * This test verifies that a bad request status is returned when the company ID is null.
     */
    @Test
    void testSaveBuilding_InvalidData_NullCompanyId() throws Exception {
        String newBuildingJson = createBuildingJson("Building Without Company");
        mockMvc.perform(post("/api/v1/companies/{companyId}/buildings", (Object) null)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newBuildingJson))
                .andExpect(status().isBadRequest());
    }

    // -------------------- DELETE Requests --------------------

    /**
     * Test deleting a building with a valid ID.
     * This test verifies that the building is deleted and a no content status is returned.
     */
    @Test
    void testDeleteBuilding_ValidId() throws Exception {
        mockMvc.perform(delete("/api/v1/companies/{companyId}/buildings/{buildingId}", validCompanyId, validBuildingId1))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/companies/{companyId}/buildings/{buildingId}", validCompanyId, validBuildingId1))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Building not found"))); // Example message check
    }

    /**
     * Test deleting a building with an invalid building ID.
     * This test verifies that a not found status is returned when the building ID does not exist.
     */
    @Test
    void testDeleteBuilding_InvalidId() throws Exception {
        mockMvc.perform(delete("/api/v1/companies/{companyId}/buildings/{buildingId}", validCompanyId, "invalidBuildingId"))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Building not found"))); // Example message check
    }

    /**
     * Test deleting a building with an invalid company ID.
     * This test verifies that a not found status is returned when the company ID does not exist.
     */
    @Test
    void testDeleteBuilding_CompanyNotFound() throws Exception {
        String invalidCompanyId = "invalidCompanyId"; // Invalid company ID
        mockMvc.perform(delete("/api/v1/companies/{companyId}/buildings/{buildingId}", invalidCompanyId, validBuildingId1))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Building not found"))); // Example message check
    }

    /**
     * Test deleting a building that does not exist.
     * This test verifies that a not found status is returned when the building ID does not exist.
     */
    @Test
    void testDeleteBuilding_BuildingNotFound() throws Exception {
        mockMvc.perform(delete("/api/v1/companies/{companyId}/buildings/{buildingId}", validCompanyId, "invalidBuildingId"))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Building not found"))); // Example message check
    }

    // -------------------- Helper Methods --------------------

    /**
     * Clear the database by deleting all buildings and companies.
     * This method is used to ensure a clean state for each test.
     */
    private void clearDatabase() {
        buildingRepository.deleteAll();
        companyRepository.deleteAll();
    }

    /**
     * Create a company with the specified name.
     *
     * @param name The name of the company to create.
     * @return The created Company object.
     */
    private Company createCompany(String name) {
        Company company = new Company();
        company.setName(name);
        return companyRepository.save(company);
    }

    /**
     * Create a building with the specified name and associate it with a company.
     *
     * @param name      The name of the building to create.
     * @param companyId The ID of the company to associate with the building.
     * @return The created Building object.
     */
    private Building createBuilding(String name, String companyId) {
        Company company = companyRepository.findById(companyId).orElseThrow();
        Building building = new Building();
        building.setName(name);
        building.setCompany(company);
        return buildingRepository.save(building);
    }

    /**
     * Create a JSON representation of a building with the specified name.
     *
     * @param name The name of the building.
     * @return A JSON string representing the building.
     */
    private String createBuildingJson(String name) {
        return String.format("{\"name\":\"%s\"}", name);
    }
}
