package com.cloudsuites.framework.webapp.rest.amenity;

import com.cloudsuites.framework.modules.amenity.repository.AmenityBookingRepository;
import com.cloudsuites.framework.modules.amenity.repository.AmenityBuildingRepository;
import com.cloudsuites.framework.modules.amenity.repository.AmenityRepository;
import com.cloudsuites.framework.modules.amenity.repository.AvailabilityRepository;
import com.cloudsuites.framework.modules.property.features.repository.BuildingRepository;
import com.cloudsuites.framework.modules.property.features.repository.CompanyRepository;
import com.cloudsuites.framework.modules.property.features.repository.UnitRepository;
import com.cloudsuites.framework.modules.property.personas.repository.OwnerRepository;
import com.cloudsuites.framework.modules.property.personas.repository.StaffRepository;
import com.cloudsuites.framework.modules.property.personas.repository.TenantRepository;
import com.cloudsuites.framework.modules.user.repository.UserRepository;
import com.cloudsuites.framework.modules.user.repository.UserRoleRepository;
import com.cloudsuites.framework.services.amenity.entities.Amenity;
import com.cloudsuites.framework.services.amenity.entities.AmenityBuilding;
import com.cloudsuites.framework.services.amenity.entities.AmenityType;
import com.cloudsuites.framework.services.amenity.entities.DailyAvailability;
import com.cloudsuites.framework.services.amenity.entities.booking.AmenityBooking;
import com.cloudsuites.framework.services.amenity.entities.features.SwimmingPool;
import com.cloudsuites.framework.services.property.features.entities.Building;
import com.cloudsuites.framework.services.property.features.entities.Company;
import com.cloudsuites.framework.services.property.features.entities.Unit;
import com.cloudsuites.framework.services.property.personas.entities.*;
import com.cloudsuites.framework.services.user.entities.Identity;
import com.cloudsuites.framework.services.user.entities.UserRole;
import com.cloudsuites.framework.webapp.authentication.utils.AdminTestHelper;
import com.cloudsuites.framework.webapp.rest.amenity.dto.AmenityBookingCalendarDto;
import com.cloudsuites.framework.webapp.rest.amenity.dto.AmenityBookingDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebTestClient
@ActiveProfiles("test")
@AutoConfigureMockMvc
class BookingCalendarRestControllerTest {

    @Autowired
    private TaskExecutor taskExecutor;

    @Autowired
    private WebTestClient webTestClient;
    String tenantId;
    Logger logger = LoggerFactory.getLogger(BookingCalendarRestControllerTest.class);
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private AmenityRepository amenityRepository;
    @Autowired
    private AmenityBookingRepository bookingRepository;
    @Autowired
    private ObjectMapper objectMapper;
    private String amenityId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private AdminTestHelper adminTestHelper;
    private String accessToken;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TenantRepository tenantRepository;
    @Autowired
    private BuildingRepository buildingRepository;
    @Autowired
    private UnitRepository unitRepository;
    private String validBuildingId;
    private String validUnitId;
    @Autowired
    private StaffRepository staffRepository;
    private String staffId;
    @Autowired
    private UserRoleRepository userRoleRepository;
    @Autowired
    private CompanyRepository companyRepository;
    @Autowired
    private OwnerRepository ownerRepository;
    @Autowired
    private AmenityBookingRepository amenityBookingRepository;
    @Autowired
    private AvailabilityRepository availabilityRepository;
    private String validUserId;
    @Autowired
    private AmenityBuildingRepository amenityBuildingRepository;

    @BeforeEach
    void setUp() throws Exception {
        logger.info("Setting up test with TaskExecutor: " + taskExecutor.getClass().getName());
        userRoleRepository.deleteAll();
        staffRepository.deleteAll();
        bookingRepository.deleteAll();
        tenantRepository.deleteAll();
        buildingRepository.deleteAll();
        ownerRepository.deleteAll();
        companyRepository.deleteAll();
        companyRepository.deleteAll();
        companyRepository.deleteAll();
        amenityRepository.deleteAll();
        unitRepository.deleteAll();
        userRepository.deleteAll();

        validBuildingId = createBuilding("BuildingA").getBuildingId();
        validUnitId = createUnit(validBuildingId).getUnitId();

        // Initialize test data
        Amenity amenity = new SwimmingPool();
        amenity.setName("Test Amenity");
        amenity.setType(AmenityType.SWIMMING_POOL);
        amenity = amenityRepository.save(amenity);
        amenityId = amenity.getAmenityId();

        // Set start and end times for the bookings
        startTime = LocalDateTime.now().minusDays(1);
        endTime = LocalDateTime.now().plusDays(1);

        Tenant tenant = createUser("testUser", "+14165557777");
        tenantId = tenant.getTenantId();

        Staff staff = createStaff("staffTestUser", "+14165557777");
        staffId = staff.getStaffId();

        // Create a booking for testing
        AmenityBooking booking = new AmenityBooking();
        booking.setAmenity(amenity);
        booking.setUserId(tenant.getTenantId());
        booking.setStartTime(LocalDateTime.now().minusHours(1));
        booking.setEndTime(LocalDateTime.now().plusHours(1));
        bookingRepository.save(booking);
        adminTestHelper = new AdminTestHelper(mockMvc, objectMapper, null, null);
        accessToken = adminTestHelper.registerAdminAndGetToken("testRegisterAdmin", "+14166024668");
    }

