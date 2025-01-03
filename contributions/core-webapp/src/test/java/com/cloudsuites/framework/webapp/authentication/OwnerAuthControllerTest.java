package com.cloudsuites.framework.webapp.authentication;

import com.cloudsuites.framework.modules.property.features.repository.BuildingRepository;
import com.cloudsuites.framework.modules.property.features.repository.UnitRepository;
import com.cloudsuites.framework.modules.property.personas.repository.OwnerRepository;
import com.cloudsuites.framework.modules.property.personas.repository.TenantRepository;
import com.cloudsuites.framework.modules.user.repository.UserRoleRepository;
import com.cloudsuites.framework.services.property.features.entities.Building;
import com.cloudsuites.framework.services.property.features.entities.Unit;
import com.cloudsuites.framework.services.property.personas.entities.Owner;
import com.cloudsuites.framework.services.user.entities.Identity;
import com.cloudsuites.framework.webapp.rest.user.dto.IdentityDto;
import com.cloudsuites.framework.webapp.rest.user.dto.OwnerDto;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class OwnerAuthControllerTest {

    private Owner testOwner;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OwnerRepository ownerRepository;

    @Autowired
    private BuildingRepository buildingRepository;

    @Autowired
    private UnitRepository unitRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private String validOwnerId;
    private String validUnitId;
    private String validBuildingId;
    @Autowired
    private TenantRepository tenantRepository;
    @Autowired
    private UserRoleRepository userRoleRepository;

    @BeforeEach
    void setUp() {
        clearDatabase();

        // Initialize test data
        this.testOwner = createOwner("testOwner@gmail.com");
        validOwnerId = testOwner.getOwnerId();
        validBuildingId = createBuilding("Building1", "City1").getBuildingId();
        validUnitId = createUnit(validBuildingId).getUnitId();
        unitRepository.findById(validUnitId).ifPresent(
                unit -> {
                    unit.setOwner(ownerRepository.findById(validOwnerId).orElseThrow());
                    unitRepository.save(unit);
                }
        );
    }

    // -------------------- Registration Tests --------------------

    /**
     * Test to register a new owner with valid data.
     * It expects a successful response and verifies the returned owner's details.
     */
    @Test
    void testRegisterOwner_ValidData() throws Exception {
        OwnerDto newTestOwner = new OwnerDto();
        IdentityDto identity = new IdentityDto();
        identity.setEmail("testRegisterOwnerV@gmail.com");
        identity.setPhoneNumber("+14166024668");
        newTestOwner.setIdentity(identity);

        mockMvc.perform(post("/api/v1/auth/buildings/{buildingId}/units/{unitId}/owner/register", validBuildingId, validUnitId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newTestOwner)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    OwnerDto responseOwnerDto = objectMapper.readValue(jsonResponse, OwnerDto.class);
                    assertThat(responseOwnerDto.getIdentity().getEmail()).isEqualTo("testRegisterOwnerV@gmail.com");
                });
    }

    /**
     * Test to register an owner with an empty username.
     * It expects a 400 Bad Request response with an appropriate error message.
     */
    @ParameterizedTest
    @ValueSource(strings = {"", " "})
    void testRegisterOwner_InvalidData_EmptyUsername(String invalidUsername) throws Exception {
        OwnerDto newOwnerDto = new OwnerDto();
        IdentityDto identity = new IdentityDto();
        identity.setEmail("invalidEmail");
        newOwnerDto.setIdentity(identity);

        mockMvc.perform(post("/api/v1/auth/buildings/{buildingId}/units/{unitId}/owner/register", validBuildingId, validUnitId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newOwnerDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Email should be valid"))); // Example message check
    }

    /**
     * Test to register an owner with a username that already exists.
     * It expects a 409 Conflict response with an appropriate error message.
     */
    @Test
    void testRegisterOwner_DuplicateUsername() throws Exception {
        OwnerDto newOwnerDto = new OwnerDto();
        IdentityDto identity = new IdentityDto();
        identity.setEmail("testOwner@gmail.com");
        newOwnerDto.setIdentity(identity);

        // Try to register again with the same username
        mockMvc.perform(post("/api/v1/auth/buildings/{buildingId}/units/{unitId}/owner/register", validBuildingId, validUnitId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newOwnerDto)))
                .andExpect(status().isConflict())
                .andExpect(content().string(containsString("User already exists"))); // Example message check
    }

    // -------------------- OTP Verification Tests --------------------

    /**
     * Test to verify OTP for a registered owner.
     * It expects a successful response and verifies the returned tokens.
     */
    @Test
    void testVerifyOtp_ValidData() throws Exception {
        String otp = "123456"; // Assume this OTP is valid

        // Assuming the OTP verification is part of your controller logic
        mockMvc.perform(post("/api/v1/auth/buildings/{buildingId}/units/{unitId}/owners/{ownerId}/verify-otp", validBuildingId, validUnitId, validOwnerId)
                        .param("otp", otp))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    // Assuming the response has tokens
                    assertThat(jsonResponse).contains("token");
                    assertThat(jsonResponse).contains("refreshToken");
                });
    }

    /**
     * Test to verify OTP with an invalid OTP.
     * It expects a 400 Bad Request response with an appropriate error message.
     */
    @Test
    void testVerifyOtp_InvalidOtp() throws Exception {
        String otp = "invalidOtp"; // Invalid OTP

        mockMvc.perform(post("/api/v1/auth/buildings/{buildingId}/units/{unitId}/owners/{ownerId}/verify-otp", validBuildingId, validUnitId, validOwnerId)
                        .param("otp", otp))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Invalid OTP provided")));
    }

    // -------------------- Token Refresh Tests --------------------

    /**
     * Test to refresh the token using a valid refresh token.
     * It expects a successful response with new tokens.
     */
    @Test
    void testRefreshToken_ValidData() throws Exception {
        String otp = "123456"; // Assume this OTP is valid
        // Assuming the OTP verification is part of your controller logic
        mockMvc.perform(post("/api/v1/auth/buildings/{buildingId}/units/{unitId}/owners/{ownerId}/verify-otp", validBuildingId, validUnitId, validOwnerId)
                        .param("otp", otp))
                // Extract the refresh token from the response
                .andExpect(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    // Extract the refresh token from the response
                    String refreshToken = objectMapper.readTree(jsonResponse).get("refreshToken").asText();

                    // Use the refresh token to refresh the token
                    mockMvc.perform(post("/api/v1/auth/buildings/{buildingId}/units/{unitId}/owners/{ownerId}/refresh-token",
                                    validBuildingId, validUnitId, validOwnerId)
                                    .param("refreshToken", refreshToken))
                            .andExpect(status().isOk())
                            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                            .andExpect(result2 -> {
                                String jsonResponse2 = result2.getResponse().getContentAsString();
                                assertThat(jsonResponse2).contains("token");
                                assertThat(jsonResponse2).contains("refreshToken");
                            });
                });
    }

    /**
     * Test to refresh the token using an invalid refresh token.
     * It expects a 400 Bad Request response with an appropriate error message.
     */
    @Test
    void testRefreshToken_InvalidToken() throws Exception {
        String refreshToken = "invalidRefreshToken"; // Invalid refresh token
        mockMvc.perform(post("/api/v1/auth/buildings/{buildingId}/units/{unitId}/owners/{ownerId}/refresh-token", validBuildingId, validUnitId, validOwnerId)
                        .param("refreshToken", refreshToken))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Invalid token")));
    }

    // -------------------- Invalid ID Tests --------------------

    /**
     * Test to register an owner with an invalid building ID.
     * It expects a 404 Not Found response with an appropriate error message.
     */
    @Test
    void testRegisterOwner_InvalidBuildingId() throws Exception {
        OwnerDto newTestOwner = new OwnerDto();
        IdentityDto identity = new IdentityDto();
        identity.setEmail("testRegisterOwnerV@gmail.com");
        identity.setPhoneNumber("+14166024668");
        newTestOwner.setIdentity(identity);

        mockMvc.perform(post("/api/v1/auth/buildings/{buildingId}/units/{unitId}/owner/register", "invalidBuildingId", validUnitId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newTestOwner)))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Unit not found"))); // Example message check
    }

    /**
     * Test to verify OTP for a non-existent owner.
     * It expects a 404 Not Found response with an appropriate error message.
     */
    @Test
    void testVerifyOtp_InvalidOwnerId() throws Exception {
        String otp = "123456"; // Assume this OTP is valid

        mockMvc.perform(post("/api/v1/auth/buildings/{buildingId}/units/{unitId}/owners/{ownerId}/verify-otp", validBuildingId, validUnitId, "invalidOwnerId")
                        .param("otp", otp))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Owner not found"))); // Example message check
    }

    // -------------------- Helper Methods --------------------

    private void clearDatabase() {
        userRoleRepository.deleteAll();
        tenantRepository.deleteAll();
        ownerRepository.deleteAll();
        buildingRepository.deleteAll();
        unitRepository.deleteAll();
    }

    private Owner createOwner(String email) {
        Owner owner = new Owner();
        Identity identity = new Identity();
        identity.setEmail(email);
        identity.setPhoneNumber("+14166024668");
        owner.setIdentity(identity);
        return ownerRepository.save(owner);
    }

    private Building createBuilding(String name, String city) {
        Building building = new Building();
        building.setName(name);
        return buildingRepository.save(building);
    }

    private Unit createUnit(String buildingId) {
        Building building = buildingRepository.findById(buildingId).orElseThrow();
        Unit unit = new Unit();
        unit.setBuilding(building);
        return unitRepository.save(unit);
    }
}