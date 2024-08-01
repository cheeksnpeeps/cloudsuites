package com.cloudsuites.framework.webapp.rest.user.dto;

import com.cloudsuites.framework.webapp.rest.property.dto.Views;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonView({Views.AddressView.class, Views.BuildingView.class, Views.StaffView.class, Views.CompanyView.class, Views.OwnerView.class})
public class AddressDto {

    @Schema(hidden = true)
    private String addressId;

    // Apartment or suite number (optional)
    @Schema(description = "Apartment or suite number of the address", example = "Apt 101")
    @Size(max = 20, message = "Apartment number must not exceed 20 characters")
    private String aptNumber;

    // Street number (optional)
    @Schema(description = "Street number of the address", example = "123")
    @Size(max = 10, message = "Street number must not exceed 10 characters")
    private String streetNumber;

    // Street name (mandatory)
    @NotBlank(message = "Street name is required")
    @Schema(description = "Street name of the address", example = "Main St")
    @Size(max = 100, message = "Street name must not exceed 100 characters")
    private String streetName;

    // Additional address line (optional)
    @Schema(description = "Additional address line for more specific details", example = "Near Central Park")
    @Size(max = 100, message = "Additional address line must not exceed 100 characters")
    private String addressLine2;

    // City (mandatory)
    @NotBlank(message = "City is required")
    @Schema(description = "City of the address", example = "Toronto")
    @Size(max = 50, message = "City name must not exceed 50 characters")
    private String city;

    // State/Province/Region (mandatory)
    @NotBlank(message = "State/Province/Region is required")
    @Schema(description = "State, Province or Region of the address", example = "Ontario")
    @Size(max = 50, message = "State/Province/Region name must not exceed 50 characters")
    private String stateProvinceRegion;

    // Postal or ZIP code (mandatory, format can vary by country)
    @NotBlank(message = "Postal code is required")
    @Schema(description = "Postal or ZIP code of the address", example = "M1M 1M1")
    @Pattern(regexp = "^[A-Za-z0-9\\s-]*$", message = "Postal code can only contain alphanumeric characters, spaces, and hyphens")
    private String postalCode;

    // Country (mandatory)
    @NotBlank(message = "Country is required")
    @Schema(description = "Country of the address", example = "Canada")
    @Size(max = 50, message = "Country name must not exceed 50 characters")
    private String country;

    // Optional fields for latitude and longitude
    @Schema(description = "Latitude of the address", example = "43.6532")
    private Double latitude;

    @Schema(description = "Longitude of the address", example = "-79.3832")
    private Double longitude;

}