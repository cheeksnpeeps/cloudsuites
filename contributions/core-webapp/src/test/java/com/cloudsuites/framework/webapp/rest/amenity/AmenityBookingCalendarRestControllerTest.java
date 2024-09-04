package com.cloudsuites.framework.webapp.rest.amenity;

import com.cloudsuites.framework.modules.amenity.repository.AmenityBookingRepository;
import com.cloudsuites.framework.modules.amenity.repository.AmenityRepository;
import com.cloudsuites.framework.modules.property.personas.repository.TenantRepository;
import com.cloudsuites.framework.modules.user.repository.UserRepository;
import com.cloudsuites.framework.services.amenity.entities.Amenity;
import com.cloudsuites.framework.services.amenity.entities.AmenityType;
import com.cloudsuites.framework.services.amenity.entities.booking.AmenityBooking;
import com.cloudsuites.framework.services.amenity.entities.features.SwimmingPool;
import com.cloudsuites.framework.services.property.personas.entities.Tenant;
import com.cloudsuites.framework.services.property.personas.entities.TenantStatus;
import com.cloudsuites.framework.services.user.entities.Identity;
import com.cloudsuites.framework.webapp.authentication.utils.AdminTestHelper;
import com.cloudsuites.framework.webapp.rest.amenity.dto.AmenityBookingCalendarDto;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AmenityBookingCalendarRestControllerTest {

    String tenantId;
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

    @BeforeEach
    void setUp() throws Exception {
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

        bookingRepository.findAll().forEach(System.out::println);

        mockMvc.perform(withAuth(get("/api/v1/amenities/{amenityId}/bookings/calendar", amenityId))
                        .param("startTime", startTime.toString())
                        .param("endTime", endTime.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    AmenityBookingCalendarDto calendarDto = objectMapper.readValue(jsonResponse, AmenityBookingCalendarDto.class);

                    assertThat(calendarDto.getAmenityId()).isEqualTo(amenityId);
                    assertThat(calendarDto.getBookedSlots()).hasSize(1);
                    assertThat(calendarDto.getAvailableSlots()).isNotEmpty();
                });
    }

    @Test
    void testGetBookingCalendarForTenant_ValidRequest() throws Exception {

        bookingRepository.findAll().forEach(System.out::println);

        mockMvc.perform(withAuth(get("/api/v1/amenities/{amenityId}/tenants/{tenantId}/bookings/calendar", amenityId, tenantId))
                        .param("startTime", startTime.toString())
                        .param("endTime", endTime.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    AmenityBookingCalendarDto calendarDto = objectMapper.readValue(jsonResponse, AmenityBookingCalendarDto.class);

                    assertThat(calendarDto.getAmenityId()).isEqualTo(amenityId);
                    assertThat(calendarDto.getBookedSlots()).hasSize(1);
                    assertThat(calendarDto.getBookedSlots().get(0).getBookingId()).isNotNull();
                    assertThat(calendarDto.getBookedSlots().get(0).getUserId()).isEqualTo(tenantId);
                    assertThat(calendarDto.getAvailableSlots()).isNotEmpty();
                });
    }

    @Test
    void testGetBookingCalendar_AmenityNotFound() throws Exception {
        String nonExistentAmenityId = "nonExistentAmenityId";
        mockMvc.perform(withAuth(get("/api/v1/amenities/{amenityId}/bookings/calendar", nonExistentAmenityId))
                        .param("startTime", startTime.toString())
                        .param("endTime", endTime.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Amenity not found")));
    }

    @Test
    void testGetBookingCalendar_InvalidTimeRange() throws Exception {
        // Set an invalid time range where startTime is after endTime
        LocalDateTime invalidStartTime = endTime.plusDays(1);

        mockMvc.perform(withAuth(get("/api/v1/amenities/{amenityId}/bookings/calendar", amenityId))
                        .param("startTime", invalidStartTime.toString())
                        .param("endTime", startTime.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());
    }

    @Test
    void testGetBookingCalendar_NoBookings() throws Exception {
        // Clear all bookings
        bookingRepository.deleteAll();

        mockMvc.perform(withAuth(get("/api/v1/amenities/{amenityId}/bookings/calendar", amenityId))
                        .param("startTime", startTime.toString())
                        .param("endTime", endTime.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    AmenityBookingCalendarDto calendarDto = objectMapper.readValue(jsonResponse, AmenityBookingCalendarDto.class);

                    assertThat(calendarDto.getAmenityId()).isEqualTo(amenityId);
                    assertThat(calendarDto.getBookedSlots()).isEmpty();
                    assertThat(calendarDto.getAvailableSlots()).isNotEmpty();
                });
    }

    private Tenant createUser(String username, String phoneNumber) {
        Tenant tenant = new Tenant();
        Identity identity = new Identity();
        identity.setEmail("tenantA@gmail.com");
        identity = userRepository.save(identity);
        tenant.setIdentity(identity);
        tenant.setStatus(TenantStatus.ACTIVE);
        tenant = tenantRepository.save(tenant);
        return tenant;
    }
}
