package com.cloudsuites.framework.webapp.rest.property;

import com.cloudsuites.framework.modules.property.features.repository.CompanyRepository;
import com.cloudsuites.framework.modules.property.personas.repository.OwnerRepository;
import com.cloudsuites.framework.modules.property.personas.repository.StaffRepository;
import com.cloudsuites.framework.modules.property.personas.repository.TenantRepository;
import com.cloudsuites.framework.modules.user.repository.AdminRepository;
import com.cloudsuites.framework.modules.user.repository.UserRepository;
import com.cloudsuites.framework.modules.user.repository.UserRoleRepository;
import com.cloudsuites.framework.services.property.features.entities.Company;
import com.cloudsuites.framework.services.property.features.service.CompanyService;
import com.cloudsuites.framework.services.user.entities.Address;
import com.cloudsuites.framework.webapp.authentication.utils.AdminTestHelper;
import com.cloudsuites.framework.webapp.rest.property.dto.CompanyDto;
import com.cloudsuites.framework.webapp.rest.user.dto.AddressDto;
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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for the CompanyRestController.
 * This class tests the REST endpoints for managing companies.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CompanyRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private CompanyRepository companyRepository;

    private String validCompanyId;

    private AdminTestHelper adminTestHelper;
    private String accessToken;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AdminRepository adminRepository;
    @Autowired
    private TenantRepository tenantRepository;
    @Autowired
    private OwnerRepository ownerRepository;
    @Autowired
    private UserRoleRepository userRoleRepository;
    @Autowired
    private StaffRepository staffRepository;

    /**
     * Set up test data before each test.
     * This method initializes test data for a company.
     */
    @BeforeEach
    void setUp() throws Exception {
        clearDatabase();
        validCompanyId = createCompany("Test Company").getCompanyId();
        adminTestHelper = new AdminTestHelper(mockMvc, objectMapper, null, null);
        accessToken = adminTestHelper.registerAdminAndGetToken("testRegisterAdmin", "+14166024668");
    }

    private MockHttpServletRequestBuilder withAuth(MockHttpServletRequestBuilder requestBuilder) {
        return requestBuilder.header("Authorization", "Bearer " + accessToken);
    }

    // -------------------- GET Requests --------------------

    /**
     * Test the retrieval of all companies.
     * This test verifies that all companies are returned correctly.
     */
    @Test
    void testGetAllCompanies() throws Exception {
        mockMvc.perform(withAuth(get("/api/v1/companies")))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    List<CompanyDto> companyDtos = objectMapper.readValue(jsonResponse, objectMapper.getTypeFactory().constructCollectionType(List.class, CompanyDto.class));
                    assertThat(companyDtos).hasSize(1);
                    assertThat(companyDtos.get(0).getName()).isEqualTo("Test Company");
                });
    }

    /**
     * Test the retrieval of a company by a valid company ID.
     * This test checks if the correct company details are returned.
     */
    @Test
    void testGetCompanyById_ValidId() throws Exception {
        mockMvc.perform(withAuth(get("/api/v1/companies/{companyId}", validCompanyId)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    CompanyDto companyDto = objectMapper.readValue(jsonResponse, CompanyDto.class);
                    assertThat(companyDto.getName()).isEqualTo("Test Company");
                });
    }

    /**
     * Test the retrieval of a company by an invalid company ID.
     * This test verifies that a not found status is returned.
     */
    @Test
    void testGetCompanyById_InvalidId() throws Exception {
        mockMvc.perform(withAuth(get("/api/v1/companies/{companyId}", "invalidCompanyId")))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Company not found")));
    }

    // -------------------- POST Requests --------------------

    /**
     * Test saving a new company with valid data.
     * This test verifies that a company is created successfully and returns the correct response.
     */
    @Test
    void testSaveCompany_ValidData() throws Exception {
        CompanyDto newCompanyDto = createMockCompanyDto("New Company");
        mockMvc.perform(withAuth(post("/api/v1/companies"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCompanyDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    CompanyDto companyDto = objectMapper.readValue(jsonResponse, CompanyDto.class);
                    assertThat(companyDto.getName()).isEqualTo("New Company");
                });
    }

    /**
     * Test saving a company with invalid data (e.g., missing name).
     * This test verifies that a bad request status is returned for invalid input.
     */
    @Test
    void testSaveCompany_InvalidData_EmptyName() throws Exception {
        CompanyDto newCompanyDto = createMockCompanyDto("");
        mockMvc.perform(withAuth(post("/api/v1/companies"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCompanyDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Company name is required")));
    }

    // -------------------- DELETE Requests --------------------

    /**
     * Test deleting a company with a valid ID.
     * This test verifies that the company is deleted and a no content status is returned.
     */
    @Test
    void testDeleteCompany_ValidId() throws Exception {
        mockMvc.perform(withAuth(delete("/api/v1/companies/{companyId}", validCompanyId)))
                .andExpect(status().isNoContent());

        mockMvc.perform(withAuth(get("/api/v1/companies/{companyId}", validCompanyId)))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Company not found")));
    }

    /**
     * Test deleting a company with an invalid company ID.
     * This test verifies that a not found status is returned when the company ID does not exist.
     */
    @Test
    void testDeleteCompany_InvalidId() throws Exception {
        mockMvc.perform(withAuth(delete("/api/v1/companies/{companyId}", "invalidCompanyId")))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Company not found")));
    }

    // -------------------- Helper Methods --------------------

    /**
     * Clear the database by deleting all companies.
     * This method is used to ensure a clean state for each test.
     */
    private void clearDatabase() {
        staffRepository.deleteAll();
        userRoleRepository.deleteAll();
        tenantRepository.deleteAll();
        adminRepository.deleteAll();
        ownerRepository.deleteAll();
        userRepository.deleteAll();
        companyRepository.deleteAll();
        adminRepository.deleteAll();
    }

    /**
     * Create a company with the specified name.
     *
     * @param name The name of the company to create.
     * @return The created Company object.
     */
    private Company createCompany(String name) {
        Company company = new Company();
        company.setName(name);
        company.setAddress(createMockAddressEntity());
        return companyRepository.save(company);
    }

    /**
     * Create a mock CompanyDto object with the given name.
     *
     * @param name The name of the company.
     * @return The mock CompanyDto object.
     */
    private CompanyDto createMockCompanyDto(String name) {
        CompanyDto companyDto = new CompanyDto();
        companyDto.setName(name);
        companyDto.setAddress(createMockAddressDto());
        return companyDto;
    }

    /**
     * Create a mock AddressDto object.
     *
     * @return The mock AddressDto object.
     */
    private AddressDto createMockAddressDto() {
        AddressDto address = new AddressDto();
        address.setAptNumber("Apt 101");
        address.setStreetNumber("123");
        address.setStreetName("Main St");
        address.setAddressLine2("Near Central Park");
        address.setCity("Toronto");
        address.setStateProvinceRegion("Ontario");
        address.setPostalCode("M1M 1M1");
        address.setCountry("Canada");
        address.setLatitude(43.6532);
        address.setLongitude(-79.3832);
        return address;
    }

    /**
     * Create a mock Address entity.
     *
     * @return The mock Address entity.
     */
    private Address createMockAddressEntity() {
        Address address = new Address();
        address.setAptNumber("Apt 101");
        address.setStreetNumber("123");
        address.setStreetName("Main St");
        address.setAddressLine2("Near Central Park");
        address.setCity("Toronto");
        address.setStateProvinceRegion("Ontario");
        address.setPostalCode("M1M 1M1");
        address.setCountry("Canada");
        address.setLatitude(43.6532);
        address.setLongitude(-79.3832);
        return address;
    }
}