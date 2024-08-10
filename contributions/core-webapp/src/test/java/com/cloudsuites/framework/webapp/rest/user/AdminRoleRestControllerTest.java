package com.cloudsuites.framework.webapp.rest.user;

import com.cloudsuites.framework.modules.user.repository.AdminRepository;
import com.cloudsuites.framework.modules.user.repository.UserRepository;
import com.cloudsuites.framework.services.user.entities.Admin;
import com.cloudsuites.framework.services.user.entities.AdminRole;
import com.cloudsuites.framework.services.user.entities.AdminStatus;
import com.cloudsuites.framework.services.user.entities.Identity;
import com.cloudsuites.framework.webapp.authentication.utils.AdminTestHelper;
import com.cloudsuites.framework.webapp.rest.user.dto.AdminDto;
import com.cloudsuites.framework.webapp.rest.user.dto.IdentityDto;
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
class AdminRoleRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private AdminTestHelper adminTestHelper;
    private String accessToken;
    private String validAdminId;

    @BeforeEach
    void setUp() throws Exception {
        clearDatabase();

        // Initialize test data
        validAdminId = createAdmin("testAdmin", AdminRole.USER).getAdminId();
        adminTestHelper = new AdminTestHelper(mockMvc, objectMapper, null, null);
        accessToken = adminTestHelper.registerAdminAndGetToken("testSuperAdmin", "+14166024668");
    }

    private MockHttpServletRequestBuilder withAuth(MockHttpServletRequestBuilder requestBuilder) {
        return requestBuilder.header("Authorization", "Bearer " + accessToken);
    }

    // -------------------- GET Requests --------------------

    @Test
    void testGetAdminRoleById_ValidId() throws Exception {
        mockMvc.perform(withAuth(get("/api/v1/admins/{adminId}/roles", validAdminId)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    AdminDto adminDto = objectMapper.readValue(jsonResponse, AdminDto.class);
                    assertThat(adminDto.getAdminId()).isEqualTo(validAdminId);
                });
    }

    @Test
    void testGetAdminRoleById_InvalidId() throws Exception {
        mockMvc.perform(withAuth(get("/api/v1/admins/{adminId}/roles", "invalidAdminId")))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetAllAdminsRoles() throws Exception {
        mockMvc.perform(withAuth(get("/api/v1/admins/roles")))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    List<AdminDto> adminDtos = objectMapper.readValue(jsonResponse, objectMapper.getTypeFactory().constructCollectionType(List.class, AdminDto.class));
                    assertThat(adminDtos).hasSize(2); // One admin created in setup
                });
    }

    // -------------------- PUT Requests --------------------

    @Test
    void testUpdateAdminRole_ValidData() throws Exception {

        AdminDto admin = new AdminDto();
        IdentityDto identity = new IdentityDto();
        identity.setUsername("testUpdateAdminRole");
        admin.setRole(AdminRole.USER);
        admin.setStatus(AdminStatus.ACTIVE);
        admin.setIdentity(identity);

        mockMvc.perform(withAuth(put("/api/v1/admins/{adminId}/roles", validAdminId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(admin)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    AdminDto adminDto = objectMapper.readValue(jsonResponse, AdminDto.class);
                    assertThat(adminDto.getRole()).isEqualTo(AdminRole.USER);
                });
    }

    @Test
    void testUpdateAdminRole_InvalidId() throws Exception {
        AdminDto admin = new AdminDto();
        IdentityDto identity = new IdentityDto();
        identity.setUsername("testUpdateAdminRole");
        admin.setRole(AdminRole.USER);
        admin.setStatus(AdminStatus.ACTIVE);
        admin.setIdentity(identity);
        mockMvc.perform(withAuth(put("/api/v1/admins/{adminId}/roles", "invalidAdminId"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(admin)))
                .andExpect(status().isNotFound());
    }

    // -------------------- DELETE Requests --------------------

    @Test
    void testDeleteAdminRole_ValidId() throws Exception {
        mockMvc.perform(withAuth(delete("/api/v1/admins/{adminId}/roles", validAdminId)))
                .andExpect(status().isNoContent());

        mockMvc.perform(withAuth(get("/api/v1/admins/{adminId}/roles", validAdminId)))
                .andExpect(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    AdminDto adminDto = objectMapper.readValue(jsonResponse, AdminDto.class);
                    assertThat(adminDto.getRole()).isEqualTo(AdminRole.DELETED);
                });
    }

    @Test
    void testDeleteAdminRole_InvalidId() throws Exception {
        mockMvc.perform(withAuth(delete("/api/v1/admins/{adminId}/roles", "invalidAdminId")))
                .andExpect(status().isNotFound());
    }

    // -------------------- Helper Methods --------------------

    private void clearDatabase() {
        adminRepository.deleteAll();
    }

    private Admin createAdmin(String username, AdminRole role) {
        Admin admin = new Admin();
        Identity identity = new Identity();
        identity.setUsername(username);
        admin.setRole(role);
        admin.setStatus(AdminStatus.ACTIVE);
        identity = userRepository.save(identity);
        admin.setIdentity(identity);
        return adminRepository.save(admin);
    }
}
