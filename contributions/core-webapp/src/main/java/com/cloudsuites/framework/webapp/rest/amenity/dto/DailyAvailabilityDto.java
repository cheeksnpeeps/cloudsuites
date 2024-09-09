package com.cloudsuites.framework.webapp.rest.amenity.dto;

import com.cloudsuites.framework.webapp.rest.property.dto.Views;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Daily availability for an amenity")
public class DailyAvailabilityDto {

    @JsonView(Views.AmenityView.class)
    @Schema(description = "Day of the week", example = "MONDAY")
    @NotNull(message = "Day of the week is required")
    private DayOfWeek dayOfWeek;

    @JsonView(Views.AmenityView.class)
    @Schema(description = "Opening time of the amenity for this day", example = "08:00:00")
    @JsonFormat(pattern = "HH:mm:ss", shape = JsonFormat.Shape.STRING)
    @NotNull(message = "Open time is required")
    private LocalTime openTime;

    @JsonView(Views.AmenityView.class)
    @Schema(description = "Closing time of the amenity for this day", example = "20:00:00")
    @JsonFormat(pattern = "HH:mm:ss", shape = JsonFormat.Shape.STRING)
    @NotNull(message = "Close time is required")
    private LocalTime closeTime;
}

