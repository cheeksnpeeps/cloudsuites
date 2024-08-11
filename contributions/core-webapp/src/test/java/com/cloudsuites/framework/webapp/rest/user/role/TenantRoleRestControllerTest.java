package com.cloudsuites.framework.webapp.rest.user.role;

import com.cloudsuites.framework.modules.property.personas.repository.TenantRepository;
import com.cloudsuites.framework.modules.user.repository.UserRepository;
import com.cloudsuites.framework.services.property.personas.entities.Tenant;
import com.cloudsuites.framework.services.property.personas.entities.TenantRole;
import com.cloudsuites.framework.services.property.personas.entities.TenantStatus;
import com.cloudsuites.framework.services.user.entities.Identity;
import com.cloudsuites.framework.webapp.authentication.utils.AdminTestHelper;
import com.cloudsuites.framework.webapp.rest.user.dto.IdentityDto;
import com.cloudsuites.framework.webapp.rest.user.dto.TenantDto;
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
class TenantRoleRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TenantRepository tenantRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private AdminTestHelper adminTestHelper;
    private String accessToken;
    private String validTenantId;

    @BeforeEach
    void setUp() throws Exception {
        clearDatabase();

        // Initialize test data
        validTenantId = createTenant("testTenant", TenantRole.DEFAULT).getTenantId();
        adminTestHelper = new AdminTestHelper(mockMvc, objectMapper, null, null);
        accessToken = adminTestHelper.registerAdminAndGetToken("testSuperAdmin", "+14166024668");
    }

    private MockHttpServletRequestBuilder withAuth(MockHttpServletRequestBuilder requestBuilder) {
        return requestBuilder.header("Authorization", "Bearer " + accessToken);
    }

    // -------------------- GET Requests --------------------

    @Test
    void testGetTenantRoleById_ValidId() throws Exception {
        mockMvc.perform(withAuth(get("/api/v1/tenants/{tenantId}/roles", validTenantId)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    TenantDto tenantDto = objectMapper.readValue(jsonResponse, TenantDto.class);
                    assertThat(tenantDto.getTenantId()).isEqualTo(validTenantId);
                });
    }

    @Test
    void testGetTenantRoleById_InvalidId() throws Exception {
        mockMvc.perform(withAuth(get("/api/v1/tenants/{tenantId}/roles", "invalidTenantId")))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetAllTenantsRoles() throws Exception {
        mockMvc.perform(withAuth(get("/api/v1/tenants/roles")))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    List<TenantDto> tenantDtos = objectMapper.readValue(jsonResponse, objectMapper.getTypeFactory().constructCollectionType(List.class, TenantDto.class));
                    assertThat(tenantDtos).hasSize(1); // One tenant created in setup
                });
    }

    // -------------------- PUT Requests --------------------

    @Test
    void testUpdateTenantRole_ValidData() throws Exception {
        TenantDto tenant = new TenantDto();
        IdentityDto identity = new IdentityDto();
        identity.setUsername("testUpdateTenantRole");
        tenant.setRole(TenantRole.DEFAULT);
        tenant.setStatus(TenantStatus.ACTIVE);
        tenant.setIdentity(identity);

        mockMvc.perform(withAuth(put("/api/v1/tenants/{tenantId}/roles", validTenantId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tenant)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    TenantDto tenantDto = objectMapper.readValue(jsonResponse, TenantDto.class);
                    assertThat(tenantDto.getRole()).isEqualTo(TenantRole.DEFAULT);
                });
    }

    @Test
    void testUpdateTenantRole_InvalidId() throws Exception {
        TenantDto tenant = new TenantDto();
        IdentityDto identity = new IdentityDto();
        identity.setUsername("testUpdateTenantRole");
        tenant.setRole(TenantRole.DEFAULT);
        tenant.setStatus(TenantStatus.ACTIVE);
        tenant.setIdentity(identity);
        mockMvc.perform(withAuth(put("/api/v1/tenants/{tenantId}/roles", "invalidTenantId"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tenant)))
                .andExpect(status().isNotFound());
    }

    // -------------------- DELETE Requests --------------------

    @Test
    void testDeleteTenantRole_ValidId() throws Exception {
        mockMvc.perform(withAuth(delete("/api/v1/tenants/{tenantId}/roles", validTenantId)))
                .andExpect(status().isNoContent());

        mockMvc.perform(withAuth(get("/api/v1/tenants/{tenantId}/roles", validTenantId)))
                .andExpect(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    TenantDto tenantDto = objectMapper.readValue(jsonResponse, TenantDto.class);
                    assertThat(tenantDto.getRole()).isEqualTo(TenantRole.DELETED);
                });
    }

    @Test
    void testDeleteTenantRole_InvalidId() throws Exception {
        mockMvc.perform(withAuth(delete("/api/v1/tenants/{tenantId}/roles", "invalidTenantId")))
                .andExpect(status().isNotFound());
    }

    // -------------------- Helper Methods --------------------

    private void clearDatabase() {
        tenantRepository.deleteAll();
    }

    private Tenant createTenant(String username, TenantRole role) {
        Tenant tenant = new Tenant();
        Identity identity = new Identity();
        identity.setUsername(username);
        tenant.setRole(role);
        tenant.setStatus(TenantStatus.ACTIVE);
        identity = userRepository.save(identity);
        tenant.setIdentity(identity);
        return tenantRepository.save(tenant);
    }
}
