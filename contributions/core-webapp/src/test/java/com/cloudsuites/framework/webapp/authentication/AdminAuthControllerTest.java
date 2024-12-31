package com.cloudsuites.framework.webapp.authentication;

import com.cloudsuites.framework.modules.property.features.repository.BuildingRepository;
import com.cloudsuites.framework.modules.property.features.repository.UnitRepository;
import com.cloudsuites.framework.modules.property.personas.repository.StaffRepository;
import com.cloudsuites.framework.modules.property.personas.repository.TenantRepository;
import com.cloudsuites.framework.modules.user.repository.AdminRepository;
import com.cloudsuites.framework.modules.user.repository.UserRepository;
import com.cloudsuites.framework.modules.user.repository.UserRoleRepository;
import com.cloudsuites.framework.services.common.exception.InvalidOperationException;
import com.cloudsuites.framework.services.common.exception.UserAlreadyExistsException;
import com.cloudsuites.framework.services.property.features.entities.Building;
import com.cloudsuites.framework.services.property.features.entities.Unit;
import com.cloudsuites.framework.services.user.AdminService;
import com.cloudsuites.framework.services.user.entities.Admin;
import com.cloudsuites.framework.services.user.entities.AdminRole;
import com.cloudsuites.framework.services.user.entities.AdminStatus;
import com.cloudsuites.framework.services.user.entities.Identity;
import com.cloudsuites.framework.webapp.rest.user.dto.AdminDto;
import com.cloudsuites.framework.webapp.rest.user.dto.IdentityDto;
import com.cloudsuites.framework.webapp.rest.user.mapper.AdminMapper;
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
class AdminAuthControllerTest {

    private Admin testAdmin;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BuildingRepository buildingRepository;

    @Autowired
    private UnitRepository unitRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AdminMapper adminMapper;

    private String validAdminId;
    private String validBuildingId;
    private String validUnitId;

    @Autowired
    private AdminService adminService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TenantRepository tenantRepository;
    @Autowired
    private AdminRepository adminRepository;
    @Autowired
    private StaffRepository staffRepository;
    @Autowired
    private UserRoleRepository userRoleRepository;

    @BeforeEach
    void setUp() throws UserAlreadyExistsException, InvalidOperationException {
        clearDatabase();

        // Initialize test data
        this.testAdmin = createAdmin("testAdmin@gmail.com");
        validAdminId = testAdmin.getAdminId();
        validBuildingId = createBuilding("Building1", "City1").getBuildingId();
        validUnitId = createUnit(validBuildingId).getUnitId();
    }

    // -------------------- Registration Tests --------------------

    @Test
    void testRegisterAdmin_ValidData() throws Exception {
        AdminDto newTestAdmin = new AdminDto();
        newTestAdmin.setRole(AdminRole.SUPER_ADMIN);
        newTestAdmin.setStatus(AdminStatus.ACTIVE);
        IdentityDto identity = new IdentityDto();
        identity.setEmail("testRegisterAdmin1@gmail.com");
        identity.setPhoneNumber("+14166024667");
        newTestAdmin.setIdentity(identity);

        mockMvc.perform(post("/api/v1/auth/admins/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newTestAdmin)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    AdminDto responseAdminDto = objectMapper.readValue(jsonResponse, AdminDto.class);
                    assertThat(responseAdminDto.getIdentity().getEmail()).isEqualTo("testRegisterAdmin1@gmail.com");
                });
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " "})
    void testRegisterAdmin_InvalidData_EmptyUsername(String invalidUsername) throws Exception {
        AdminDto newAdminDto = new AdminDto();
        newAdminDto.setRole(AdminRole.SUPER_ADMIN);
        newAdminDto.setStatus(AdminStatus.ACTIVE);
        IdentityDto identity = new IdentityDto();
        identity.setEmail("invalidEmail");
        newAdminDto.setIdentity(identity);

        mockMvc.perform(post("/api/v1/auth/admins/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newAdminDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Email should be valid"))); // Example message check
    }

    @Test
    void testRegisterAdmin_DuplicateUsername() throws Exception {
        AdminDto newAdminDto = new AdminDto();
        IdentityDto identity = new IdentityDto();
        newAdminDto.setStatus(AdminStatus.ACTIVE);
        newAdminDto.setRole(AdminRole.SUPER_ADMIN);
        identity.setEmail("testAdmin@gmail.com"); // Username already exists
        identity.setPhoneNumber("+14166024668");
        newAdminDto.setIdentity(identity);

        // Try to register again with the same username
        mockMvc.perform(post("/api/v1/auth/admins/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newAdminDto)))
                .andExpect(status().isConflict())
                .andExpect(content().string(containsString("User already exists"))); // Example message check
    }

    // -------------------- OTP Verification Tests --------------------

    @Test
    void testVerifyOtp_ValidData() throws Exception {
        String otp = "123456"; // Assume this OTP is valid

        // Assuming the OTP verification is part of your controller logic
        mockMvc.perform(post("/api/v1/auth/admins/{adminId}/verify-otp", validAdminId)
                        .param("otp", otp))
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

        mockMvc.perform(post("/api/v1/auth/admins/{adminId}/verify-otp", validAdminId)
                        .param("otp", otp))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Invalid OTP provided")));
    }

    // -------------------- Token Refresh Tests --------------------

    @Test
    void testRefreshToken_ValidData() throws Exception {
        String otp = "123456"; // Assume this OTP is valid
        // Assuming the OTP verification is part of your controller logic
        mockMvc.perform(post("/api/v1/auth/admins/{adminId}/verify-otp", validAdminId)
                        .param("otp", otp))
                .andExpect(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    String refreshToken = objectMapper.readTree(jsonResponse).get("refreshToken").asText();

                    // Use the refresh token to refresh the token
                    mockMvc.perform(post("/api/v1/auth/admins/{adminId}/refresh-token", validAdminId)
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

    @Test
    void testRefreshToken_InvalidToken() throws Exception {
        String refreshToken = "invalidRefreshToken"; // Invalid refresh token
        mockMvc.perform(post("/api/v1/auth/admins/{adminId}/refresh-token", validAdminId)
                        .param("refreshToken", refreshToken))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Invalid token")));
    }

    // -------------------- Invalid ID Tests --------------------

    @Test
    void testRegisterAdmin_InvalidAdminId() throws Exception {
        String otp = "123456"; // Assume this OTP is valid
        mockMvc.perform(post("/api/v1/auth/admins/{adminId}/verify-otp", "invalidAdminId")
                        .param("otp", otp))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Admin not found"))); // Example message check
    }

    // -------------------- Helper Methods --------------------

    private void clearDatabase() {
        userRoleRepository.deleteAll();
        tenantRepository.deleteAll();
        adminRepository.deleteAll();
        staffRepository.deleteAll();
        userRepository.deleteAll();
        buildingRepository.deleteAll();
    }

    private Admin createAdmin(String username) throws UserAlreadyExistsException, InvalidOperationException {
        Admin admin = new Admin();
        admin.setRole(AdminRole.SUPER_ADMIN);
        admin.setStatus(AdminStatus.ACTIVE);
        Identity identity = new Identity();
        identity.setEmail(username);
        identity.setPhoneNumber("+14166024668");
        admin.setIdentity(identity);
        admin = adminService.createAdmin(admin);
        return admin; // Save admin with the repository here
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
