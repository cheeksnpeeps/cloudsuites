package com.cloudsuites.framework.webapp.rest.user.role;

import com.cloudsuites.framework.modules.property.personas.repository.OwnerRepository;
import com.cloudsuites.framework.modules.user.repository.AdminRepository;
import com.cloudsuites.framework.modules.user.repository.UserRepository;
import com.cloudsuites.framework.modules.user.repository.UserRoleRepository;
import com.cloudsuites.framework.services.property.personas.entities.Owner;
import com.cloudsuites.framework.services.property.personas.entities.OwnerRole;
import com.cloudsuites.framework.services.property.personas.entities.OwnerStatus;
import com.cloudsuites.framework.services.user.entities.Identity;
import com.cloudsuites.framework.webapp.authentication.utils.AdminTestHelper;
import com.cloudsuites.framework.webapp.rest.user.dto.IdentityDto;
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
class OwnerRoleRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OwnerRepository ownerRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private AdminTestHelper adminTestHelper;
    private String accessToken;
    private String validOwnerId;
    @Autowired
    private UserRoleRepository userRoleRepository;
    @Autowired
    private AdminRepository adminRepository;

    @BeforeEach
    void setUp() throws Exception {
        clearDatabase();

        // Initialize test data
        validOwnerId = createOwner("testOwner", OwnerRole.DEFAULT).getOwnerId();
        adminTestHelper = new AdminTestHelper(mockMvc, objectMapper, null, null);
        accessToken = adminTestHelper.registerAdminAndGetToken("testSuperAdmin", "+14166024668");
    }

    private MockHttpServletRequestBuilder withAuth(MockHttpServletRequestBuilder requestBuilder) {
        return requestBuilder.header("Authorization", "Bearer " + accessToken);
    }

    // -------------------- GET Requests --------------------

    @Test
    void testGetOwnerRoleById_ValidId() throws Exception {
        mockMvc.perform(withAuth(get("/api/v1/owners/{ownerId}/roles", validOwnerId)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    OwnerDto ownerDto = objectMapper.readValue(jsonResponse, OwnerDto.class);
                    assertThat(ownerDto.getOwnerId()).isEqualTo(validOwnerId);
                });
    }

    @Test
    void testGetOwnerRoleById_InvalidId() throws Exception {
        mockMvc.perform(withAuth(get("/api/v1/owners/{ownerId}/roles", "invalidOwnerId")))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetAllOwnersRoles() throws Exception {
        mockMvc.perform(withAuth(get("/api/v1/owners/roles")))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    List<OwnerDto> ownerDtos = objectMapper.readValue(jsonResponse, objectMapper.getTypeFactory().constructCollectionType(List.class, OwnerDto.class));
                    assertThat(ownerDtos).hasSize(1); // One owner created in setup
                });
    }

    // -------------------- PUT Requests --------------------

    @Test
    void testUpdateOwnerRole_ValidData() throws Exception {
        OwnerDto owner = new OwnerDto();
        IdentityDto identity = new IdentityDto();
        identity.setEmail("testUpdateOwnerRole@gmail.com");
        owner.setRole(OwnerRole.DEFAULT);
        owner.setStatus(OwnerStatus.ACTIVE);
        owner.setIdentity(identity);

        mockMvc.perform(withAuth(put("/api/v1/owners/{ownerId}/roles", validOwnerId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(owner)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    OwnerDto ownerDto = objectMapper.readValue(jsonResponse, OwnerDto.class);
                    assertThat(ownerDto.getRole()).isEqualTo(OwnerRole.DEFAULT);
                });
    }

    @Test
    void testUpdateOwnerRole_InvalidId() throws Exception {
        OwnerDto owner = new OwnerDto();
        IdentityDto identity = new IdentityDto();
        identity.setEmail("testUpdateOwnerRole@gmail.com");
        owner.setRole(OwnerRole.DEFAULT);
        owner.setStatus(OwnerStatus.ACTIVE);
        owner.setIdentity(identity);

        mockMvc.perform(withAuth(put("/api/v1/owners/{ownerId}/roles", "invalidOwnerId"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(owner)))
                .andExpect(status().isNotFound());
    }

    // -------------------- DELETE Requests --------------------

    @Test
    void testDeleteOwnerRole_ValidId() throws Exception {
        mockMvc.perform(withAuth(delete("/api/v1/owners/{ownerId}/roles", validOwnerId)))
                .andExpect(status().isNoContent());

        mockMvc.perform(withAuth(get("/api/v1/owners/{ownerId}/roles", validOwnerId)))
                .andExpect(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    OwnerDto ownerDto = objectMapper.readValue(jsonResponse, OwnerDto.class);
                    assertThat(ownerDto.getRole()).isEqualTo(OwnerRole.DELETED);
                });
    }

    @Test
    void testDeleteOwnerRole_InvalidId() throws Exception {
        mockMvc.perform(withAuth(delete("/api/v1/owners/{ownerId}/roles", "invalidOwnerId")))
                .andExpect(status().isNotFound());
    }

    // -------------------- Helper Methods --------------------

    private void clearDatabase() {
        userRoleRepository.deleteAll(); // Delete user roles first (dependent table)
        adminRepository.deleteAll();    // Delete admins (if they reference identity)
        ownerRepository.deleteAll();    // Delete owners (if they reference identity)
        userRepository.deleteAll();     // Delete users from the identity table last
    }

    private Owner createOwner(String username, OwnerRole role) {
        Owner owner = new Owner();
        Identity identity = new Identity();
        identity.setEmail(username + "@gmail.com");
        owner.setRole(role);
        owner.setStatus(OwnerStatus.ACTIVE);
        identity = userRepository.save(identity);
        owner.setIdentity(identity);
        return ownerRepository.save(owner);
    }
}
