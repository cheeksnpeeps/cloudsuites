package com.cloudsuites.framework.webapp.rest.user.role;

import com.cloudsuites.framework.modules.property.personas.repository.OwnerRepository;
import com.cloudsuites.framework.modules.property.personas.repository.StaffRepository;
import com.cloudsuites.framework.modules.user.repository.AdminRepository;
import com.cloudsuites.framework.modules.user.repository.UserRepository;
import com.cloudsuites.framework.modules.user.repository.UserRoleRepository;
import com.cloudsuites.framework.services.property.personas.entities.Staff;
import com.cloudsuites.framework.services.property.personas.entities.StaffRole;
import com.cloudsuites.framework.services.property.personas.entities.StaffStatus;
import com.cloudsuites.framework.services.user.entities.Identity;
import com.cloudsuites.framework.webapp.authentication.utils.AdminTestHelper;
import com.cloudsuites.framework.webapp.rest.user.dto.IdentityDto;
import com.cloudsuites.framework.webapp.rest.user.dto.StaffDto;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class StaffRoleRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private AdminTestHelper adminTestHelper;
    private String accessToken;
    private String validStaffId;
    @Autowired
    private UserRoleRepository userRoleRepository;
    @Autowired
    private AdminRepository adminRepository;
    @Autowired
    private OwnerRepository ownerRepository;

    @BeforeEach
    void setUp() throws Exception {
        clearDatabase();

        // Initialize test data
        validStaffId = createStaff("testStaff", StaffRole.BUILDING_SECURITY).getStaffId();
        adminTestHelper = new AdminTestHelper(mockMvc, objectMapper, null, null);
        accessToken = adminTestHelper.registerAdminAndGetToken("testSuperAdmin", "+14166024668");
    }

    private MockHttpServletRequestBuilder withAuth(MockHttpServletRequestBuilder requestBuilder) {
        return requestBuilder.header("Authorization", "Bearer " + accessToken);
    }

    // -------------------- GET Requests --------------------

    @Test
    void testGetStaffRoleById_ValidId() throws Exception {
        mockMvc.perform(withAuth(get("/api/v1/staff/{staffId}/roles", validStaffId)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    StaffDto staffDto = objectMapper.readValue(jsonResponse, StaffDto.class);
                    assertThat(staffDto.getStaffId()).isEqualTo(validStaffId);
                });
    }

    @Test
    void testGetStaffRoleById_InvalidId() throws Exception {
        mockMvc.perform(withAuth(get("/api/v1/staff/{staffId}/roles", "invalidStaffId")))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetAllStaffRoles() throws Exception {
        mockMvc.perform(withAuth(get("/api/v1/staff/roles")))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    List<StaffDto> staffDtos = objectMapper.readValue(jsonResponse, objectMapper.getTypeFactory().constructCollectionType(List.class, StaffDto.class));
                    assertThat(staffDtos).hasSize(1); // One staff created in setup
                });
    }

    // -------------------- PUT Requests --------------------

    @Test
    void testUpdateStaffRole_ValidData() throws Exception {
        StaffDto staff = new StaffDto();
        IdentityDto identity = new IdentityDto();
        identity.setEmail("testUpdateStaffRole@gmail.com");
        staff.setRole(StaffRole.BUILDING_SECURITY);
        staff.setStatus(StaffStatus.ACTIVE);
        staff.setIdentity(identity);

        mockMvc.perform(withAuth(put("/api/v1/staff/{staffId}/roles", validStaffId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(staff)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    StaffDto staffDto = objectMapper.readValue(jsonResponse, StaffDto.class);
                    assertThat(staffDto.getRole()).isEqualTo(StaffRole.BUILDING_SECURITY);
                });
    }

    @Test
    void testUpdateStaffRole_InvalidId() throws Exception {
        StaffDto staff = new StaffDto();
        IdentityDto identity = new IdentityDto();
        identity.setEmail("testUpdateStaffRole@gmail.com");
        staff.setRole(StaffRole.BUILDING_SECURITY);
        staff.setStatus(StaffStatus.ACTIVE);
        staff.setIdentity(identity);

        mockMvc.perform(withAuth(put("/api/v1/staff/{staffId}/roles", "invalidStaffId"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(staff)))
                .andExpect(status().isNotFound());
    }

    // -------------------- DELETE Requests --------------------

    @Test
    void testDeleteStaffRole_ValidId() throws Exception {
        mockMvc.perform(withAuth(delete("/api/v1/staff/{staffId}/roles", validStaffId)))
                .andExpect(status().isNoContent());

        mockMvc.perform(withAuth(get("/api/v1/staff/{staffId}/roles", validStaffId)))
                .andExpect(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    StaffDto staffDto = objectMapper.readValue(jsonResponse, StaffDto.class);
                    assertThat(staffDto.getRole()).isEqualTo(StaffRole.DELETED);
                });
    }

    @Test
    void testDeleteStaffRole_InvalidId() throws Exception {
        mockMvc.perform(withAuth(delete("/api/v1/staff/{staffId}/roles", "invalidStaffId")))
                .andExpect(status().isNotFound());
    }

    // -------------------- Helper Methods --------------------

    private void clearDatabase() {
        userRoleRepository.deleteAll(); // Delete user roles first
        adminRepository.deleteAll();    // Delete admins (if they reference identity)
        ownerRepository.deleteAll();    // Delete owners (if they reference identity)
        staffRepository.deleteAll();    // Delete staff (if they reference identity)
        userRepository.deleteAll();     // Delete users from the identity table last
    }

    private Staff createStaff(String username, StaffRole role) {
        Staff staff = new Staff();
        Identity identity = new Identity();
        identity.setEmail(username + "@gmail.com");
        staff.setRole(role);
        staff.setStatus(StaffStatus.ACTIVE);
        identity = userRepository.save(identity);
        staff.setIdentity(identity);
        return staffRepository.save(staff);
    }
}

