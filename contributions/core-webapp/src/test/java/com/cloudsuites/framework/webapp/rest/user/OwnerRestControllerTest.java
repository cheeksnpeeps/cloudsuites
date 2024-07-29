package com.cloudsuites.framework.webapp.rest.user;

import com.cloudsuites.framework.modules.property.features.repository.BuildingRepository;
import com.cloudsuites.framework.modules.property.features.repository.FloorRepository;
import com.cloudsuites.framework.modules.property.features.repository.UnitRepository;
import com.cloudsuites.framework.modules.property.personas.repository.OwnerRepository;
import com.cloudsuites.framework.services.property.features.entities.Building;
import com.cloudsuites.framework.services.property.features.entities.Floor;
import com.cloudsuites.framework.services.property.features.entities.Unit;
import com.cloudsuites.framework.services.property.personas.entities.Owner;
import com.cloudsuites.framework.services.user.entities.Address;
import com.cloudsuites.framework.services.user.entities.Identity;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class OwnerRestControllerTest {

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

    @BeforeEach
    void setUp() {
        clearDatabase();

        // Initialize test data
        validOwnerId1 = createOwner("test1").getOwnerId();
        validOwnerId2 = createOwner("test2").getOwnerId();
        validBuildingId1 = createBuilding("building1", "city1").getBuildingId();
        validUnitId1 = createUnit(validBuildingId1).getUnitId();
    }

    // -------------------- GET Requests --------------------

    @Test
    void testGetAllOwners() throws Exception {
        mockMvc.perform(get("/api/v1/owners"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    List<OwnerDto> ownerDtos = objectMapper.readValue(jsonResponse, objectMapper.getTypeFactory().constructCollectionType(List.class, OwnerDto.class));
                    assertThat(ownerDtos).hasSize(2);
                });
    }

    @Test
    void testGetOwnerById_ValidId() throws Exception {
        mockMvc.perform(get("/api/v1/owners/{ownerId}", validOwnerId1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    OwnerDto ownerDto = objectMapper.readValue(jsonResponse, OwnerDto.class);
                    assertThat(ownerDto.getIdentity().getUsername()).isEqualTo("test1");
                });
    }

    @Test
    void testGetOwnerById_InvalidId() throws Exception {
        mockMvc.perform(get("/api/v1/owners/{ownerId}", "invalidOwnerId"))
                .andExpect(status().isNotFound());
    }

    // -------------------- POST Requests --------------------

    @Test
    void testCreateOwner_ValidData() throws Exception {
        String newOwnerJson = "{\"identity\":{\"username\":\"newOwner\"}}";
        mockMvc.perform(post("/api/v1/owners")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newOwnerJson))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void testCreateOwner_InvalidData() throws Exception {
        String newOwnerJson = "{\"identity\":{\"username\":\"\"}}"; // Invalid username
        mockMvc.perform(post("/api/v1/owners")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newOwnerJson))
                .andExpect(status().isBadRequest());
    }

    // -------------------- PUT Requests --------------------

    @Test
    void testUpdateOwner_ValidData() throws Exception {
        String updatedOwnerJson = "{\"identity\":{\"username\":\"updatedOwner\"}}";
        mockMvc.perform(put("/api/v1/owners/{ownerId}", validOwnerId1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedOwnerJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    OwnerDto ownerDto = objectMapper.readValue(jsonResponse, OwnerDto.class);
                    assertThat(ownerDto.getIdentity().getUsername()).isEqualTo("updatedOwner");
                });
    }

    @Test
    void testUpdateOwner_InvalidId() throws Exception {
        String updatedOwnerJson = "{\"identity\":{\"username\":\"updatedOwner\"}}";
        mockMvc.perform(put("/api/v1/owners/{ownerId}", "invalidOwnerId")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedOwnerJson))
                .andExpect(status().isNotFound());
    }

    // -------------------- DELETE Requests --------------------

    @Test
    void testDeleteOwner_ValidId() throws Exception {
        mockMvc.perform(delete("/api/v1/owners/{ownerId}", validOwnerId1))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/owners/{ownerId}", validOwnerId1))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteOwner_InvalidId() throws Exception {
        mockMvc.perform(delete("/api/v1/owners/{ownerId}", "invalidOwnerId"))
                .andExpect(status().isNotFound());
    }

    // -------------------- Unit Management --------------------

    @Test
    void testAddUnitToOwner_ValidData() throws Exception {
        mockMvc.perform(post("/api/v1/owners/{ownerId}/buildings/{buildingId}/units/{unitId}/transfer", validOwnerId2, validBuildingId1, validUnitId1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    OwnerDto ownerDto = objectMapper.readValue(jsonResponse, OwnerDto.class);
                    assertThat(ownerDto.getUnits()).hasSize(1);
                });
    }

    @Test
    void testAddUnitToOwner_InvalidOwner() throws Exception {
        mockMvc.perform(post("/api/v1/owners/{ownerId}/buildings/{buildingId}/units/{unitId}/transfer", "invalidOwnerId", validBuildingId1, validUnitId1))
                .andExpect(status().isNotFound());
    }

    @Test
    void testRemoveUnitFromOwner_ValidData() throws Exception {
        // First, add the unit to the owner
        mockMvc.perform(post("/api/v1/owners/{ownerId}/buildings/{buildingId}/units/{unitId}/transfer", validOwnerId2, validBuildingId1, validUnitId1));

        // Now attempt to remove the unit
        mockMvc.perform(delete("/api/v1/owners/{ownerId}/units/{unitId}", validOwnerId2, validUnitId1))
                .andExpect(status().isOk());

        // Verify that the unit is no longer associated with the owner
        mockMvc.perform(get("/api/v1/owners/{ownerId}", validOwnerId2))
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
    }

    private Owner createOwner(String username) {
        Owner owner = new Owner();
        Identity identity = new Identity();
        identity.setUsername(username);
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

    // -------------------- Additional Edge Case Tests --------------------

    @Test
    void testCreateOwner_DuplicateUsername() throws Exception {
        String newOwnerJson = "{\"identity\":{\"username\":\"test1\"}}"; // Username already exists
        mockMvc.perform(post("/api/v1/owners")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newOwnerJson))
                .andExpect(status().isConflict()); // Assuming you handle duplicates with 409
    }

    @Test
    void testDeleteOwner_WithAssociatedUnits() throws Exception {
        // First, add a unit to validOwnerId1
        String newUnitId = createUnit(validBuildingId1).getUnitId();
        mockMvc.perform(post("/api/v1/owners/{ownerId}/buildings/{buildingId}/units/{unitId}/transfer", validOwnerId1, validBuildingId1, newUnitId));

        // Attempt to delete the owner while they still have associated units
        mockMvc.perform(delete("/api/v1/owners/{ownerId}", validOwnerId1))
                .andExpect(status().isConflict()); // Assuming your API returns 409 for conflict
    }

}
