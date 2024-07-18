package com.cloudsuites.framework.webapp.rest.user.dto;

import com.cloudsuites.framework.services.property.entities.Building;
import com.cloudsuites.framework.services.property.entities.ManagementCompany;
import com.cloudsuites.framework.services.property.entities.StaffRole;
import com.cloudsuites.framework.webapp.rest.property.dto.Views;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StaffDto {

    @JsonView(Views.StaffView.class)
    private Long staffId;

    @JsonView(Views.StaffView.class)
    private StaffRole staffRole;

    @JsonBackReference
    @JsonView(Views.StaffView.class)
    private ManagementCompany managementCompany;

    @JsonBackReference
    @JsonView(Views.StaffView.class)
    private List<Building> buildings;
}

