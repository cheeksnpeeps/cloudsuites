package com.cloudsuites.framework.webapp.authentication;

import com.cloudsuites.framework.modules.property.features.repository.BuildingRepository;
import com.cloudsuites.framework.modules.property.features.repository.CompanyRepository;
import com.cloudsuites.framework.modules.property.features.repository.UnitRepository;
import com.cloudsuites.framework.services.common.exception.InvalidOperationException;
import com.cloudsuites.framework.services.common.exception.NotFoundResponseException;
import com.cloudsuites.framework.services.common.exception.UserAlreadyExistsException;
import com.cloudsuites.framework.services.property.features.entities.Building;
import com.cloudsuites.framework.services.property.features.entities.Company;
import com.cloudsuites.framework.services.property.personas.entities.Staff;
import com.cloudsuites.framework.services.property.personas.entities.StaffRole;
import com.cloudsuites.framework.services.property.personas.entities.StaffStatus;
import com.cloudsuites.framework.services.property.personas.service.StaffService;
import com.cloudsuites.framework.services.user.UserService;
import com.cloudsuites.framework.services.user.entities.Identity;
import com.cloudsuites.framework.webapp.rest.user.dto.IdentityDto;
import com.cloudsuites.framework.webapp.rest.user.dto.StaffDto;
import com.cloudsuites.framework.webapp.rest.user.mapper.StaffMapper;
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
class StaffAuthControllerTest {

    private Staff testStaff;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BuildingRepository buildingRepository;

    @Autowired
    private UnitRepository unitRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private StaffMapper staffMapper;

    @Autowired
    private StaffService staffService;

    @Autowired
    private UserService userService;

    private String validStaffId;
    private String validBuildingId;
    private String validCompanyId;

    @Autowired
    private CompanyRepository companyRepository;

    @BeforeEach
    void setUp() throws UserAlreadyExistsException, InvalidOperationException, NotFoundResponseException {
        clearDatabase();

        this.validCompanyId = createCompany().getCompanyId();
        this.testStaff = createStaff("testStaff@gmail.com");
        this.validStaffId = testStaff.getStaffId();
        this.validBuildingId = createBuilding("Building1", "City1").getBuildingId();
    }

    // -------------------- Registration Tests --------------------

    @Test
    void testRegisterStaff_ValidData() throws Exception {
        StaffDto newStaffDto = new StaffDto();
        newStaffDto.setRole(StaffRole.BUILDING_SUPERVISOR);
        newStaffDto.setStatus(StaffStatus.ACTIVE);
        IdentityDto identity = new IdentityDto();
        identity.setEmail("testRegisterStaff@gmail.com");
        identity.setPhoneNumber("+14166024668");
        newStaffDto.setIdentity(identity);

        mockMvc.perform(post("/api/v1/auth/staff/companies/{companyId}/buildings/{buildingId}/register", validCompanyId, validBuildingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newStaffDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    StaffDto responseStaffDto = objectMapper.readValue(jsonResponse, StaffDto.class);
                    assertThat(responseStaffDto.getIdentity().getEmail()).isEqualTo("testRegisterStaff@gmail.com");
                });
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " "})
    void testRegisterStaff_InvalidData_EmptyUsername(String invalidUsername) throws Exception {
        StaffDto newStaffDto = new StaffDto();
        newStaffDto.setRole(StaffRole.BUILDING_SUPERVISOR);
        newStaffDto.setStatus(StaffStatus.ACTIVE);
        IdentityDto identity = new IdentityDto();
        identity.setEmail("invalidEmail");
        newStaffDto.setIdentity(identity);

        mockMvc.perform(post("/api/v1/auth/staff/companies/{companyId}/buildings/{buildingId}/register", validCompanyId, validBuildingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newStaffDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Email should be valid"))); // Example message check
    }

    // -------------------- OTP Verification Tests --------------------

    @Test
    void testVerifyOtp_ValidData() throws Exception {
        String otp = "123456"; // Assume this OTP is valid
        // Assuming OTP is sent during registration

        mockMvc.perform(post("/api/v1/auth/staff/{staffId}/verify-otp", validStaffId)
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

        mockMvc.perform(post("/api/v1/auth/staff/{staffId}/verify-otp", validStaffId)
                        .param("otp", otp))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Invalid OTP provided")));
    }

    // -------------------- Token Refresh Tests --------------------

    @Test
    void testRefreshToken_ValidData() throws Exception {
        String otp = "123456"; // Assume this OTP is valid
        // Assuming OTP is sent during registration
        mockMvc.perform(post("/api/v1/auth/staff/{staffId}/verify-otp", validStaffId)
                        .param("otp", otp))
                .andExpect(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    String refreshToken = objectMapper.readTree(jsonResponse).get("refreshToken").asText();

                    // Use the refresh token to refresh the token
                    mockMvc.perform(post("/api/v1/auth/staff/{staffId}/refresh-token", validStaffId)
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
        mockMvc.perform(post("/api/v1/auth/staff/{staffId}/refresh-token", validStaffId)
                        .param("refreshToken", refreshToken))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Invalid token")));
    }

    // -------------------- Invalid ID Tests --------------------

    @Test
    void testVerifyOtp_InvalidStaffId() throws Exception {
        String otp = "123456"; // Assume this OTP is valid
        mockMvc.perform(post("/api/v1/auth/staff/{staffId}/verify-otp", "invalidStaffId")
                        .param("otp", otp))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Staff not found"))); // Example message check
    }

    // -------------------- Helper Methods --------------------

    private void clearDatabase() {
        // Clear out your database entities here
    }

    private Staff createStaff(String username) throws UserAlreadyExistsException, InvalidOperationException, NotFoundResponseException {
        Staff staff = new Staff();
        staff.setRole(StaffRole.BUILDING_SUPERVISOR);
        staff.setStatus(StaffStatus.ACTIVE);
        Identity identity = new Identity();
        identity.setEmail(username);
        identity.setPhoneNumber("+14166024668");
        staff.setIdentity(identity);
        staff = staffService.createStaff(staff, validCompanyId, validBuildingId);
        return staff; // Save staff with the repository here
    }

    private Building createBuilding(String name, String city) {
        Building building = new Building();
        building.setName(name);
        building.setCompany(companyRepository.findById(validCompanyId).orElseThrow());
        return buildingRepository.save(building);
    }

    private Company createCompany() {
        Company company = new Company();
        company.setName("Test Company");
        return companyRepository.save(company);
    }
}