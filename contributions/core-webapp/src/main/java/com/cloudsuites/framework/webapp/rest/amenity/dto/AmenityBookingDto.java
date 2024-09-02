package com.cloudsuites.framework.webapp.rest.amenity.dto;

import com.cloudsuites.framework.services.amenity.entities.booking.BookingStatus;
import com.cloudsuites.framework.webapp.rest.property.dto.Views;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.PrePersist;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AmenityBookingDto {

    private Long bookingId;

    @NotBlank
    @Schema(description = "Amenity ID")
    @JsonView({Views.AmenityBooking.class})
    private String amenityId;

    @NotBlank
    @Schema(description = "User ID")
    @JsonView({Views.AmenityBooking.class})
    private String userId;

    @NotNull
    @FutureOrPresent
    @Schema(description = "Start time of the booking", example = "2021-12-31T23:59:59")
    @JsonView({Views.AmenityBooking.class})
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime startTime;

    @NotNull
    @Future
    @Schema(description = "End time of the booking", example = "2022-01-01T00:00:00")
    @JsonView({Views.AmenityBooking.class})
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime endTime;

    @Schema(description = "Booking status")
    @JsonView({Views.AmenityBooking.class})
    private BookingStatus status;

    @JsonView({Views.AmenityBooking.class})
    @Schema(hidden = true)
    private LocalDateTime createdAt;

    @JsonView({Views.AmenityBooking.class})
    @Schema(hidden = true)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
