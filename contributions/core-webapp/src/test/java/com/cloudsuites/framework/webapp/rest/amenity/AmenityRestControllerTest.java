package com.cloudsuites.framework.webapp.rest.amenity;

import com.cloudsuites.framework.modules.amenity.repository.AmenityBookingRepository;
import com.cloudsuites.framework.modules.amenity.repository.AmenityBuildingRepository;
import com.cloudsuites.framework.modules.amenity.repository.AmenityRepository;
import com.cloudsuites.framework.modules.property.features.repository.BuildingRepository;
import com.cloudsuites.framework.modules.property.personas.repository.StaffRepository;
import com.cloudsuites.framework.modules.property.personas.repository.TenantRepository;
import com.cloudsuites.framework.modules.user.repository.AdminRepository;
import com.cloudsuites.framework.modules.user.repository.UserRepository;
import com.cloudsuites.framework.modules.user.repository.UserRoleRepository;
import com.cloudsuites.framework.services.amenity.entities.Amenity;
import com.cloudsuites.framework.services.amenity.entities.AmenityBuilding;
import com.cloudsuites.framework.services.amenity.entities.AmenityType;
import com.cloudsuites.framework.services.amenity.entities.features.SwimmingPool;
import com.cloudsuites.framework.services.property.features.entities.Building;
import com.cloudsuites.framework.webapp.authentication.utils.AdminTestHelper;
import com.cloudsuites.framework.webapp.rest.amenity.dto.AmenityDto;
import com.cloudsuites.framework.webapp.rest.amenity.dto.DailyAvailabilityDto;
import com.cloudsuites.framework.webapp.rest.amenity.mapper.AmenityMapper;
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

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AmenityRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AmenityRepository amenityRepository;

    @Autowired
    private AmenityMapper amenityMapper;

    @Autowired
    private BuildingRepository buildingRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private String validAmenityId;
    private String validBuildingId;

    @Autowired
    private AmenityBuildingRepository amenityBuildingRepository;

    private AdminTestHelper adminTestHelper;
    private String accessToken;
    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private UserRepository identityRepository;
    @Autowired
    private AmenityBookingRepository amenityBookingRepository;
    @Autowired
    private TenantRepository tenantRepository;
    @Autowired
    private UserRoleRepository userRoleRepository;
    @Autowired
    private StaffRepository staffRepository;

    @BeforeEach
    void setUp() throws Exception {
        clearDatabase();
        Building building = createBuilding("BuildingA");
        validBuildingId = building.getBuildingId();
        Amenity amenity = createAmenity(validBuildingId);
        validAmenityId = amenity.getAmenityId();
        adminTestHelper = new AdminTestHelper(mockMvc, objectMapper, null, null);
        accessToken = adminTestHelper.registerAdminAndGetToken("testRegisterAdmin", "+14166024668");
    }

    private MockHttpServletRequestBuilder withAuth(MockHttpServletRequestBuilder requestBuilder) {
        return requestBuilder.header("Authorization", "Bearer " + accessToken);
    }

    // -------------------- Helper Methods --------------------

    private void clearDatabase() {
        amenityBookingRepository.deleteAll();
        amenityRepository.deleteAll();
        userRoleRepository.deleteAll();
        staffRepository.deleteAll();
        tenantRepository.deleteAll();
        buildingRepository.deleteAll();
        amenityBuildingRepository.deleteAll();
        adminRepository.deleteAll();
        identityRepository.deleteAll();
    }

    private Amenity createAmenity(String buildingId) {
        Amenity amenity = new SwimmingPool();
        amenity.setName("First Pool");
        amenity.setType(AmenityType.SWIMMING_POOL);
        amenity = amenityRepository.save(amenity);
        Building building = buildingRepository.findById(buildingId).orElseThrow();
        AmenityBuilding amenityBuilding = new AmenityBuilding();
        amenityBuilding.setBuildingId(building.getBuildingId());
        amenityBuilding.setAmenityId(amenity.getAmenityId());
        amenityBuildingRepository.save(amenityBuilding);
        return amenity;
    }

    private Building createBuilding(String name) {
        Building building = new Building();
        building.setName(name);
        return buildingRepository.save(building);
    }

    // -------------------- GET Requests --------------------

    @Test
    void listAmenitiesByBuildingId_shouldReturnAmenities() throws Exception {
        mockMvc.perform(withAuth(get("/api/v1/buildings/{buildingId}/amenities", validBuildingId))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    List<AmenityDto> amenityDtos = objectMapper.readValue(jsonResponse, objectMapper.getTypeFactory().constructCollectionType(List.class, AmenityDto.class));
                    assertThat(amenityDtos).hasSize(1);
                    assertThat(amenityDtos.get(0).getAmenityId()).isEqualTo(validAmenityId); // Check amenity ID
                });
    }

    @Test
    void getAmenityById_shouldReturnAmenity() throws Exception {
        mockMvc.perform(withAuth(get("/api/v1/buildings/{buildingId}/amenities/{amenityId}", validBuildingId, validAmenityId))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    AmenityDto amenityDto = objectMapper.readValue(jsonResponse, AmenityDto.class);
                    assertThat(amenityDto.getAmenityId()).isEqualTo(validAmenityId); // Check the amenity ID
                    assertThat(amenityDto.getName()).isEqualTo("First Pool"); // Check the amenity name
                });
    }

    @Test
    void getAmenityById_invalidId_shouldReturnNotFound() throws Exception {
        mockMvc.perform(withAuth(get("/api/v1/buildings/{buildingId}/amenities/{amenityId}", validBuildingId, "invalidAmenityId"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    // -------------------- POST Requests --------------------

    @Test
    void createAmenity_shouldReturnCreatedAmenity() throws Exception {
        AmenityDto newAmenity = new AmenityDto();
        newAmenity.setName("Pool");
        newAmenity.setType(AmenityType.SWIMMING_POOL);
        newAmenity.setDescription("Well-equipped pool");
        newAmenity.setBuildingIds(List.of(validBuildingId)); // Associate the amenity with the building
        DailyAvailabilityDto dailyAvailability = new DailyAvailabilityDto();
        dailyAvailability.setDayOfWeek(DayOfWeek.MONDAY);
        dailyAvailability.setOpenTime(LocalTime.NOON);
        dailyAvailability.setCloseTime(LocalTime.NOON.plusHours(1));
        newAmenity.setDailyAvailabilities(List.of(dailyAvailability)); // Set daily availabilities
        // newAmenity.setAmenityType(AmenityType.SWIMMING_POOL);
        newAmenity.setBuildingIds(List.of(validBuildingId)); // Associate the amenity with the building
        mockMvc.perform(withAuth(post("/api/v1/amenities", validBuildingId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newAmenity)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    AmenityDto amenityDto = objectMapper.readValue(jsonResponse, AmenityDto.class);
                    assertThat(amenityDto.getName()).isEqualTo("Pool"); // Validate the created amenity name
                });
    }

    @Test
    void createAmenity_withEmptyName_shouldReturnBadRequest() throws Exception {
        AmenityDto newAmenity = new AmenityDto();
        newAmenity.setName(""); // Invalid name
        newAmenity.setType(AmenityType.SWIMMING_POOL);
        mockMvc.perform(withAuth(post("/api/v1/amenities", validBuildingId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newAmenity)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Name is mandatory"))); // Example error message
    }

    // -------------------- PUT Requests --------------------

    @Test
    void updateAmenity_shouldReturnUpdatedAmenity() throws Exception {
        AmenityDto updatedAmenityDto = new AmenityDto();
        updatedAmenityDto.setAmenityId(validAmenityId);
        updatedAmenityDto.setName("Updated Pool");
//        updatedAmenityDto.setAmenityType(AmenityType.SWIMMING_POOL);
        mockMvc.perform(withAuth(put("/api/v1/buildings/{buildingId}/amenities/{amenityId}", validBuildingId, validAmenityId))
                        .content(objectMapper.writeValueAsString(updatedAmenityDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    AmenityDto amenityDto = objectMapper.readValue(jsonResponse, AmenityDto.class);
                    assertThat(amenityDto.getName()).isEqualTo("Updated Pool"); // Check the updated name
                });
    }

    @Test
    void updateAmenity_invalidId_shouldReturnNotFound() throws Exception {
        AmenityDto updatedAmenityDto = new AmenityDto();
        updatedAmenityDto.setAmenityId("invalidAmenityId");
        updatedAmenityDto.setName("Updated Pool");
        //       updatedAmenityDto.setAmenityType(AmenityType.SWIMMING_POOL);
        mockMvc.perform(withAuth(put("/api/v1/buildings/{buildingId}/amenities/{amenityId}", validBuildingId, "invalidAmenityId"))
                        .content(objectMapper.writeValueAsString(updatedAmenityDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    // -------------------- DELETE Requests --------------------

    @Test
    void deleteAmenity_shouldReturnSuccessMessage() throws Exception {
        mockMvc.perform(withAuth(delete("/api/v1/buildings/{buildingId}/amenities/{amenityId}", validBuildingId, validAmenityId)))
                .andExpect(status().isNoContent());

        // Verify that the amenity is no longer found after deletion
        mockMvc.perform(withAuth(get("/api/v1/buildings/{buildingId}/amenities/{amenityId}", validBuildingId, validAmenityId)))
                .andExpect(status().isNotFound());
    }

    @Test
    void createAmenity_withDuplicateName_shouldReturnConflict() throws Exception {
        AmenityDto newAmenity = new AmenityDto();
        newAmenity.setName("First Pool"); // Existing name
        newAmenity.setDescription("Another pool");
        newAmenity.setType(AmenityType.SWIMMING_POOL);
        newAmenity.setBuildingIds(List.of(validBuildingId));

        mockMvc.perform(withAuth(post("/api/v1/amenities", validBuildingId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newAmenity)))
                .andExpect(status().isConflict()) // Assuming you handle duplicate entries as a conflict
                .andExpect(content().string(containsString("menity already exists with name: First Pool")));
    }

    @Test
    void deleteAmenity_invalidId_shouldReturnNotFound() throws Exception {
        mockMvc.perform(withAuth(delete("/api/v1/buildings/{buildingId}/amenities/{amenityId}", validBuildingId, "invalidAmenityId")))
                .andExpect(status().isNotFound());
    }
}
