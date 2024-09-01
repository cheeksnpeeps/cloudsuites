package com.cloudsuites.framework.webapp.rest.amenity.dto;

import com.cloudsuites.framework.services.amenity.entities.AmenityType;
import com.cloudsuites.framework.services.amenity.entities.MaintenanceStatus;
import com.cloudsuites.framework.services.amenity.entities.booking.BookingLimitPeriod;
import com.cloudsuites.framework.webapp.rest.property.dto.Views;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class AmenityDto {

    @JsonView({Views.AmenityView.class, Views.BuildingView.class})
    @Schema(hidden = true)
    private String amenityId;

    @JsonView({Views.AmenityView.class, Views.BuildingView.class})
    @Schema(description = "Name of the amenity", example = "Swimming Pool")
    @NotBlank(message = "Name is mandatory")
    private String name;

    @JsonView({Views.AmenityView.class, Views.BuildingView.class})
    @Schema(description = "Type of the amenity", example = "SWIMMING_POOL")
    @NotNull(message = "Type is mandatory")
    private AmenityType type;

    @JsonView({Views.AmenityView.class, Views.BuildingView.class})
    @Schema(description = "Indicates if the amenity is currently active", example = "true")
    private Boolean isActive = false;

    @JsonView({Views.AmenityView.class, Views.BuildingView.class})
    @Schema(description = "Image URL for the amenity", example = "http://example.com/image.jpg")
    private String imageUrl;

    @JsonView(Views.AmenityView.class)
    @Schema(description = "Description of the amenity", example = "A large swimming pool with a diving board")
    @Size(max = 500, message = "Description can have a maximum of 500 characters")
    private String description;

    @JsonView(Views.AmenityView.class)
    @Schema(description = "Opening time of the amenity", example = "08:00:00")
    private LocalTime openTime = LocalTime.of(8, 0);

    @JsonView(Views.AmenityView.class)
    @Schema(description = "Closing time of the amenity", example = "20:00:00")
    private LocalTime closeTime = LocalTime.of(20, 0);

    @JsonView(Views.AmenityView.class)
    @Schema(description = "Location or address of the amenity", example = "Rooftop, Building A")
    private String location;

    @JsonView(Views.AmenityView.class)
    @Schema(description = "Maximum capacity for the amenity", example = "50")
    private Integer capacity;

    @JsonView(Views.AmenityView.class)
    @Schema(description = "Indicates if booking is required for the amenity", example = "true")
    private Boolean isBookingRequired = false;

    @JsonView(Views.AmenityView.class)
    @Schema(description = "Indicates if the amenity requires a fee to use", example = "false")
    private Boolean isPaidService = false;

    @JsonView(Views.AmenityView.class)
    @Schema(description = "Hourly rate for using the amenity", example = "15.50")
    @DecimalMin(value = "0.0", inclusive = true, message = "Hourly rate must be greater than 0")
    private BigDecimal hourlyRate = BigDecimal.ZERO;

    @JsonView(Views.AmenityView.class)
    @Schema(description = "Rules and regulations for the amenity", example = "No food or drinks allowed")
    private String rules = "";

    @JsonView(Views.AmenityView.class)
    @Schema(description = "Current maintenance status of the amenity", example = "OPERATIONAL")
    private MaintenanceStatus maintenanceStatus = MaintenanceStatus.OPERATIONAL;

    @JsonView(Views.AmenityView.class)
    @Schema(description = "Number of days in advance the amenity can be booked", example = "7")
    private Integer advanceBookingPeriod = 0;

    @JsonView(Views.AmenityView.class)
    @Schema(description = "Maximum duration for which the amenity can be booked", example = "120")
    @Min(value = 1, message = "Booking duration limit must be at least 1 minute")
    private Integer bookingDurationLimit = 180;

    @JsonView(Views.AmenityView.class)
    @Schema(description = "Minimum duration for which the amenity can be booked", example = "30")
    @Min(value = 1, message = "Minimum booking duration must be at least 1 minute")
    private Integer minimumBookingDuration = 60;

    @JsonView(Views.AmenityView.class)
    @Schema(description = "Maximum number of bookings a tenant can make for this amenity", example = "5")
    @Min(value = 1, message = "Max bookings per tenant must be at least 1")
    private Integer maxBookingsPerTenant = 2;

    @JsonView(Views.AmenityView.class)
    @Schema(description = "The period for which the booking limit applies (e.g., DAILY, WEEKLY, MONTHLY)", example = "DAILY")
    private BookingLimitPeriod bookingLimitPeriod = BookingLimitPeriod.DAILY;

    @JsonView(Views.AmenityView.class)
    @Schema(description = "Image gallery URLs for the amenity")
    private Set<String> imageGallery;

    @JsonView(Views.AmenityView.class)
    @Schema(description = "Video URL for the amenity", example = "http://example.com/video.mp4")
    private String videoUrl;

    @JsonView(Views.AmenityView.class)
    @Schema(description = "Building IDs associated with the amenity")
    private List<String> buildingIds;
}
