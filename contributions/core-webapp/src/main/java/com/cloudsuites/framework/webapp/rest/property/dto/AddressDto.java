package com.cloudsuites.framework.webapp.rest.property.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown=true)
@JsonView(Views.AddressView.class)
public class AddressDto {

    private Long addressId;

    @NotBlank(message = "Apartment number is required")
    private String aptNumber;

    @NotBlank(message = "Street number is required")
    private String streetNumber;

    @NotBlank(message = "Street name is required")
    private String streetName;

    @NotBlank(message = "City is required")
    private String city;

    @NotBlank(message = "Province is required")
    private String province;

    @NotBlank(message = "Postal code is required")
    private String postalCode;

    @NotBlank(message = "Country is required")
    private String country;
}
