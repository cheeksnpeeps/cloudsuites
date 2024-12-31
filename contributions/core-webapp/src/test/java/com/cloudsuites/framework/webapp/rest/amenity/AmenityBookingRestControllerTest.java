package com.cloudsuites.framework.webapp.rest.amenity;

import com.cloudsuites.framework.modules.amenity.repository.AmenityBookingRepository;
import com.cloudsuites.framework.modules.amenity.repository.AmenityRepository;
import com.cloudsuites.framework.modules.amenity.repository.AvailabilityRepository;
import com.cloudsuites.framework.modules.property.features.repository.BuildingRepository;
import com.cloudsuites.framework.modules.property.features.repository.UnitRepository;
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

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureWebTestClient
@ActiveProfiles("test")
@AutoConfigureMockMvc
class AmenityBookingRestControllerTest {

    @Autowired
    private TaskExecutor taskExecutor;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebTestClient webTestClient;

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
    private TenantRepository tenantRepository;

    @Autowired
    private UnitRepository unitRepository;

    @Autowired
    private AvailabilityRepository availabilityRepository;

    private String validAmenityId;
    private String validUserId;
    private String accessToken;

    private Amenity amenity;
    private AdminTestHelper adminTestHelper;

    Logger logger = LoggerFactory.getLogger(AmenityBookingRestControllerTest.class);

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private StaffRepository staffRepository;
    @Autowired
    private UserRoleRepository userRoleRepository;
    @Autowired
    private AdminRepository adminRepository;

    @BeforeEach
    void setUp() throws Exception {
        logger.info("Setting up test with TaskExecutor: " + taskExecutor.getClass().getName());

        // Clear the database before every test
        clearDatabase();

        // Create necessary entities
        Building building = createBuilding("BuildingA");
        String validBuildingId = building.getBuildingId();
        logger.info("Created building with ID: " + validBuildingId);

        amenity = createAmenity(validBuildingId);
        validAmenityId = amenity.getAmenityId();
        logger.info("Created amenity with ID: " + validAmenityId);

        // Verify that the amenity was actually saved
        Amenity retrievedAmenity = amenityRepository.findById(validAmenityId).orElse(null);
        logger.info("Retrieved Amenity: " + retrievedAmenity);

        // Initialize other setup data
        Unit unit = createUnit(validBuildingId);
        String validUnitId = unit.getUnitId();

        Tenant tenant = createUser("testUser", "+14165557777");
        validUserId = tenant.getTenantId();
        logger.info("Created tenant with ID: " + validUserId);

        // Initialize AdminTestHelper for authentication
        adminTestHelper = new AdminTestHelper(mockMvc, objectMapper, validBuildingId, validUnitId);
        accessToken = adminTestHelper.registerAdminAndGetToken("testRegisterAdmin", "+14166024668");

        // Log IDs to ensure correctness
        logger.debug("Setup Created Amenity ID: {}", amenity.getAmenityId());
        logger.debug("Setup Created Tenant ID: {}", tenant.getTenantId());
        logger.debug("Setup Access Token: {}", accessToken);
    }

    // Helper method to clean the database before tests
    private void clearDatabase() {
        amenityBookingRepository.deleteAll();
        availabilityRepository.deleteAll();
        amenityRepository.deleteAll();
        tenantRepository.deleteAll();
        userRoleRepository.deleteAll();
        staffRepository.deleteAll();
        unitRepository.deleteAll();
        buildingRepository.deleteAll();
        adminRepository.deleteAll();
    }

    // Helper method to create a building entity
    private Building createBuilding(String name) {
        Building building = new Building();
        building.setName(name);
        return buildingRepository.save(building);
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
        return amenity;
    }

    // Helper method to create a tenant user
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

    // Helper method to create a unit
    private Unit createUnit(String buildingId) {
        Unit unit = new Unit();
        unit.setBuilding(buildingRepository.findById(buildingId).orElseThrow());
        unit.setUnitNumber(101);
        unit.setNumberOfBedrooms(3);
        return unitRepository.save(unit);
    }

    // -------------------- Test Methods --------------------

    @Test
    void bookAmenity_shouldReturnCreatedBooking() {
        Amenity retrievedAmenity = amenityRepository.findById(validAmenityId).orElse(null);
        assertThat(retrievedAmenity).isNotNull();
        logger.info("Test - Retrieved Amenity: " + retrievedAmenity.getAmenityId());
        // Set up booking details
        AmenityBookingDto bookingDto = new AmenityBookingDto();
        bookingDto.setStartTime(LocalDateTime.now().plusDays(1).withHour(12).withMinute(0).withSecond(0).withNano(0));
        bookingDto.setEndTime(LocalDateTime.now().plusDays(1).withHour(13).withMinute(0).withSecond(0).withNano(0));

        // Perform the request and capture the result
        webTestClient.post()
                .uri("/api/v1/amenities/{amenityId}/tenants/{tenantId}/bookings", validAmenityId, validUserId)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + accessToken)
                .bodyValue(bookingDto)
                .exchange()
                .expectStatus().isCreated() // Expect a 201 Created status
                .expectBody(AmenityBookingDto.class) // Expect body to be of type AmenityBookingDto
                .consumeWith(response -> {
                    AmenityBookingDto createdBooking = response.getResponseBody();
                    assertThat(createdBooking).isNotNull();
                    assertThat(createdBooking.getAmenityId()).isEqualTo(validAmenityId);
                    assertThat(createdBooking.getUserId()).isEqualTo(validUserId);

                    // Verify the booking exists in the database
                    Optional<AmenityBooking> bookingOpt = amenityBookingRepository.findById(createdBooking.getBookingId());
                    assertTrue(bookingOpt.isPresent(), "Booking should exist in the database"); // Ensure the booking is present
                    AmenityBooking booking = bookingOpt.get();
                    assertThat(booking.getUserId()).isEqualTo(validUserId);
                    assertThat(booking.getStartTime()).isEqualTo(bookingDto.getStartTime());
                });
    }

    @Test
    void bookAmenity_withInvalidTime_shouldReturnBadRequest() {
        Amenity retrievedAmenity = amenityRepository.findById(validAmenityId).orElse(null);
        assertThat(retrievedAmenity).isNotNull();
        logger.info("Test - Retrieved Amenity: " + retrievedAmenity.getAmenityId());

        AmenityBookingDto bookingDto = new AmenityBookingDto();
        bookingDto.setStartTime(LocalDateTime.now().withHour(0));
        bookingDto.setEndTime(LocalDateTime.now().withHour(0));

        webTestClient.post()
                .uri("/api/v1/amenities/{amenityId}/tenants/{tenantId}/bookings", validAmenityId, validUserId)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + accessToken)
                .bodyValue(bookingDto)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(String.class)
                .consumeWith(response -> {
                    String errorMessage = response.getResponseBody();
                    assertThat(errorMessage).contains("Booking period cannot be zero");
                });
    }
}
