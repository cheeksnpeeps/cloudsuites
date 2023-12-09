package com.cloudsuites.framework.webapp.rest.property.dto;

import com.fasterxml.jackson.annotation.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown=true)
@JsonView(Views.FloorView.class)
public class FloorDto {

    private Long floorId;

    @NotNull(message = "Building is required")
    @JsonBackReference
    private BuildingDto building;

    @Valid
    @JsonManagedReference
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<UnitDto> units;

    @Positive(message = "Floor number must be a positive number")
    private Integer floorNumber;
}
