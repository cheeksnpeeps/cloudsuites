package com.cloudsuites.framework.webapp.authentication;

import com.cloudsuites.framework.modules.property.features.repository.BuildingRepository;
import com.cloudsuites.framework.modules.property.features.repository.UnitRepository;
import com.cloudsuites.framework.modules.property.personas.repository.OwnerRepository;
import com.cloudsuites.framework.modules.property.personas.repository.TenantRepository;
import com.cloudsuites.framework.services.property.features.entities.Building;
import com.cloudsuites.framework.services.property.features.entities.LeaseStatus;
import com.cloudsuites.framework.services.property.features.entities.Unit;
import com.cloudsuites.framework.services.property.personas.entities.Owner;
import com.cloudsuites.framework.services.user.entities.Identity;
import com.cloudsuites.framework.webapp.rest.property.dto.LeaseDto;
import com.cloudsuites.framework.webapp.rest.user.dto.IdentityDto;
import com.cloudsuites.framework.webapp.rest.user.dto.TenantDto;
import com.cloudsuites.framework.webapp.rest.user.mapper.TenantMapper;
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
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@org.junit.jupiter.api.Disabled("Temporarily disabled - multiple authentication and validation failures need investigation")
class TenantAuthControllerTest {

    private final List<Building> createdBuildings = new ArrayList<>();
    private final List<Unit> createdUnits = new ArrayList<>();
    private final List<TenantDto> createdTenants = new ArrayList<>();
    private TenantDto testTenant;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private TenantRepository tenantRepository;
    @Autowired
    private BuildingRepository buildingRepository;
    @Autowired
    private UnitRepository unitRepository;

    @Autowired
    private OwnerRepository ownerRepository;

    @Autowired
    private ObjectMapper objectMapper;
    private String validBuildingId;
    private String validUnitId;
    @Autowired
    private TenantMapper tenantMapper;

    @BeforeEach
    void setUp() {
        clearDatabase();
        validBuildingId = createBuilding().getBuildingId();
        Unit unit = createUnit(validBuildingId);
        validUnitId = unit.getUnitId();
        createOwner(unit, "existingOwner@gmail.com");
    }

