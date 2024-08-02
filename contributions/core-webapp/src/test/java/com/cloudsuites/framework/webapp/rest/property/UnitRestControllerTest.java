package com.cloudsuites.framework.webapp.rest.property;

import com.cloudsuites.framework.services.property.features.entities.Building;
import com.cloudsuites.framework.services.property.features.entities.Unit;
import com.cloudsuites.framework.services.property.features.service.BuildingService;
import com.cloudsuites.framework.services.property.features.service.UnitService;
import com.cloudsuites.framework.services.user.entities.Address;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UnitRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BuildingService buildingService;

    @Autowired
    private UnitService unitService;

    private Building building;

    @BeforeEach
    void setUp() {
        // Create a Building entity for testing
        building = new Building();
        building.setName("Test Building");
        building.setAddress(createMockAddressEntity());
        building = buildingService.saveBuilding(building);
    }

    @Test
    void testGetAllUnits() throws Exception {
        // Create multiple Unit entities
        Unit unit1 = new Unit();
        unit1.setUnitNumber(101);
        unit1.setBuilding(building);
        unitService.saveUnit(unit1);

        Unit unit2 = new Unit();
        unit2.setUnitNumber(102);
        unit2.setBuilding(building);
        unitService.saveUnit(unit2);

        mockMvc.perform(get("/api/v1/buildings/{buildingId}/units", building.getBuildingId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].unitNumber").value(101))
                .andExpect(jsonPath("$[1].unitNumber").value(102));
    }

    @Test
    void testGetUnitById() throws Exception {
        // Create a Unit entity
        Unit unit = new Unit();
        unit.setUnitNumber(102);
        unit.setBuilding(building);
        unit = unitService.saveUnit(unit); // Save and retrieve the unit

        mockMvc.perform(get("/api/v1/buildings/{buildingId}/units/{unitId}", building.getBuildingId(), unit.getUnitId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.unitNumber").value(102));
    }

    @Test
    void testGetUnitById_NotFound() throws Exception {
        String unitId = "non-existing-unit";

        mockMvc.perform(get("/api/v1/buildings/{buildingId}/units/{unitId}", building.getBuildingId(), unitId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteUnitById() throws Exception {
        // Create a Unit entity
        Unit unit = new Unit();
        unit.setUnitNumber(103);
        unit.setBuilding(building);
        unit = unitService.saveUnit(unit); // Save the unit

        mockMvc.perform(delete("/api/v1/buildings/{buildingId}/units/{unitId}", building.getBuildingId(), unit.getUnitId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteUnitById_NotFound() throws Exception {
        String unitId = "non-existing-unit";

        mockMvc.perform(delete("/api/v1/buildings/{buildingId}/units/{unitId}", building.getBuildingId(), unitId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    /**
     * Create a mock Address entity.
     *
     * @return The mock Address entity.
     */
    private Address createMockAddressEntity() {
        Address address = new Address();
        address.setAptNumber("Apt 101");
        address.setStreetNumber("123");
        address.setStreetName("Main St");
        address.setAddressLine2("Near Central Park");
        address.setCity("Toronto");
        address.setStateProvinceRegion("Ontario");
        address.setPostalCode("M1M 1M1");
        address.setCountry("Canada");
        address.setLatitude(43.6532);
        address.setLongitude(-79.3832);
        return address;
    }
}