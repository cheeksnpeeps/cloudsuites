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

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonTypeName("LIBRARY")
public class LibraryDto extends AmenityDto {



    @JsonView(Views.AmenityView.class)
    @Schema(description = "Number of books available in the library", example = "1000")
    private Integer numberOfBooks;

    @JsonView(Views.AmenityView.class)
    @Schema(description = "Indicates if computers are available for public use", example = "true")
    private Boolean hasComputers;

    @JsonView(Views.AmenityView.class)
    @Schema(description = "Maximum seating capacity of the library", example = "30")
    private Integer seatingCapacity;
}
