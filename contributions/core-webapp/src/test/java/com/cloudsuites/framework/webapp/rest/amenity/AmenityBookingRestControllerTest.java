package com.cloudsuites.framework.webapp.rest.amenity;

import com.cloudsuites.framework.modules.amenity.repository.AmenityBookingRepository;
import com.cloudsuites.framework.modules.amenity.repository.AmenityRepository;
import com.cloudsuites.framework.modules.property.features.repository.BuildingRepository;
import com.cloudsuites.framework.modules.property.personas.repository.TenantRepository;
import com.cloudsuites.framework.modules.user.repository.AdminRepository;
import com.cloudsuites.framework.modules.user.repository.UserRepository;
import com.cloudsuites.framework.services.amenity.entities.Amenity;
import com.cloudsuites.framework.services.amenity.entities.AmenityType;
import com.cloudsuites.framework.services.amenity.entities.booking.AmenityBooking;
import com.cloudsuites.framework.services.amenity.entities.features.SwimmingPool;
import com.cloudsuites.framework.services.property.features.entities.Building;
import com.cloudsuites.framework.services.property.personas.entities.Tenant;
import com.cloudsuites.framework.services.property.personas.entities.TenantStatus;
import com.cloudsuites.framework.services.user.entities.Identity;
import com.cloudsuites.framework.webapp.authentication.utils.AdminTestHelper;
import com.cloudsuites.framework.webapp.rest.amenity.dto.AmenityBookingDto;
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

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AmenityBookingRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AmenityRepository amenityRepository;

    @Autowired
    private AmenityBookingRepository amenityBookingRepository;

    @Autowired
    private com.cloudsuites.framework.webapp.rest.amenityBooking.mapper.AmenityBookingMapper amenityBookingMapper;

    @Autowired
    private BuildingRepository buildingRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private UserRepository userRepository;

    private String validAmenityId;
    private Long validBookingId;
    private String validUserId;
    private String accessToken;

    private AdminTestHelper adminTestHelper;

    @Autowired
    private TenantRepository tenantRepository;

    private Amenity amenity;

    @BeforeEach
    void setUp() throws Exception {
        clearDatabase();
        Building building = createBuilding("BuildingA");
        amenity = createAmenity(building.getBuildingId());
        validAmenityId = amenity.getAmenityId();

        // Initialize AdminTestHelper for admin access
        adminTestHelper = new AdminTestHelper(mockMvc, objectMapper, null, null);
        accessToken = adminTestHelper.registerAdminAndGetToken("testRegisterAdmin", "+14166024668");

        // Create a user and get the userId (not shown: implement user creation)
        Tenant tenant = createUser("testUser", "+14165557777");
        validUserId = tenant.getTenantId();
    }

    private MockHttpServletRequestBuilder withAuth(MockHttpServletRequestBuilder requestBuilder) {
        return requestBuilder.header("Authorization", "Bearer " + accessToken);
    }

    // -------------------- Helper Methods --------------------

    private void clearDatabase() {
        amenityBookingRepository.deleteAll();
        amenityRepository.deleteAll();
        buildingRepository.deleteAll();
        adminRepository.deleteAll();
        userRepository.deleteAll();
    }

    private Amenity createAmenity(String buildingId) {
        Amenity amenity = new SwimmingPool();
        amenity.setName("First Pool");
        amenity.setType(AmenityType.SWIMMING_POOL);
        amenity = amenityRepository.save(amenity);

        // Logic to associate Amenity with Building if needed
        return amenity;
    }

    private Building createBuilding(String name) {
        Building building = new Building();
        building.setName(name);
        return buildingRepository.save(building);
    }

    private Tenant createUser(String username, String phoneNumber) {
        Tenant tenant = new Tenant();
        Identity identity = new Identity();
        identity.setEmail("tenantA@gmail.com");
        identity = userRepository.save(identity);
        tenant.setIdentity(identity);
        tenant.setStatus(TenantStatus.ACTIVE);
        tenant.setBuilding(buildingRepository.findAll().get(0));
        tenant = tenantRepository.save(tenant);
        return tenant;
    }

    // -------------------- Test Methods --------------------

    @Test
    void bookAmenity_shouldReturnCreatedBooking() throws Exception {
        AmenityBookingDto bookingDto = new AmenityBookingDto();
        bookingDto.setAmenityId(validAmenityId);
        bookingDto.setUserId(validUserId);
        bookingDto.setStartTime(LocalDateTime.now().plusDays(1));
        bookingDto.setEndTime(LocalDateTime.now().plusDays(1).plusHours(1));

        mockMvc.perform(withAuth(post("/api/v1/amenities/{amenityId}/bookings", validAmenityId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    AmenityBookingDto createdBooking = objectMapper.readValue(jsonResponse, AmenityBookingDto.class);
                    assertThat(createdBooking.getAmenityId()).isEqualTo(validAmenityId);
                    assertThat(createdBooking.getUserId()).isEqualTo(validUserId);
                });
    }

    @Test
    void bookAmenity_withInvalidTime_shouldReturnBadRequest() throws Exception {
        AmenityBookingDto bookingDto = new AmenityBookingDto();
        bookingDto.setAmenityId(validAmenityId);
        bookingDto.setUserId(validUserId);
        bookingDto.setStartTime(LocalDateTime.now().minusDays(1));  // Invalid past start time
        bookingDto.setEndTime(LocalDateTime.now().plusHours(1));

        mockMvc.perform(withAuth(post("/api/v1/amenities/{amenityId}/bookings", validAmenityId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("startTime=must be a date in the present or in the future")));
    }

    @Test
    void getBookingById_shouldReturnBooking() throws Exception {
        // Assuming we have a method to create a booking and return the booking ID
        validBookingId = createBooking(amenity, validUserId);

        mockMvc.perform(withAuth(get("/api/v1/amenities/bookings/{bookingId}", validBookingId))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    AmenityBookingDto bookingDto = objectMapper.readValue(jsonResponse, AmenityBookingDto.class);
                    assertThat(bookingDto.getBookingId()).isEqualTo(validBookingId);
                    assertThat(bookingDto.getAmenityId()).isEqualTo(validAmenityId);
                });
    }

    @Test
    void getBookingById_invalidId_shouldReturnNotFound() throws Exception {
        mockMvc.perform(withAuth(get("/api/v1/amenities/bookings/{bookingId}", "invalidBookingId"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void cancelBooking_shouldReturnNoContent() throws Exception {
        // Assuming we have a method to create a booking and return the booking ID
        validBookingId = createBooking(amenity, validUserId);

        mockMvc.perform(withAuth(delete("/api/v1/amenities/bookings/{bookingId}", validBookingId)))
                .andExpect(status().isNoContent());

        // Verify that the booking is no longer found after deletion
        mockMvc.perform(withAuth(get("/api/v1/amenities/bookings/{bookingId}", validBookingId)))
                .andExpect(status().isNotFound());
    }

    @Test
    void cancelBooking_invalidId_shouldReturnNotFound() throws Exception {
        mockMvc.perform(withAuth(delete("/api/v1/amenities/bookings/{bookingId}", "invalidBookingId")))
                .andExpect(status().isNotFound());
    }

    // -------------------- Helper Methods --------------------

    private Long createBooking(Amenity amenity, String userId) {
        AmenityBooking booking = new AmenityBooking();
        booking.setAmenity(amenity);
        booking.setUserId(userId);
        booking.setStartTime(LocalDateTime.now().plusDays(1));
        booking.setEndTime(LocalDateTime.now().plusDays(1).plusHours(1));
        booking = amenityBookingRepository.save(booking);
        return booking.getBookingId();
    }
}