package com.cloudsuites.framework.webapp.rest.amenity;

import com.cloudsuites.framework.modules.amenity.repository.AmenityBookingRepository;
import com.cloudsuites.framework.modules.amenity.repository.AmenityRepository;
import com.cloudsuites.framework.modules.amenity.repository.AvailabilityRepository;
import com.cloudsuites.framework.modules.property.features.repository.BuildingRepository;
import com.cloudsuites.framework.modules.property.features.repository.UnitRepository;
import com.cloudsuites.framework.modules.property.personas.repository.OwnerRepository;
import com.cloudsuites.framework.modules.property.personas.repository.StaffRepository;
import com.cloudsuites.framework.modules.property.personas.repository.TenantRepository;
import com.cloudsuites.framework.modules.user.repository.AdminRepository;
import com.cloudsuites.framework.modules.user.repository.UserRepository;
import com.cloudsuites.framework.modules.user.repository.UserRoleRepository;
import com.cloudsuites.framework.services.amenity.entities.Amenity;
import com.cloudsuites.framework.services.amenity.entities.AmenityType;
import com.cloudsuites.framework.services.amenity.entities.DailyAvailability;
import com.cloudsuites.framework.services.amenity.entities.booking.AmenityBooking;
import com.cloudsuites.framework.services.amenity.entities.features.SwimmingPool;
import com.cloudsuites.framework.services.property.features.entities.Building;
import com.cloudsuites.framework.services.property.features.entities.Unit;
import com.cloudsuites.framework.services.property.personas.entities.Tenant;
import com.cloudsuites.framework.services.property.personas.entities.TenantStatus;
import com.cloudsuites.framework.services.user.entities.Identity;
import com.cloudsuites.framework.webapp.authentication.utils.AdminTestHelper;
import com.cloudsuites.framework.webapp.rest.amenity.dto.AmenityBookingDto;
import com.cloudsuites.framework.webapp.rest.amenity.mapper.AmenityBookingMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
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
@ExtendWith(SpringExtension.class)
class AmenityBookingRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AmenityRepository amenityRepository;

    @Autowired
    private AmenityBookingRepository amenityBookingRepository;

    @Autowired
    private AmenityBookingMapper mapper;

    @Autowired
    private BuildingRepository buildingRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private UserRepository userRepository;

    private String validAmenityId;
    @Autowired
    protected WebApplicationContext wac;
    private String validUserId;
    private String accessToken;

    private AdminTestHelper adminTestHelper;

    @Autowired
    private TenantRepository tenantRepository;

    private Amenity amenity;
    Logger logger = LoggerFactory.getLogger(AmenityBookingRestControllerTest.class);
    private String validBookingId;


    @Autowired
    private UnitRepository unitRepository;
    @Autowired
    private AvailabilityRepository availabilityRepository;
    @Autowired
    private StaffRepository staffRepository;
    @Autowired
    private OwnerRepository ownerRepository;
    @Autowired
    private UserRoleRepository userRoleRepository;

    @BeforeEach
    void setUp() throws Exception {
        clearDatabase();

        Building building = createBuilding("BuildingA");
        String validBuildingId = building.getBuildingId();
        amenity = createAmenity(building.getBuildingId());
        validAmenityId = amenity.getAmenityId();

        Unit unit = createUnit(validBuildingId);
        String validUnitId = unit.getUnitId();

        // Create a user and get the userId (not shown: implement user creation)
        Tenant tenant = createUser("testUser", "+14165557777");
        validUserId = tenant.getTenantId();

        mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .apply(SecurityMockMvcConfigurers.springSecurity()) // Ensure this is imported
                .addFilters(new CharacterEncodingFilter("UTF-8", true)) // Optional: Set request/response encoding
                .build();

        // Initialize AdminTestHelper for admin access
        adminTestHelper = new AdminTestHelper(mockMvc, objectMapper, validBuildingId, validUnitId);
        accessToken = adminTestHelper.registerAdminAndGetToken("testRegisterAdmin", "+14166024668");


    }

    private MockHttpServletRequestBuilder withAuth(MockHttpServletRequestBuilder requestBuilder) {
        return requestBuilder.header("Authorization", "Bearer " + accessToken);
    }

    // -------------------- Helper Methods --------------------

    private void clearDatabase() {

    }

    private Amenity createAmenity(String buildingId) {
        Amenity amenity = new SwimmingPool();
        amenity.setName("First Pool");
        amenity.setType(AmenityType.SWIMMING_POOL);

        List<DailyAvailability> dailyAvailabilities = new ArrayList<>();
        dailyAvailabilities.add(new DailyAvailability(amenity, DayOfWeek.MONDAY, LocalTime.of(8, 0), LocalTime.of(20, 0)));
        dailyAvailabilities.add(new DailyAvailability(amenity, DayOfWeek.TUESDAY, LocalTime.of(8, 0), LocalTime.of(20, 0)));
        dailyAvailabilities.add(new DailyAvailability(amenity, DayOfWeek.WEDNESDAY, LocalTime.of(8, 0), LocalTime.of(20, 0)));
        dailyAvailabilities.add(new DailyAvailability(amenity, DayOfWeek.THURSDAY, LocalTime.of(8, 0), LocalTime.of(20, 0)));
        dailyAvailabilities.add(new DailyAvailability(amenity, DayOfWeek.FRIDAY, LocalTime.of(8, 0), LocalTime.of(20, 0)));
        dailyAvailabilities.add(new DailyAvailability(amenity, DayOfWeek.SATURDAY, LocalTime.of(8, 0), LocalTime.of(20, 0)));
        dailyAvailabilities.add(new DailyAvailability(amenity, DayOfWeek.SUNDAY, LocalTime.of(8, 0), LocalTime.of(20, 0)));

        // Save the amenity
        Amenity savedAmenity = amenityRepository.save(amenity);
        logger.debug("Saved amenity with ID: {}", savedAmenity.getAmenityId());

        // Save daily availabilities if they exist
        logger.debug("Saving daily availabilities for amenity ID: {}", savedAmenity.getAmenityId());
        availabilityRepository.saveAll(dailyAvailabilities);
        logger.debug("Saved daily availabilities: {}", dailyAvailabilities);

        amenity.setDailyAvailabilities(dailyAvailabilities);
        amenity = amenityRepository.save(amenity);
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
    @WithMockUser(username = "testUser", roles = "USER")
        // Use @WithMockUser to mock authentication
    void bookAmenity_shouldReturnCreatedBooking() throws Exception {
        AmenityBookingDto bookingDto = new AmenityBookingDto();
        // start time to the next day at 12:00 pm
        bookingDto.setStartTime(LocalDateTime.now().plusDays(1).withHour(12).withMinute(0).withSecond(0).withNano(0));
        bookingDto.setEndTime(LocalDateTime.now().plusDays(1).withHour(13).withMinute(0).withSecond(0).withNano(0));
        logger.debug("Booking object {}", objectMapper.writeValueAsString(bookingDto));
        // Perform the request and capture the MvcResult
        mockMvc.perform(withAuth(post("/api/v1/amenities/{amenityId}/tenants/{tenantId}/bookings", validAmenityId, validUserId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingDto)))
                .andExpect(status().isCreated())
                .andExpect(resultAfterAsync -> {
                    String jsonResponse = resultAfterAsync.getResponse().getContentAsString();
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
        bookingDto.setStartTime(LocalDateTime.now().withHour(0));
        bookingDto.setEndTime(LocalDateTime.now().withHour(0));

        mockMvc.perform(withAuth(post("/api/v1/amenities/{amenityId}/tenants/{tenantId}/bookings", validAmenityId, validUserId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Booking period is less than the minimum allowed")));
    }

    @Test
    void getBookingById_shouldReturnBooking() throws Exception {
        // Assuming we have a method to create a booking and return the booking ID
        validBookingId = createBooking(amenity, validUserId);

        mockMvc.perform(withAuth(get("/api/v1/amenities/bookings/{bookingId}", validBookingId))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
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
        String newBooking = createBooking(amenity, validUserId);


        mockMvc.perform(withAuth(delete("/api/v1/amenities/tenants/{tenantId}/bookings/{bookingId}", validUserId, newBooking))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        // Verify that the booking is no longer found after deletion
        mockMvc.perform(withAuth(get("/api/v1/amenities/bookings/{bookingId}", newBooking))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void cancelBooking_invalidId_shouldReturnNotFound() throws Exception {
        mockMvc.perform(withAuth(delete("/api/v1/amenities/tenants/{tenantId}/bookings/{bookingId}", validUserId, "invalidBookingId"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    private Unit createUnit(String buildingId) {
        Unit unit = new Unit();
        unit.setBuilding(buildingRepository.findById(buildingId).orElseThrow());
        unit.setUnitNumber(101);
        unit.setNumberOfBedrooms(3);
        return unitRepository.save(unit);
    }

    
    // -------------------- Helper Methods --------------------

    private String createBooking(Amenity amenity, String userId) {
        AmenityBooking booking = new AmenityBooking();
        booking.setAmenity(amenity);
        booking.setUserId(userId);
        booking.setStartTime(LocalDateTime.now().plusDays(1));
        booking.setEndTime(LocalDateTime.now().plusDays(1).plusHours(1));
        booking = amenityBookingRepository.save(booking);
        return booking.getBookingId();
    }
}