    private MockHttpServletRequestBuilder withAuth(MockHttpServletRequestBuilder requestBuilder) {
        return requestBuilder.header("Authorization", "Bearer " + accessToken);
    }

    @Test
    void testGetBookingCalendar_ValidRequest() throws Exception {
        // Create a request payload to match the curl example
        bookAmenity();
        String requestPayload = objectMapper.writeValueAsString(new TestBookingCalendarRequest(startTime, endTime));
        logger.debug("Request payload: {}", requestPayload);

        mockMvc.perform(withAuth(post("/api/v1/buildings/{buildingId}/tenants/{tenantId}/bookings/calendar", validBuildingId, tenantId))
                        .content(requestPayload)  // Send the JSON payload
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    AmenityBookingCalendarDto calendarDto = objectMapper.readValue(jsonResponse, AmenityBookingCalendarDto.class);

                    // Additional assertions to validate the contents of calendarDto
                    assertThat(calendarDto, is(notNullValue()));
                    assertThat(calendarDto.getAmenitySchedule().getBookedSlots(), is(not(empty())));
                    jsonPath("$.amenitySchedule.bookedSlots[*].status", everyItem(equalTo("REQUESTED")));
                });
    }

    // Helper method to create an amenity entity with availability
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
        amenity = amenityRepository.save(amenity);  // Save the amenity
        availabilityRepository.saveAll(dailyAvailabilities);  // Save the availability
        amenity.setDailyAvailabilities(dailyAvailabilities);
        amenityBuildingRepository.save(new AmenityBuilding(amenityId, buildingId));
        return amenity;
    }

    private void bookAmenity() {
        Amenity retrievedAmenity = createAmenity(validBuildingId);
        String validAmenityId = retrievedAmenity.getAmenityId();
        // Set up booking details
        AmenityBookingDto bookingDto = new AmenityBookingDto();
        bookingDto.setStartTime(LocalDateTime.now().plusDays(0).withHour(12).withMinute(0).withSecond(0).withNano(0).atOffset(java.time.ZoneOffset.UTC));
        bookingDto.setEndTime(LocalDateTime.now().plusDays(0).withHour(13).withMinute(0).withSecond(0).withNano(0).atOffset(java.time.ZoneOffset.UTC));

        // Perform the request and capture the result
        webTestClient.post()
                .uri("/api/v1/amenities/{amenityId}/tenants/{tenantId}/bookings", validAmenityId, validUserId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(bookingDto)
                .exchange()
                .expectStatus().isCreated() // Expect a 201 Created status
                .expectBody(AmenityBookingDto.class) // Expect body to be of type AmenityBookingDto
                .consumeWith(response -> {
                    AmenityBookingDto createdBooking = response.getResponseBody();
                    Assertions.assertThat(createdBooking).isNotNull();
                    Assertions.assertThat(createdBooking.getAmenityId()).isEqualTo(validAmenityId);
                    Assertions.assertThat(createdBooking.getUserId()).isEqualTo(validUserId);

                    // Verify the booking exists in the database
                    Optional<AmenityBooking> bookingOpt = amenityBookingRepository.findById(createdBooking.getBookingId());
                    assertTrue(bookingOpt.isPresent(), "Booking should exist in the database"); // Ensure the booking is present
                    AmenityBooking booking = bookingOpt.get();
                    Assertions.assertThat(booking.getUserId()).isEqualTo(validUserId);
                    Assertions.assertThat(booking.getStartTime()).isEqualTo(bookingDto.getStartTime());
                });
    }

    @Test
    void testGetStaffBookingCalendar_ValidRequest() throws Exception {
        bookAmenity();
        String requestPayload = objectMapper.writeValueAsString(new TestBookingCalendarRequest(startTime, endTime));
        logger.debug("Request payload: {}", requestPayload);
        mockMvc.perform(withAuth(post("/api/v1/buildings/{buildingId}/staff/{staffId}/bookings/calendar", validBuildingId, staffId))
                        .content(requestPayload)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    AmenityBookingCalendarDto calendarDto = objectMapper.readValue(jsonResponse, AmenityBookingCalendarDto.class);

                    // Additional assertions to validate the contents of calendarDto
                    assertThat(calendarDto.getAmenitySchedule().getBookedSlots(), is(not(empty())));
                    jsonPath("$.amenitySchedule.bookedSlots[*].status", everyItem(equalTo("REQUESTED")));
                });
    }

    @Test
    void testGetBookingCalendar_TenantNotFound() throws Exception {
        String requestPayload = objectMapper.writeValueAsString(new TestBookingCalendarRequest(startTime, endTime));

        String nonExistentTenantId = "strangerDanger";
        mockMvc.perform(withAuth(post("/api/v1/buildings/{buildingId}/tenants/{tenantId}/bookings/calendar", validBuildingId, nonExistentTenantId))
                        .content(requestPayload)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Tenant not found")));
    }

    @Test
    void testGetBookingCalendar_InvalidTimeRange() throws Exception {
        // Set an invalid time range where startTime is after endTime
        LocalDateTime invalidStartTime = endTime.plusDays(1);

        String requestPayload = objectMapper.writeValueAsString(new TestBookingCalendarRequest(invalidStartTime, startTime));

        mockMvc.perform(withAuth(post("/api/v1/buildings/{buildingId}/tenants/{tenantId}/bookings/calendar", validBuildingId, tenantId))
                        .content(requestPayload)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(content().string(containsString("Start time cannot be after end time")));
    }

    @Test
    void testGetBookingCalendar_NoBookings() throws Exception {
        // Clear all bookings
        bookingRepository.deleteAll();

        String requestPayload = objectMapper.writeValueAsString(new TestBookingCalendarRequest(startTime, endTime));

        mockMvc.perform(withAuth(post("/api/v1/buildings/{buildingId}/tenants/{tenantId}/bookings/calendar", validBuildingId, tenantId))
                        .content(requestPayload)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    AmenityBookingCalendarDto calendarDto = objectMapper.readValue(jsonResponse, AmenityBookingCalendarDto.class);

                    // Assertions for no bookings
                    assertThat(calendarDto, is(notNullValue()));
                    assertThat(calendarDto.getAmenitySchedule().getBookedSlots(), is(empty()));
                });
    }

    private Building createBuilding(String name) {
        Building building = new Building();
        building.setName(name);
        return buildingRepository.save(building);
    }

    private Unit createUnit(String buildingId) {
        Unit unit = new Unit();
        Building building = buildingRepository.findById(buildingId).orElseThrow();
        unit.setBuilding(building);
        return unitRepository.save(unit);
    }

    private Tenant createUser(String username, String phoneNumber) {
        Identity identity = new Identity();
        identity.setEmail("tenantA@gmail.com");
        identity = userRepository.save(identity);
        Tenant tenant = new Tenant();
        tenant.setIdentity(identity);
        tenant.setStatus(TenantStatus.ACTIVE);
        tenant.setBuilding(buildingRepository.findById(validBuildingId).orElseThrow());
        tenant = tenantRepository.save(tenant);
        UserRole userRole = tenant.getUserRole();
        userRoleRepository.save(userRole);
        return tenant;
    }

    private Staff createStaff(String username, String phoneNumber) {
        Identity identity = new Identity();
        identity.setEmail("staff@gmail.com");
        identity = userRepository.save(identity);
        validUserId = identity.getUserId();
        Staff staff = new Staff();
        staff.setIdentity(identity);
        staff.setRole(StaffRole.BUILDING_SECURITY);
        staff.setStatus(StaffStatus.ACTIVE);
        staff.setBuilding(buildingRepository.findById(validBuildingId).orElseThrow());
        Company company = new Company();
        company.setName("Test Company 2");
        staff.setCompany(company);
        //companyRepository.save(company);
        Staff savedStaff = staffRepository.save(staff);

        UserRole userRole = savedStaff.getUserRole();
        logger.debug("Created user role: {} {} {} {}", userRole.getRole(), userRole.getIdentityId(), userRole.getPersonaId(), userRole.getUserType());
        userRoleRepository.save(userRole);
        return staff;
    }
}
