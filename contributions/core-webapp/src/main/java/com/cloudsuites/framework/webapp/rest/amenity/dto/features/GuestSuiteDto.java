package com.cloudsuites.framework.webapp.rest.amenity.dto.features;

import com.cloudsuites.framework.webapp.rest.amenity.dto.AmenityDto;
import com.cloudsuites.framework.webapp.rest.property.dto.Views;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonTypeName("GUEST_SUITE")
public class GuestSuiteDto extends AmenityDto {

    @JsonView(Views.AmenityView.class)
    @Schema(description = "Number of bedrooms in the guest suite", example = "2")
    private Integer numberOfBedrooms;

    @JsonView(Views.AmenityView.class)
    @Schema(description = "Indicates if a kitchen is available", example = "true")
    private Boolean hasKitchen;

    @JsonView(Views.AmenityView.class)
    @Schema(description = "Nightly rental fee for the guest suite", example = "150.00")
    private BigDecimal nightlyRentalFee;
}