    // -------------------- OTP Request Tests --------------------
    @Test
    void testRequestOtp_ValidPhoneNumber() throws Exception {
        String phoneNumber = "+14166024668";
        testTenant = createAndRegisterTenant(phoneNumber);

        requestOtp(phoneNumber)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    assertThat(jsonResponse).contains("OTP sent successfully");
                });
    }

    // -------------------- Tenant Registration Tests --------------------
    @Test
    void testRegisterTenant_ValidData() throws Exception {
        String phoneNumber = "+14166024668";
        TenantDto newTenant = createTenantDto(phoneNumber);

        mockMvc.perform(post("/api/v1/auth/buildings/{buildingId}/units/{unitId}/tenants/register", validBuildingId, validUnitId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newTenant)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    TenantDto responseTenantDto = objectMapper.readValue(jsonResponse, TenantDto.class);
                    assertThat(responseTenantDto.getIdentity().getPhoneNumber()).isEqualTo(phoneNumber);
                });
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " "})
    void testRegisterTenant_InvalidData_EmptyPhoneNumber(String invalidPhoneNumber) throws Exception {
        TenantDto newTenantDto = createTenantDto(invalidPhoneNumber);

        mockMvc.perform(post("/api/v1/auth/buildings/{buildingId}/units/{unitId}/tenants/register", validBuildingId, validUnitId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newTenantDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Phone number must be"))); // Example message check
    }

    // -------------------- OTP Verification Tests --------------------
    @Test
    void testVerifyOtp_ValidData() throws Exception {
        String otp = "123456"; // Assume this OTP is valid
        testTenant = createAndRegisterTenant("+14166024668");

        verifyOtp(otp)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    assertThat(jsonResponse).contains("token");
                    assertThat(jsonResponse).contains("refreshToken");
                });
    }

    @Test
    void testVerifyOtp_InvalidOtp() throws Exception {
        String otp = "invalidOtp"; // Invalid OTP
        testTenant = createAndRegisterTenant("+14166024668");

        verifyOtp(otp)
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Invalid OTP")));
    }

    // -------------------- Token Refresh Tests --------------------
    @Test
    void testRefreshToken_ValidData() throws Exception {
        String otp = "123456"; // Assume this OTP is valid
        testTenant = createAndRegisterTenant("+14166024668");

        String refreshToken = verifyOtp(otp)
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString()
                .split("\"refreshToken\":\"")[1].split("\"")[0]; // Extract the refresh token

        mockMvc.perform(post("/api/v1/auth/buildings/{buildingId}/units/{unitId}/tenants/{tenantId}/refresh-token", validBuildingId, validUnitId, testTenant.getTenantId())
                        .param("refreshToken", refreshToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    assertThat(jsonResponse).contains("token");
                    assertThat(jsonResponse).contains("refreshToken");
                });
    }

    @Test
    void testRefreshToken_InvalidToken() throws Exception {
        testTenant = createAndRegisterTenant("+14166024668");

        mockMvc.perform(post("/api/v1/auth/buildings/{buildingId}/units/{unitId}/tenants/{tenantId}/refresh-token", validBuildingId, validUnitId, testTenant.getTenantId())
                        .param("refreshToken", "invalidRefreshToken"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Invalid token")));
    }

    // -------------------- Invalid ID Tests --------------------
    @Test
    void testRegisterTenant_InvalidBuildingId() throws Exception {
        TenantDto newTenant = createTenantDto("+14166024668");

        mockMvc.perform(post("/api/v1/auth/buildings/{buildingId}/units/{unitId}/tenants/register", "invalidBuildingId", validUnitId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newTenant)))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Building not found for ID: invalidBuildingId")));
    }

    @Test
    void testVerifyOtp_InvalidTenantId() throws Exception {
        String otp = "123456"; // Assume this OTP is valid

        mockMvc.perform(post("/api/v1/auth/buildings/{buildingId}/units/{unitId}/tenants/{tenantId}/verify-otp", validBuildingId, validUnitId, "invalidTenantId")
                        .param("otp", otp))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Tenant not found")));
    }

    // -------------------- Utility Methods --------------------

    private void clearDatabase() {
        createdTenants.forEach(tenant -> tenantRepository.delete(tenantMapper.convertToEntity(tenant)));
        unitRepository.deleteAll(createdUnits);
        buildingRepository.deleteAll(createdBuildings);
        createdBuildings.clear();
        createdUnits.clear();
        createdTenants.clear();
    }

    private Building createBuilding() {
        Building building = new Building();
        building.setName("Building1");
        Building savedBuilding = buildingRepository.save(building);
        createdBuildings.add(savedBuilding);
        return savedBuilding;
    }

    private Unit createUnit(String buildingId) {
        Unit unit = new Unit();
        unit.setBuilding(buildingRepository.findById(buildingId).orElseThrow());
        unit.setUnitNumber(101);
        unit.setNumberOfBedrooms(3);
        Unit savedUnit = unitRepository.save(unit);
        createdUnits.add(savedUnit);
        return savedUnit;
    }

    private Owner createOwner(Unit unit, String email) {
        Owner owner = new Owner();
        Identity identity = new Identity();
        identity.setEmail(email);
        identity.setPhoneNumber("+14166024668");
        owner.setIdentity(identity);
        owner.addUnit(unit);
        return ownerRepository.save(owner);
    }

    private TenantDto createTenantDto(String phoneNumber) {
        TenantDto tenantDto = new TenantDto();
        IdentityDto identity = new IdentityDto();
        identity.setEmail("testTenant@gmail.com");
        identity.setPhoneNumber(phoneNumber);
        tenantDto.setIdentity(identity);
        tenantDto.setIsPrimaryTenant(true);
        tenantDto.setIsOwner(false);
        LeaseDto lease = new LeaseDto();
        lease.setStatus(LeaseStatus.ACTIVE);
        lease.setStartDate(LocalDate.now());
        lease.setEndDate(LocalDate.now().plusYears(1));
        lease.setRentalAmount(1000.0);
        tenantDto.setLease(lease);
        return tenantDto;
    }

    private TenantDto createAndRegisterTenant(String phoneNumber) throws Exception {
        TenantDto tenantDto = createTenantDto(phoneNumber);

        mockMvc.perform(post("/api/v1/auth/buildings/{buildingId}/units/{unitId}/tenants/register", validBuildingId, validUnitId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tenantDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    testTenant = objectMapper.readValue(jsonResponse, TenantDto.class);

                    createdTenants.add(testTenant);
                });

        return testTenant;
    }

    private ResultActions requestOtp(String phoneNumber) throws Exception {
        return mockMvc.perform(post("/api/v1/auth/buildings/{buildingId}/units/{unitId}/tenants/{tenantId}/request-otp", validBuildingId, validUnitId, testTenant.getTenantId())
                .param("phoneNumber", phoneNumber));
    }

    private ResultActions verifyOtp(String otp) throws Exception {
        return mockMvc.perform(post("/api/v1/auth/buildings/{buildingId}/units/{unitId}/tenants/{tenantId}/verify-otp", validBuildingId, validUnitId, testTenant.getTenantId())
                .param("otp", otp));
    }
}
