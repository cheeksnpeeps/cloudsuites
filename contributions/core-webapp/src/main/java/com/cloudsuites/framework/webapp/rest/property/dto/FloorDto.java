package com.cloudsuites.framework.webapp.rest.property.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown=true)
public class FloorDto {

    @Schema(hidden = true)
    @JsonView({Views.FloorView.class, Views.UnitView.class})
    private String floorId;

    @Schema(description = "Name of the floor", example = "Ground Floor")
    @JsonView({Views.FloorView.class, Views.UnitView.class})
    private String floorName;

    @Positive(message = "Floor number must be a positive number")
    @Schema(description = "Number of the floor", example = "1")
    @JsonView({Views.FloorView.class, Views.UnitView.class})
    private Integer floorNumber;

    @Schema(hidden = true)
    @JsonView(Views.FloorView.class)
    private BuildingDto building;

    @Schema(description = "Units in the floor")
    @JsonView(Views.FloorView.class)
    private List<UnitDto> units;

}
