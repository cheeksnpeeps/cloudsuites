package com.cloudsuites.framework.webapp.rest.amenity.dto;

import com.cloudsuites.framework.services.property.personas.entities.Tenant;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AmenityBookingCalendarDto {

    @Schema(description = "Tenant details")
    private Tenant tenant;

    @Schema(description = "List of amenities with their schedules and booked slots")
    private List<AmenityScheduleDto> amenities;

}