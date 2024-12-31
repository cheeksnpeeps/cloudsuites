package com.cloudsuites.framework.webapp.rest.user;

import com.cloudsuites.framework.modules.property.features.repository.BuildingRepository;
import com.cloudsuites.framework.modules.property.features.repository.CompanyRepository;
import com.cloudsuites.framework.modules.property.personas.repository.OwnerRepository;
import com.cloudsuites.framework.modules.property.personas.repository.StaffRepository;
import com.cloudsuites.framework.modules.property.personas.repository.TenantRepository;
import com.cloudsuites.framework.modules.user.repository.AdminRepository;
import com.cloudsuites.framework.modules.user.repository.UserRepository;
import com.cloudsuites.framework.modules.user.repository.UserRoleRepository;
import com.cloudsuites.framework.services.property.features.entities.Building;
import com.cloudsuites.framework.services.property.features.entities.Company;
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
public class StaffRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BuildingRepository buildingRepository;

    @Autowired
    private AdminRepository adminRepository;

    private String validCompanyId;
    private String validBuildingId;
    private String validStaffId;

    private AdminTestHelper adminTestHelper;
    private String accessToken;
    @Autowired
    private UserRoleRepository userRoleRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TenantRepository tenantRepository;
    @Autowired
    private OwnerRepository ownerRepository;

    @BeforeEach
    void setUp() throws Exception {
        clearDatabase();
        // Initialize test data
        validCompanyId = createCompany("Test Company").getCompanyId();
        validBuildingId = createBuilding("Test Building").getBuildingId();
        validStaffId = createStaff(validCompanyId).getStaffId();
        adminTestHelper = new AdminTestHelper(mockMvc, objectMapper, null, null);
        accessToken = adminTestHelper.registerAdminAndGetToken("testRegisterAdmin", "+14166024668");
    }

    private MockHttpServletRequestBuilder withAuth(MockHttpServletRequestBuilder requestBuilder) {
        return requestBuilder.header("Authorization", "Bearer " + accessToken);
    }

    // -------------------- GET Requests --------------------

    @Test
    void testGetAllStaffsByCompanyId() throws Exception {
        mockMvc.perform(withAuth(get("/api/v1/staff/companies/{companyId}", validCompanyId)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    List<StaffDto> staffDtos = objectMapper.readValue(jsonResponse, objectMapper.getTypeFactory().constructCollectionType(List.class, StaffDto.class));
                    assertThat(staffDtos).hasSize(1);
                });
    }

    @Test
    void testGetAllStaffsByBuildingId() throws Exception {
        mockMvc.perform(withAuth(get("/api/v1/staff/buildings/{buildingId}", validBuildingId)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    List<StaffDto> staffDtos = objectMapper.readValue(jsonResponse, objectMapper.getTypeFactory().constructCollectionType(List.class, StaffDto.class));
                    assertThat(staffDtos).isEmpty(); // No staff associated with this building yet
                });
    }

    @Test
    void testGetStaffById_ValidId() throws Exception {
        mockMvc.perform(withAuth(get("/api/v1/staff/{id}", validStaffId)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    StaffDto staffDto = objectMapper.readValue(jsonResponse, StaffDto.class);
                    assertThat(staffDto.getIdentity().getEmail()).isEqualTo("testStaff@gmail.com");
                });
    }

    @Test
    void testGetStaffById_InvalidId() throws Exception {
        mockMvc.perform(withAuth(get("/api/v1/staff/{id}", "invalidStaffId")))
                .andExpect(status().isNotFound());
    }

    // -------------------- POST Requests --------------------

    @Test
    void testCreateStaff_ValidData() throws Exception {
        StaffDto staff = new StaffDto();
        staff.setRole(StaffRole.BUILDING_SUPERVISOR);
        staff.setStatus(StaffStatus.ACTIVE);
        IdentityDto identity = new IdentityDto();
        identity.setEmail("newStaff@gmail.com");
        staff.setIdentity(identity);

        String responseContent = mockMvc.perform(withAuth(post("/api/v1/staff/companies/{companyId}", validCompanyId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(staff))))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        StaffDto staffDto = objectMapper.readValue(responseContent, StaffDto.class);
        assertThat(staffDto.getIdentity().getEmail()).isEqualTo("newStaff@gmail.com");
    }

    @Test
    void testCreateStaff_InvalidData() throws Exception {
        String newStaffJson = "{\"identity\":{\"username\":\"\"}}"; // Invalid username
        mockMvc.perform(withAuth(post("/api/v1/staff/companies/{companyId}", validCompanyId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newStaffJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateStaff_ExistingUsername() throws Exception {
        StaffDto staff = new StaffDto();
        staff.setRole(StaffRole.BUILDING_SUPERVISOR);
        staff.setStatus(StaffStatus.ACTIVE);
        IdentityDto identity = new IdentityDto();
        identity.setEmail("testStaff@gmail.com");
        staff.setIdentity(identity);
        mockMvc.perform(withAuth(post("/api/v1/staff/companies/{companyId}", validCompanyId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(staff)))
                .andExpect(status().isConflict()); // Assuming you handle conflicts with 409
    }

    @Test
    void testCreateStaff_MalformedJson() throws Exception {
        String malformedJson = "{\"identity\":{}}"; // Missing username
        mockMvc.perform(withAuth(post("/api/v1/staff/companies/{companyId}", validCompanyId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(malformedJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateBuildingStaff_ValidData() throws Exception {
        StaffDto staff = new StaffDto();
        staff.setRole(StaffRole.BUILDING_SUPERVISOR);
        staff.setStatus(StaffStatus.ACTIVE);
        IdentityDto identity = new IdentityDto();
        identity.setEmail("buildingStaff@gmail.com");
        staff.setIdentity(identity);
        String responseContent = mockMvc.perform(withAuth(post("/api/v1/staff/companies/{companyId}/building/{buildingId}", validCompanyId, validBuildingId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(staff)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        StaffDto staffDto = objectMapper.readValue(responseContent, StaffDto.class);
        assertThat(staffDto.getIdentity().getEmail()).isEqualTo("buildingStaff@gmail.com");
    }

    // -------------------- PUT Requests --------------------

    @Test
    void testUpdateStaff_ValidData() throws Exception {
        String updatedStaffJson = "{\"identity\":{\"username\":\"updatedStaff\",\"email\":\"updatedStaff@gmail.com\"}}";
        String responseContent = mockMvc.perform(withAuth(put("/api/v1/staff/{id}", validStaffId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedStaffJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        StaffDto staffDto = objectMapper.readValue(responseContent, StaffDto.class);
        assertThat(staffDto.getIdentity().getEmail()).isEqualTo("updatedStaff@gmail.com");
    }

    @Test
    void testUpdateStaff_InvalidId() throws Exception {
        String updatedStaffJson = "{\"identity\":{\"username\":\"updatedStaff\"}}";
        mockMvc.perform(withAuth(put("/api/v1/staff/{id}", "invalidStaffId"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedStaffJson))
                .andExpect(status().isNotFound());
    }

    // -------------------- DELETE Requests --------------------

    @Test
    void testDeleteStaff_ValidId() throws Exception {
        mockMvc.perform(withAuth(delete("/api/v1/staff/{staffId}", validStaffId)))
                .andExpect(status().isNoContent());

        mockMvc.perform(withAuth(get("/api/v1/staff/{staffId}", validStaffId)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteStaff_InvalidId() throws Exception {
        mockMvc.perform(withAuth(delete("/api/v1/staff/{staffId}", "invalidStaffId")))
                .andExpect(status().isNotFound());
    }

    // -------------------- Helper Methods --------------------

    private void clearDatabase() {
        userRoleRepository.deleteAll();
        userRepository.deleteAll();
        tenantRepository.deleteAll();
        adminRepository.deleteAll();
        ownerRepository.deleteAll();
        staffRepository.deleteAll();
        companyRepository.deleteAll();
        adminRepository.deleteAll();
    }

    private Company createCompany(String name) {
        Company company = new Company();
        company.setName(name);
        return companyRepository.save(company);
    }

    private Staff createStaff(String companyId) {
        Staff staff = new Staff();
        staff.setRole(StaffRole.BUILDING_SUPERVISOR);
        Identity identity = new Identity();
        identity.setEmail("testStaff@gmail.com");
        staff.setIdentity(identity);
        staff.setCompany(companyRepository.findById(companyId).orElseThrow());
        return staffRepository.save(staff);
    }

    private Building createBuilding(String name) {
        Building building = new Building();
        building.setName(name);
        return buildingRepository.save(building);
    }
}
