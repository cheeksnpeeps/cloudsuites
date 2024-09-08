package com.cloudsuites.framework.webapp.rest.amenity.dto.features;

import com.cloudsuites.framework.services.amenity.entities.AmenityType;
import com.cloudsuites.framework.webapp.rest.amenity.dto.AmenityDto;
import com.cloudsuites.framework.webapp.rest.property.dto.Views;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonTypeName("SWIMMING_POOL")
public class SwimmingPoolDto extends AmenityDto {

    @JsonView({Views.AmenityView.class, Views.BuildingView.class})
    @Schema(description = "Type of the amenity", example = "SWIMMING_POOL")
    @NotNull(message = "Type is mandatory")
    private AmenityType type;

    @JsonView(Views.AmenityView.class)
    @Schema(description = "Indicates if a lifeguard is present", example = "true")
    private Boolean hasLifeguard;

    @JsonView(Views.AmenityView.class)
    @Schema(description = "Average depth of the swimming pool in meters", example = "1.8")
    private Double depth;

    @JsonView(Views.AmenityView.class)
    @Schema(description = "Maximum depth of the swimming pool in meters", example = "3.5")
    private Double maxDepth;

    @JsonView(Views.AmenityView.class)
    @Schema(description = "Minimum depth of the swimming pool in meters", example = "0.5")
    private Double minDepth;

    @JsonView(Views.AmenityView.class)
    @Schema(description = "Size of the swimming pool in square meters", example = "150.0")
    private Double poolSize;

    @JsonView(Views.AmenityView.class)
    @Schema(description = "Maximum number of people allowed in the pool at once", example = "50")
    private Integer maxCapacity;

    @JsonView(Views.AmenityView.class)
    @Schema(description = "Temperature of the pool water in degrees Celsius", example = "25.0")
    private Double waterTemperature;

    @JsonView(Views.AmenityView.class)
    @Schema(description = "Chlorine level in the pool water", example = "1.5")
    private Double chlorineLevel;

    @JsonView(Views.AmenityView.class)
    @Schema(description = "Indicates if the pool is heated", example = "true")
    private Boolean hasHeating;

    @JsonView(Views.AmenityView.class)
    @Schema(description = "Indicates if the pool is located indoors", example = "false")
    private Boolean isIndoor;

    @JsonView(Views.AmenityView.class)
    @Schema(description = "Indicates if children are allowed in the pool", example = "true")
    private Boolean childrenAllowed;

    @JsonView(Views.AmenityView.class)
    @Schema(description = "Maximum age for children allowed without adult supervision", example = "12")
    private Integer maxAgeForChildren;

    @JsonView(Views.AmenityView.class)
    @Schema(description = "Features that improve accessibility for disabled individuals", example = "Ramp access, handrails")
    private String accessibilityFeatures;

    @JsonView(Views.AmenityView.class)
    @Schema(description = "Indicates if showering is required before using the pool", example = "true")
    private Boolean showerRequired;

    @JsonView(Views.AmenityView.class)
    @Schema(description = "Hours reserved for lap swimming", example = "06:00-08:00")
    private String lapSwimmingHours;

    @JsonView(Views.AmenityView.class)
    @Schema(description = "Hours reserved for recreational swimming", example = "09:00-18:00")
    private String recreationalSwimmingHours;

    @JsonView(Views.AmenityView.class)
    @Schema(description = "Hours reserved for adult swimming", example = "19:00-21:00")
    private String adultSwimTime;

    @JsonView(Views.AmenityView.class)
    @Schema(description = "Indicates if the pool has slides", example = "true")
    private Boolean hasSlides;

    @JsonView(Views.AmenityView.class)
    @Schema(description = "Indicates if the pool has a diving board", example = "true")
    private Boolean hasDivingBoard;

    @JsonView(Views.AmenityView.class)
    @Schema(description = "Indicates if the pool area includes a Jacuzzi", example = "true")
    private Boolean hasJacuzzi;

    @JsonView(Views.AmenityView.class)
    @Schema(description = "Temperature of the Jacuzzi water in degrees Celsius", example = "37.0")
    private Double jacuzziTemperature;

    @JsonView(Views.AmenityView.class)
    @Schema(description = "Indicates if there is a poolside bar", example = "true")
    private Boolean hasPoolBar;

    @JsonView(Views.AmenityView.class)
    @Schema(description = "Schedule for pool cleaning and maintenance", example = "Daily at 2 AM")
    private String cleaningSchedule;

    @JsonView(Views.AmenityView.class)
    @Schema(description = "Fee for private swimming lessons", example = "50.00")
    private BigDecimal privateLessonsFee;

    @JsonView(Views.AmenityView.class)
    @Schema(description = "Fee for renting the pool for private events", example = "200.00")
    private BigDecimal poolRentalFee;
}
