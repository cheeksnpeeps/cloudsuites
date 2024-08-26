package com.cloudsuites.framework.services.amenity.entities.features;

import com.cloudsuites.framework.services.amenity.entities.Amenity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "swimming_pool")
public class SwimmingPool extends Amenity {

    @Column(name = "has_lifeguard")
    private Boolean hasLifeguard; // Indicates if a lifeguard is present

    @Column(name = "depth")
    private Double depth; // Average depth of the swimming pool

    @Column(name = "max_depth")
    private Double maxDepth; // Maximum depth of the swimming pool

    @Column(name = "min_depth")
    private Double minDepth; // Minimum depth of the swimming pool

    @Column(name = "pool_size")
    private Double poolSize; // Size of the swimming pool in square meters

    @Column(name = "max_capacity")
    private Integer maxCapacity; // Maximum number of people allowed in the pool at once

    @Column(name = "water_temperature")
    private Double waterTemperature; // Temperature of the pool water

    @Column(name = "chlorine_level")
    private Double chlorineLevel; // Chlorine level in the pool water

    @Column(name = "has_heating")
    private Boolean hasHeating; // Indicates if the pool is heated

    @Column(name = "is_indoor")
    private Boolean isIndoor; // Indicates if the pool is located indoors

    @Column(name = "children_allowed")
    private Boolean childrenAllowed; // Indicates if children are allowed in the pool

    @Column(name = "max_age_for_children")
    private Integer maxAgeForChildren; // Maximum age for children allowed without adult supervision

    @Column(name = "accessibility_features")
    private String accessibilityFeatures; // Features that improve accessibility for disabled individuals

    @Column(name = "shower_required")
    private Boolean showerRequired; // Indicates if showering is required before using the pool

    @Column(name = "lap_swimming_hours")
    private String lapSwimmingHours; // Hours reserved for lap swimming

    @Column(name = "recreational_swimming_hours")
    private String recreationalSwimmingHours; // Hours reserved for recreational swimming

    @Column(name = "adult_swim_time")
    private String adultSwimTime; // Hours reserved for adult swimming

    @Column(name = "has_slides")
    private Boolean hasSlides; // Indicates if the pool has slides

    @Column(name = "has_diving_board")
    private Boolean hasDivingBoard; // Indicates if the pool has a diving board

    @Column(name = "has_jacuzzi")
    private Boolean hasJacuzzi; // Indicates if the pool area includes a Jacuzzi

    @Column(name = "jacuzzi_temperature")
    private Double jacuzziTemperature; // Temperature of the Jacuzzi water

    @Column(name = "has_pool_bar")
    private Boolean hasPoolBar; // Indicates if there is a poolside bar

    @Column(name = "cleaning_schedule")
    private String cleaningSchedule; // Schedule for pool cleaning and maintenance

    @Column(name = "private_lessons_fee")
    private BigDecimal privateLessonsFee; // Fee for private swimming lessons

    @Column(name = "pool_rental_fee")
    private BigDecimal poolRentalFee; // Fee for renting the pool for private events
}
