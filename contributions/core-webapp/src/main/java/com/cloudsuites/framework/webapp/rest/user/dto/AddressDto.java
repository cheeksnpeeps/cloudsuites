package com.cloudsuites.framework.webapp.rest.user.dto;

import com.cloudsuites.framework.webapp.rest.property.dto.Views;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown=true)
@JsonView({Views.AddressView.class, Views.BuildingView.class, Views.StaffView.class, Views.CompanyView.class, Views.OwnerView.class,})
public class AddressDto {

    @Schema(hidden = true)
    private String addressId;

    @NotBlank(message = "Apartment number is required")
    @Schema(description = "Apartment number of the address", example = "123")
    private String aptNumber;

    @NotBlank(message = "Street number is required")
    @Schema(description = "Street number of the address", example = "123")
    private String streetNumber;

    @NotBlank(message = "Street name is required")
    @Schema(description = "Street name of the address", example = "Main St")
    private String streetName;

    @NotBlank(message = "City is required")
    @Schema(description = "City of the address", example = "Toronto")
    private String city;

    @NotBlank(message = "Province is required")
    @Schema(description = "Province of the address", example = "Ontario")
    private String province;

    @NotBlank(message = "Postal code is required")
    @Schema(description = "Postal code of the address", example = "M1M 1M1")
    private String postalCode;

    @NotBlank(message = "Country is required")
    @Schema(description = "Country of the address", example = "Canada")
    private String country;
}
