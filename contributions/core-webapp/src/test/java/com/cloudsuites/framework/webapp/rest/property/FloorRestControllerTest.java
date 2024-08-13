package com.cloudsuites.framework.webapp.rest.property;

import com.cloudsuites.framework.modules.property.features.repository.BuildingRepository;
import com.cloudsuites.framework.modules.property.features.repository.FloorRepository;
import com.cloudsuites.framework.modules.property.features.repository.UnitRepository;
import com.cloudsuites.framework.modules.user.repository.AdminRepository;
import com.cloudsuites.framework.services.common.exception.NotFoundResponseException;
import com.cloudsuites.framework.services.property.features.entities.Building;
import com.cloudsuites.framework.services.property.features.entities.Floor;
import com.cloudsuites.framework.services.property.features.entities.Unit;
import com.cloudsuites.framework.services.property.features.service.FloorService;
import com.cloudsuites.framework.webapp.authentication.utils.AdminTestHelper;
import com.cloudsuites.framework.webapp.rest.property.dto.FloorDto;
import com.cloudsuites.framework.webapp.rest.property.dto.UnitDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for the FloorRestController.
 * This class tests the REST endpoints for managing floors within a building.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class FloorRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private FloorService floorService;

    @Autowired
    private FloorRepository floorRepository;

    @Autowired
    private BuildingRepository buildingRepository;

    private String validBuildingId;
    private String validFloorId;
    @Autowired
    private UnitRepository unitRepository;

    private AdminTestHelper adminTestHelper;
    private String accessToken;
    @Autowired
    private AdminRepository adminRepository;

    /**
     * Set up test data before each test.
     * This method initializes test data for a building and a floor.
     */
    @BeforeEach
    void setUp() throws Exception {
        clearDatabase();
        validBuildingId = createBuilding("Test Building").getBuildingId();
        validFloorId = createFloor(validBuildingId, "Test Floor").getFloorId();
        adminTestHelper = new AdminTestHelper(mockMvc, objectMapper, null, null);
        accessToken = adminTestHelper.registerAdminAndGetToken("testRegisterAdmin", "+14166024668");
    }

    private MockHttpServletRequestBuilder withAuth(MockHttpServletRequestBuilder requestBuilder) {
        return requestBuilder.header("Authorization", "Bearer " + accessToken);
    }

    // -------------------- GET Requests --------------------

    /**
     * Test the retrieval of all floors for a building.
     * This test verifies that all floors are returned correctly for a building.
     */
    @Test
    void testGetAllFloorsByBuildingId() throws Exception {
        mockMvc.perform(withAuth(get("/api/v1/buildings/{buildingId}/floors", validBuildingId)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    List<FloorDto> floorDtos = objectMapper.readValue(jsonResponse, objectMapper.getTypeFactory().constructCollectionType(List.class, FloorDto.class));
                    assertThat(floorDtos).hasSize(1);
                    assertThat(floorDtos.get(0).getFloorName()).isEqualTo("Test Floor");
                });
    }

    /**
     * Test the retrieval of a floor by a valid floor ID.
     * This test checks if the correct floor details are returned.
     */
    @Test
    void testGetFloorById_ValidId() throws Exception {
        mockMvc.perform(withAuth(get("/api/v1/buildings/{buildingId}/floors/{floorId}", validBuildingId, validFloorId)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    FloorDto floorDto = objectMapper.readValue(jsonResponse, FloorDto.class);
                    assertThat(floorDto.getFloorName()).isEqualTo("Test Floor");
                });
    }

    /**
     * Test the retrieval of a floor by an invalid floor ID.
     * This test verifies that a not found status is returned.
     */
    @Test
    void testGetFloorById_InvalidId() throws Exception {
        mockMvc.perform(withAuth(get("/api/v1/buildings/{buildingId}/floors/{floorId}", validBuildingId, "invalidFloorId")))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Floor not found")));
    }

    // -------------------- POST Requests --------------------

    /**
     * Test saving a new floor with valid data.
     * This test verifies that a floor is created successfully and returns the correct response.
     */
    @Test
    void testSaveFloor_ValidData() throws Exception {
        FloorDto newFloorDto = new FloorDto();
        newFloorDto.setFloorName("New Floor");
        newFloorDto.setFloorNumber(2);
        UnitDto unit = new UnitDto();
        unit.setUnitNumber(201);
        unit.setNumberOfBedrooms(3);
        newFloorDto.setUnits(List.of(unit));

        mockMvc.perform(withAuth(post("/api/v1/buildings/{buildingId}/floors", validBuildingId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newFloorDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    FloorDto floorDto = objectMapper.readValue(jsonResponse, FloorDto.class);
                    assertThat(floorDto.getFloorName()).isEqualTo("New Floor");
                });
    }

    /**
     * Test saving a floor with invalid data (e.g., missing name).
     * This test verifies that a bad request status is returned for invalid input.
     */
    @Test
    void testSaveFloor_InvalidData_EmptyName() throws Exception {
        FloorDto newFloorDto = new FloorDto();
        newFloorDto.setFloorName(""); // Invalid name

        mockMvc.perform(withAuth(post("/api/v1/buildings/{buildingId}/floors", validBuildingId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newFloorDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Floor number must be provided")));
    }

    // -------------------- DELETE Requests --------------------

    /**
     * Test deleting a floor with a valid ID.
     * This test verifies that the floor is deleted and a no content status is returned.
     */
    @Test
    void testDeleteFloor_ValidId() throws Exception {
        mockMvc.perform(withAuth(delete("/api/v1/buildings/{buildingId}/floors/{floorId}", validBuildingId, validFloorId)))
                .andExpect(status().isNoContent());

        mockMvc.perform(withAuth(get("/api/v1/buildings/{buildingId}/floors/{floorId}", validBuildingId, validFloorId)))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Floor not found")));
    }

    /**
     * Test deleting a floor with an invalid floor ID.
     * This test verifies that a not found status is returned when the floor ID does not exist.
     */
    @Test
    void testDeleteFloor_InvalidId() throws Exception {
        mockMvc.perform(withAuth(delete("/api/v1/buildings/{buildingId}/floors/{floorId}", validBuildingId, "invalidFloorId")))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Floor not found")));
    }

    // -------------------- Helper Methods --------------------

    /**
     * Clear the database by deleting all floors.
     * This method is used to ensure a clean state for each test.
     */
    private void clearDatabase() {
        floorRepository.deleteAll();
        adminRepository.deleteAll();
    }

    /**
     * Create a building with the specified name.
     *
     * @param name The name of the building to create.
     * @return The created Building object.
     */
    private Building createBuilding(String name) {
        Building building = new Building();
        building.setName(name);
        // Set other properties as needed
        return buildingRepository.save(building);
    }

    /**
     * Create a floor with the specified name under a given building.
     *
     * @param buildingId The ID of the building the floor belongs to.
     * @param name       The name of the floor to create.
     * @return The created Floor object.
     */
    private Floor createFloor(String buildingId, String name) throws NotFoundResponseException {
        Floor floor = new Floor();
        floor.setFloorName(name);
        floor.setFloorNumber(1);
        Unit unit = new Unit();
        unit.setUnitNumber(101);
        unit.setNumberOfBedrooms(3);
        unit.setFloor(floor);
        floor.addUnit(unit);
        // Set other properties as needed
        return floorService.saveFloorAndUnits(buildingId, floor);
    }
}
