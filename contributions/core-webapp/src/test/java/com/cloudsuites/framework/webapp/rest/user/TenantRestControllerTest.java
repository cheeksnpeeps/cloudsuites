package com.cloudsuites.framework.webapp.rest.user;

import com.cloudsuites.framework.modules.property.features.repository.BuildingRepository;
import com.cloudsuites.framework.modules.property.features.repository.UnitRepository;
import com.cloudsuites.framework.modules.property.personas.repository.OwnerRepository;
import com.cloudsuites.framework.modules.property.personas.repository.TenantRepository;
import com.cloudsuites.framework.modules.user.repository.AdminRepository;
import com.cloudsuites.framework.services.common.exception.NotFoundResponseException;
import com.cloudsuites.framework.services.common.exception.UserAlreadyExistsException;
import com.cloudsuites.framework.services.property.features.entities.Building;
import com.cloudsuites.framework.services.property.features.entities.Unit;
import com.cloudsuites.framework.services.property.personas.entities.Owner;
import com.cloudsuites.framework.services.property.personas.entities.OwnerStatus;
import com.cloudsuites.framework.services.property.personas.entities.Tenant;
import com.cloudsuites.framework.services.property.personas.entities.TenantStatus;
import com.cloudsuites.framework.services.property.personas.service.TenantService;
import com.cloudsuites.framework.services.user.UserService;
import com.cloudsuites.framework.services.user.entities.Identity;
import com.cloudsuites.framework.webapp.authentication.utils.AdminTestHelper;
import com.cloudsuites.framework.webapp.rest.user.dto.TenantDto;
import com.cloudsuites.framework.webapp.rest.user.mapper.TenantMapper;
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
class TenantRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TenantService tenantService;

    @Autowired
    private TenantMapper tenantMapper;

    @Autowired
    private BuildingRepository buildingRepository;

    @Autowired
    private UnitRepository unitRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TenantRepository tenantRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private OwnerRepository ownerRepository;

    private String validTenantId;
    private String validBuildingId;
    private String validUnitId;
    private Tenant testTenant;

    private AdminTestHelper adminTestHelper;
    private String accessToken;

    @BeforeEach
    void setUp() throws Exception {
        clearDatabase();
        validBuildingId = createBuilding("BuildingA").getBuildingId();
        validUnitId = createUnit(validBuildingId).getUnitId();
        testTenant = createTenant(validUnitId);
        validTenantId = testTenant.getTenantId();

        adminTestHelper = new AdminTestHelper(mockMvc, objectMapper, validBuildingId, validUnitId);
        accessToken = adminTestHelper.registerAdminAndGetToken("testRegisterAdmin", "+14166024668");
    }

    private MockHttpServletRequestBuilder withAuth(MockHttpServletRequestBuilder requestBuilder) {
        return requestBuilder.header("Authorization", "Bearer " + accessToken);
    }
    // -------------------- GET Requests --------------------

    @Test
    void listTenantsByBuildingId_shouldReturnTenants() throws Exception {
        mockMvc.perform(withAuth(get("/api/v1/buildings/{buildingId}/tenants", validBuildingId)
                        .param("status", "ACTIVE")
                        .contentType(MediaType.APPLICATION_JSON)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    List<TenantDto> tenantDtos = objectMapper.readValue(jsonResponse, objectMapper.getTypeFactory().constructCollectionType(List.class, TenantDto.class));
                    assertThat(tenantDtos).hasSize(1);
                    assertThat(tenantDtos.get(0).getTenantId()).isEqualTo(validTenantId); // Check tenant ID
                });
    }

    @Test
    void getTenantById_shouldReturnTenant() throws Exception {
        mockMvc.perform(withAuth(get("/api/v1/buildings/{buildingId}/units/{unitId}/tenants/{tenantId}", validBuildingId, validUnitId, validTenantId)
                        .contentType(MediaType.APPLICATION_JSON)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    TenantDto tenantDto = objectMapper.readValue(jsonResponse, TenantDto.class);
                    assertThat(tenantDto.getTenantId()).isEqualTo(validTenantId); // Check the tenant ID
                    assertThat(tenantDto.getStatus()).isEqualTo(TenantStatus.ACTIVE); // Check the tenant status
                });
    }

    @Test
    void getTenantById_invalidId_shouldReturnNotFound() throws Exception {
        mockMvc.perform(withAuth(get("/api/v1/buildings/{buildingId}/units/{unitId}/tenants/{tenantId}", validBuildingId, validUnitId, "invalidTenantId")
                        .contentType(MediaType.APPLICATION_JSON)))
                .andExpect(status().isNotFound());
    }

    // -------------------- POST Requests --------------------

    @Test
    void createTenant_shouldReturnCreatedTenant() throws Exception {
        String newTenantJson = "{\"identity\":{\"email\":\"newTenant@gmail.com\"},\"lease\":{\"status\":\"ACTIVE\",\"startDate\":\"2024-08-21\",\"endDate\":\"2025-08-21\",\"rentalAmount\":1000.0}}"; // New tenant data

        mockMvc.perform(withAuth(post("/api/v1/buildings/{buildingId}/units/{unitId}/tenants", validBuildingId, validUnitId)
                        .contentType(MediaType.APPLICATION_JSON))
                        .content(newTenantJson))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    TenantDto tenantDto = objectMapper.readValue(jsonResponse, TenantDto.class);
                    assertThat(tenantDto.getIdentity().getEmail()).isEqualTo("newTenant@gmail.com"); // Validate the created tenant username
                });
    }

    @Test
    void createTenant_withDuplicateEmail_shouldReturnConflict() throws Exception {
        String newTenantJson = "{\"identity\":{\"username\":\"TenantA\",\"email\":\"tenantA@gmail.com\"}}"; // Username already exists

        mockMvc.perform(withAuth(post("/api/v1/buildings/{buildingId}/units/{unitId}/tenants", validBuildingId, validUnitId)
                        .contentType(MediaType.APPLICATION_JSON))
                        .content(newTenantJson))
                .andExpect(status().isConflict())
                .andExpect(content().string(containsString("User already exists"))); // Example error message
    }

    @Test
    void createTenant_withEmptyEmail_shouldReturnBadRequest() throws Exception {
        String newTenantJson = "{\"identity\":{\"email\":\"\"}}"; // Invalid username

        mockMvc.perform(withAuth(post("/api/v1/buildings/{buildingId}/units/{unitId}/tenants", validBuildingId, validUnitId)
                        .contentType(MediaType.APPLICATION_JSON))
                        .content(newTenantJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Email must be between"))); // Example error message
    }

    // -------------------- PUT Requests --------------------

    @Test
    void updateTenant_shouldReturnUpdatedTenant() throws Exception {
        TenantDto updatedTenantDto = new TenantDto();
        updatedTenantDto.setTenantId(validTenantId);
        updatedTenantDto.setStatus(TenantStatus.INACTIVE); // Update the status or other fields as needed

        mockMvc.perform(withAuth(put("/api/v1/buildings/{buildingId}/units/{unitId}/tenants/{tenantId}", validBuildingId, validUnitId, validTenantId)
                        .content(objectMapper.writeValueAsString(updatedTenantDto))
                        .contentType(MediaType.APPLICATION_JSON)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    TenantDto tenantDto = objectMapper.readValue(jsonResponse, TenantDto.class);
                    assertThat(tenantDto.getStatus()).isEqualTo(TenantStatus.INACTIVE); // Check the updated status
                });
    }

    @Test
    void updateTenant_invalidId_shouldReturnNotFound() throws Exception {
        TenantDto updatedTenantDto = new TenantDto();
        updatedTenantDto.setTenantId("invalidTenantId");
        updatedTenantDto.setStatus(TenantStatus.INACTIVE);

        mockMvc.perform(withAuth(put("/api/v1/buildings/{buildingId}/units/{unitId}/tenants/{tenantId}", validBuildingId, validUnitId, "invalidTenantId")
                        .content(objectMapper.writeValueAsString(updatedTenantDto))
                        .contentType(MediaType.APPLICATION_JSON)))
                .andExpect(status().isNotFound());
    }

    // -------------------- DELETE Requests --------------------

    @Test
    void deleteTenant_shouldReturnSuccessMessage() throws Exception {
        mockMvc.perform(withAuth(delete("/api/v1/buildings/{buildingId}/units/{unitId}/tenants/{tenantId}", validBuildingId, validUnitId, validTenantId)))
                .andExpect(status().isOk());

        // Verify that the tenant status is INACTIVE after deletion
        mockMvc.perform(withAuth(get("/api/v1/buildings/{buildingId}/units/{unitId}/tenants/{tenantId}", validBuildingId, validUnitId, validTenantId)))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    TenantDto tenantDto = objectMapper.readValue(jsonResponse, TenantDto.class);
                    assertThat(tenantDto.getStatus()).isEqualTo(TenantStatus.INACTIVE); // Check the tenant status is inactive
                });
    }

    @Test
    void deleteTenant_invalidId_shouldReturnNotFound() throws Exception {
        mockMvc.perform(withAuth(delete("/api/v1/buildings/{buildingId}/units/{unitId}/tenants/{tenantId}", validBuildingId, validUnitId, "invalidTenantId")))
                .andExpect(status().isNotFound());
    }

    // -------------------- Helper Methods --------------------

    private void clearDatabase() {
        tenantRepository.deleteAll();
        unitRepository.deleteAll();
        buildingRepository.deleteAll();
        adminRepository.deleteAll();
    }

    private Tenant createTenant(String unitId) throws UserAlreadyExistsException {
        Tenant tenant = new Tenant();
        Identity identity = new Identity();
        identity.setEmail("tenantA@gmail.com");
        identity = userService.createUser(identity);
        tenant.setIdentity(identity);

        Unit unit = unitRepository.findById(unitId).orElseThrow();
        tenant.setUnit(unit);
        tenant.setStatus(TenantStatus.ACTIVE);
        tenant.setBuilding(unit.getBuilding());
        tenant = tenantRepository.save(tenant);

        Owner owner = new Owner();
        Identity identityO = new Identity();
        identityO.setEmail("owner@gmail.com");
        identityO = userService.createUser(identityO);
        owner.setIdentity(identityO);

        owner.addUnit(unit);
        owner.setStatus(OwnerStatus.ACTIVE);
        ownerRepository.save(owner);

        unit.addTenant(tenant);
        unitRepository.save(unit);

        try {
            List<Tenant> tenants = tenantService.getAllTenantsByBuilding(unit.getBuilding().getBuildingId(), TenantStatus.ACTIVE);
            assertThat(tenants).hasSize(1); // Verify tenant count
        } catch (NotFoundResponseException e) {
            throw new RuntimeException(e);
        }
        return tenant;
    }

    private Building createBuilding(String name) {
        Building building = new Building();
        building.setName(name);
        return buildingRepository.save(building);
    }

    private Unit createUnit(String buildingId) {
        Unit unit = new Unit();
        Building building = buildingRepository.findById(buildingId).orElseThrow();
        unit.setBuilding(building);
        return unitRepository.save(unit);
    }
}
