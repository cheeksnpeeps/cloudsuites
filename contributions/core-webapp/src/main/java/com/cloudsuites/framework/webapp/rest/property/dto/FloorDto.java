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

    @JsonView({Views.FloorView.class, Views.UnitView.class})
    private Long floorId;

    @NotNull(message = "Building is required")
    @JsonBackReference
    @JsonView(Views.FloorView.class)
    private BuildingDto building;

    @Valid
    @JsonManagedReference
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonView(Views.FloorView.class)
    private List<UnitDto> units;

    @Positive(message = "Floor number must be a positive number")
    @JsonView({Views.FloorView.class, Views.UnitView.class})
    private Integer floorNumber;
}
