package com.cloudsuites.framework.webapp.rest.user;
import com.cloudsuites.framework.modules.property.features.repository.BuildingRepository;
import com.cloudsuites.framework.modules.property.features.repository.FloorRepository;
import com.cloudsuites.framework.modules.property.features.repository.UnitRepository;
import com.cloudsuites.framework.modules.property.personas.repository.OwnerRepository;
import com.cloudsuites.framework.modules.user.repository.AdminRepository;
import com.cloudsuites.framework.services.property.features.entities.Building;
import com.cloudsuites.framework.services.property.features.entities.Floor;
import com.cloudsuites.framework.services.property.features.entities.Unit;
import com.cloudsuites.framework.services.property.personas.entities.Owner;
import com.cloudsuites.framework.services.user.entities.Address;
import com.cloudsuites.framework.services.user.entities.Identity;
import com.cloudsuites.framework.webapp.authentication.utils.AdminTestHelper;
import com.cloudsuites.framework.webapp.rest.user.dto.OwnerDto;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class OwnerRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OwnerRepository ownerRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BuildingRepository buildingRepository;

    @Autowired
    private UnitRepository unitRepository;

    @Autowired
    private FloorRepository floorRepository;

    private String validOwnerId1;
    private String validOwnerId2;
    private String validUnitId1;
    private String validBuildingId1;

    private AdminTestHelper adminTestHelper;
    private String accessToken;
    @Autowired
    private AdminRepository adminRepository;

    @BeforeEach
    void setUp() throws Exception {
        clearDatabase();

        // Initialize test data
        validOwnerId1 = createOwner("test1").getOwnerId();
        validOwnerId2 = createOwner("test2").getOwnerId();
        validBuildingId1 = createBuilding("building1", "city1").getBuildingId();
        validUnitId1 = createUnit(validBuildingId1).getUnitId();
        adminTestHelper = new AdminTestHelper(mockMvc, objectMapper, null, null);
        accessToken = adminTestHelper.registerAdminAndGetToken("testRegisterAdmin", "+14166024668");
    }

    private MockHttpServletRequestBuilder withAuth(MockHttpServletRequestBuilder requestBuilder) {
        return requestBuilder.header("Authorization", "Bearer " + accessToken);
    }

    // -------------------- GET Requests --------------------

    /**
     * Test to retrieve all owners.
     * It expects a successful response and verifies that the size of the returned owner list is 2.
     */
    @Test
    void testGetAllOwners() throws Exception {
        mockMvc.perform(withAuth(get("/api/v1/owners")))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    List<OwnerDto> ownerDtos = objectMapper.readValue(jsonResponse, objectMapper.getTypeFactory().constructCollectionType(List.class, OwnerDto.class));
                    assertThat(ownerDtos).hasSize(2);
                });
    }

    /**
     * Test to retrieve an owner by a valid ID.
     * It expects a successful response and verifies that the owner's username matches the expected value.
     */
    @Test
    void testGetOwnerById_ValidId() throws Exception {
        mockMvc.perform(withAuth(get("/api/v1/owners/{ownerId}", validOwnerId1)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    OwnerDto ownerDto = objectMapper.readValue(jsonResponse, OwnerDto.class);
                    assertThat(ownerDto.getIdentity().getEmail()).isEqualTo("test1@gmail.com");
                });
    }

    /**
     * Test to retrieve an owner by an invalid ID.
     * It expects a 404 Not Found response.
     */
    @Test
    void testGetOwnerById_InvalidId() throws Exception {
        mockMvc.perform(withAuth(get("/api/v1/owners/{ownerId}", "invalidOwnerId")))
                .andExpect(status().isNotFound());
    }

    // -------------------- POST Requests --------------------

    /**
     * Test to create a new owner with valid data.
     * It expects a successful creation response and verifies that the content type is JSON.
     */
    @Test
    void testCreateOwner_ValidData() throws Exception {
        String newOwnerJson = "{\"identity\":{\"username\":\"newOwner\", \"email\":\"newOwner@company.com\"}}";
        mockMvc.perform(withAuth(post("/api/v1/owners"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newOwnerJson))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    /**
     * Test to create a new owner with an empty username.
     * It expects a 400 Bad Request response and checks for an appropriate error message.
     */
    @Test
    void testCreateOwner_InvalidData_EmptyEmail() throws Exception {
        String newOwnerJson = "{\"identity\":{\"username\":\"\", \"email\":\"\"}}"; // Invalid username
        mockMvc.perform(withAuth(post("/api/v1/owners"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newOwnerJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Email must be between 5 and 50 characters long"))); // Example message check
    }

    /**
     * Test to create a new owner with a null username.
     * It expects a 409 Conflict response and checks for an appropriate error message.
     */
    @Test
    void testCreateOwner_InvalidData_NullUsername() throws Exception {
        String newOwnerJson = "{\"identity\":{\"username\":null, \"email\":null}}"; // Null username
        mockMvc.perform(withAuth(post("/api/v1/owners"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newOwnerJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Email is mandatory"))); // Example message check
    }

    /**
     * Test to create a new owner with a username that already exists.
     * It expects a 409 Conflict response and checks for an appropriate error message.
     */
    @Test
    void testCreateOwner_DuplicateEmail() throws Exception {
        String newOwnerJson = "{\"identity\":{\"username\":\"test1\", \"email\":\"test1@gmail.com\"}}"; // Username already exists
        mockMvc.perform(withAuth(post("/api/v1/owners"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newOwnerJson))
                .andExpect(status().isConflict())
                .andExpect(content().string(containsString("User already exists"))); // Example message check
    }

    // -------------------- PUT Requests --------------------

    /**
     * Test to update an existing owner with valid data.
     * It expects a successful response and verifies that the owner's username has been updated correctly.
     */
    @Test
    void testUpdateOwner_ValidData() throws Exception {
        String updatedOwnerJson = "{\"identity\":{\"username\":\"updatedOwner\", \"email\":\"valid@company.com\"}}";
        mockMvc.perform(withAuth(put("/api/v1/owners/{ownerId}", validOwnerId1))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedOwnerJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    OwnerDto ownerDto = objectMapper.readValue(jsonResponse, OwnerDto.class);
                    assertThat(ownerDto.getIdentity().getEmail()).isEqualTo("valid@company.com");
                });
    }

    /**
     * Test to update an owner with an invalid ID.
     * It expects a 404 Not Found response.
     */
    @Test
    void testUpdateOwner_InvalidId() throws Exception {
        String updatedOwnerJson = "{\"identity\":{\"username\":\"updatedOwner\", \"email\":\"updated@company.com\"}}";
        mockMvc.perform(withAuth(put("/api/v1/owners/{ownerId}", "invalidOwnerId"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedOwnerJson))
                .andExpect(status().isNotFound());
    }

    /**
     * Test to update an owner with an empty username.
     * It expects a 400 Bad Request response and checks for an appropriate error message.
     */
    @Test
    void testUpdateOwner_InvalidData_EmptyEmail() throws Exception {
        String updatedOwnerJson = "{\"identity\":{\"username\":\"\", \"email\":\"\"}}"; // Invalid username
        mockMvc.perform(withAuth(put("/api/v1/owners/{ownerId}", validOwnerId1))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedOwnerJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Email must be between 5 and 50 characters long"))); // Example message check
    }

    // -------------------- DELETE Requests --------------------

    /**
     * Test to delete an owner with a valid ID.
     * It expects a successful deletion response and verifies that the owner no longer exists.
     */
    @Test
    void testDeleteOwner_ValidId() throws Exception {
        mockMvc.perform(withAuth(delete("/api/v1/owners/{ownerId}", validOwnerId1)))
                .andExpect(status().isNoContent());

        mockMvc.perform(withAuth(get("/api/v1/owners/{ownerId}", validOwnerId1)))
                .andExpect(status().isNotFound());
    }

    /**
     * Test to delete an owner with an invalid ID.
     * It expects a 404 Not Found response.
     */
    @Test
    void testDeleteOwner_InvalidId() throws Exception {
        mockMvc.perform(withAuth(delete("/api/v1/owners/{ownerId}", "invalidOwnerId")))
                .andExpect(status().isNotFound());
    }

    /**
     * Test to delete an owner that has associated units.
     * It expects a 409 Conflict response and checks for an appropriate error message.
     */
    @Test
    void testDeleteOwner_WithAssociatedUnits() throws Exception {
        // First, add a unit to validOwnerId1
        String newUnitId = createUnit(validBuildingId1).getUnitId();
        mockMvc.perform(withAuth(post("/api/v1/owners/{ownerId}/buildings/{buildingId}/units/{unitId}/transfer", validOwnerId1, validBuildingId1, newUnitId)));

        // Attempt to delete the owner while they still have associated units
        mockMvc.perform(withAuth(delete("/api/v1/owners/{ownerId}", validOwnerId1)))
                .andExpect(status().isConflict()) // Assuming your API returns 409 for conflict
                .andExpect(content().string(containsString("Owner has units. Cannot delete owner with units."))); // Example message check
    }

    // -------------------- Unit Management --------------------

    /**
     * Test to add a unit to an owner with valid data.
     * It expects a successful response and verifies that the owner now has one unit associated.
     */
    @Test
    void testAddUnitToOwner_ValidData() throws Exception {
        mockMvc.perform(withAuth(post("/api/v1/owners/{ownerId}/buildings/{buildingId}/units/{unitId}/transfer", validOwnerId2, validBuildingId1, validUnitId1)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    OwnerDto ownerDto = objectMapper.readValue(jsonResponse, OwnerDto.class);
                    assertThat(ownerDto.getUnits()).hasSize(1);
                });
    }

    /**
     * Test to add a unit to an invalid owner.
     * It expects a 404 Not Found response.
     */
    @Test
    void testAddUnitToOwner_InvalidOwner() throws Exception {
        mockMvc.perform(withAuth(post("/api/v1/owners/{ownerId}/buildings/{buildingId}/units/{unitId}/transfer", "invalidOwnerId", validBuildingId1, validUnitId1)))
                .andExpect(status().isNotFound());
    }

    /**
     * Test to add a unit using an invalid unit ID.
     * It expects a 404 Not Found response and checks for an appropriate error message.
     */
    @Test
    void testAddUnitToOwner_InvalidUnit() throws Exception {
        mockMvc.perform(withAuth(post("/api/v1/owners/{ownerId}/buildings/{buildingId}/units/{unitId}/transfer", validOwnerId2, validBuildingId1, "invalidUnitId")))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Unit not found"))); // Example message check
    }

    /**
     * Test to remove a unit from an owner with valid data.
     * It first adds a unit to the owner, then expects a successful response for the removal,
     * and verifies that the owner no longer has any units associated.
     */
    @Test
    void testRemoveUnitFromOwner_ValidData() throws Exception {
        // First, add the unit to the owner
        mockMvc.perform(withAuth(post("/api/v1/owners/{ownerId}/buildings/{buildingId}/units/{unitId}/transfer", validOwnerId2, validBuildingId1, validUnitId1)));

        // Now attempt to remove the unit
        mockMvc.perform(withAuth(delete("/api/v1/owners/{ownerId}/units/{unitId}", validOwnerId2, validUnitId1)))
                .andExpect(status().isOk());

        // Verify that the unit is no longer associated with the owner
        mockMvc.perform(withAuth(get("/api/v1/owners/{ownerId}", validOwnerId2)))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    OwnerDto ownerDto = objectMapper.readValue(jsonResponse, OwnerDto.class);
                    assertThat(ownerDto.getUnits()).isEmpty();
                });
    }

    // -------------------- Helper Methods --------------------

    private void clearDatabase() {
        ownerRepository.deleteAll();
        buildingRepository.deleteAll();
        unitRepository.deleteAll();
        adminRepository.deleteAll();
    }

    private Owner createOwner(String username) {
        Owner owner = new Owner();
        Identity identity = new Identity();
        identity.setEmail(username + "@gmail.com");
        owner.setIdentity(identity);
        return ownerRepository.save(owner);
    }

    private Building createBuilding(String name, String city) {
        Building building = new Building();
        building.setName(name);
        Address address = new Address();
        address.setCity(city);
        building.setAddress(address);
        return buildingRepository.save(building);
    }

    private Unit createUnit(String buildingId) {
        Building building = buildingRepository.findById(buildingId).orElseThrow();
        Unit unit = new Unit();
        Floor floor = new Floor();
        floor.setFloorName("floor");
        unit.setFloor(floorRepository.save(floor));
        unit.setBuilding(building);
        return unitRepository.save(unit);
    }
}