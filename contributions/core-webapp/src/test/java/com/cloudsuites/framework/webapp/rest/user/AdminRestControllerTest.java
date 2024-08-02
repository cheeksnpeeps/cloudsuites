package com.cloudsuites.framework.webapp.rest.user;

import com.cloudsuites.framework.modules.user.AdminRepository;
import com.cloudsuites.framework.services.common.exception.InvalidOperationException;
import com.cloudsuites.framework.services.common.exception.UsernameAlreadyExistsException;
import com.cloudsuites.framework.services.user.AdminService;
import com.cloudsuites.framework.services.user.entities.Admin;
import com.cloudsuites.framework.services.user.entities.AdminRole;
import com.cloudsuites.framework.services.user.entities.AdminStatus;
import com.cloudsuites.framework.services.user.entities.Identity;
import com.cloudsuites.framework.webapp.rest.user.dto.AdminDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AdminRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AdminService adminService;

    @Autowired
    private ObjectMapper objectMapper;

    private Admin testAdmin;
    @Autowired
    private AdminRepository adminRepository;

    @BeforeEach
    void setUp() throws UsernameAlreadyExistsException, InvalidOperationException {
        clearDatabase();
        testAdmin = createAdmin("testAdmin", "test@company.com");
    }

    // -------------------- GET Requests --------------------

    @Test
    void testGetAllAdmins() throws Exception {
        mockMvc.perform(get("/api/v1/admins"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    List<AdminDto> adminDtos = objectMapper.readValue(jsonResponse, objectMapper.getTypeFactory().constructCollectionType(List.class, AdminDto.class));
                    assertThat(adminDtos).hasSize(1);
                });
    }

    @Test
    void testGetAdminById_ValidId() throws Exception {
        mockMvc.perform(get("/api/v1/admins/{adminId}", testAdmin.getAdminId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    AdminDto adminDto = objectMapper.readValue(jsonResponse, AdminDto.class);
                    assertThat(adminDto.getIdentity().getUsername()).isEqualTo("testAdmin");
                });
    }

    @Test
    void testGetAdminById_InvalidId() throws Exception {
        mockMvc.perform(get("/api/v1/admins/{adminId}", "invalidAdminId"))
                .andExpect(status().isNotFound());
    }

    // -------------------- POST Requests --------------------

    @Test
    void testCreateAdmin_ValidData() throws Exception {
        String newAdminJson = "{\"identity\":{\"username\":\"newAdmin\", \"email\":\"new@company.com\"}}";
        String responseContent = mockMvc.perform(post("/api/v1/admins")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newAdminJson))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        AdminDto adminDto = objectMapper.readValue(responseContent, AdminDto.class);
        assertThat(adminDto.getIdentity().getUsername()).isEqualTo("newAdmin");
    }

    @Test
    void testCreateAdmin_ExistingUsername() throws Exception {
        String existingAdminJson = "{\"identity\":{\"username\":\"testAdmin\", \"email\":\"test@company.com\"}}"; // Same as existing
        mockMvc.perform(post("/api/v1/admins")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(existingAdminJson))
                .andExpect(status().isConflict()); // Assuming conflict for existing username
    }

    @Test
    void testCreateAdmin_InvalidData() throws Exception {
        String invalidAdminJson = "{\"identity\":{\"username\":\"\", \"email\":\"\"}}"; // Invalid data
        mockMvc.perform(post("/api/v1/admins")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidAdminJson))
                .andExpect(status().isBadRequest());
    }

    // -------------------- PUT Requests --------------------

    @Test
    void testUpdateAdmin_ValidData() throws Exception {
        String updatedAdminJson = "{\"identity\":{\"username\":\"updatedAdmin\", \"email\":\"updated@company.com\"}}";
        String responseContent = mockMvc.perform(put("/api/v1/admins/{adminId}", testAdmin.getAdminId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedAdminJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        AdminDto adminDto = objectMapper.readValue(responseContent, AdminDto.class);
        assertThat(adminDto.getIdentity().getUsername()).isEqualTo("updatedAdmin");
    }

    @Test
    void testUpdateAdmin_InvalidId() throws Exception {
        String updatedAdminJson = "{\"identity\":{\"username\":\"updatedAdmin\"}}";
        mockMvc.perform(put("/api/v1/admins/{adminId}", "invalidAdminId")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedAdminJson))
                .andExpect(status().isNotFound());
    }

    // -------------------- DELETE Requests --------------------

    @Test
    void testDeleteAdmin_ValidId() throws Exception {
        mockMvc.perform(delete("/api/v1/admins/{adminId}", testAdmin.getAdminId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/admins/{adminId}", testAdmin.getAdminId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteAdmin_InvalidId() throws Exception {
        mockMvc.perform(delete("/api/v1/admins/{adminId}", "invalidAdminId"))
                .andExpect(status().isNotFound());
    }

    // -------------------- Helper Methods --------------------

    private void clearDatabase() {
        adminRepository.deleteAll();
    }

    private Admin createAdmin(String username, String email) throws UsernameAlreadyExistsException, InvalidOperationException {
        Admin admin = new Admin();
        Identity identity = new Identity();
        identity.setUsername(username);
        identity.setEmail(email);
        admin.setIdentity(identity);
        admin.setRole(AdminRole.USER);
        admin.setStatus(AdminStatus.ACTIVE);
        return adminService.createAdmin(admin);
    }
}
