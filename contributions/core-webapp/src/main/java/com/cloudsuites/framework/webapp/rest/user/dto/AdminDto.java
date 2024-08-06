package com.cloudsuites.framework.webapp.rest.user.dto;

import com.cloudsuites.framework.services.user.entities.AdminRole;
import com.cloudsuites.framework.services.user.entities.AdminStatus;
import com.cloudsuites.framework.webapp.rest.property.dto.Views;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AdminDto {

    @JsonView({Views.AdminView.class})
    @Schema(hidden = true)
    private String adminId;

    @JsonView({Views.AdminView.class})
    @Schema(description = "Admin identity details")
    @Valid // Ensure that the Identity object is validated
    @NotNull(message = "Identity is required")
    private IdentityDto identity;

    @JsonView(Views.AdminView.class)
    @Schema(description = "Admin status", example = "ACTIVE")
    @NotNull(message = "Status is required")
    private AdminStatus status;

    @JsonView(Views.AdminView.class)
    @Schema(description = "Role of the admin", example = "SUPER_ADMIN")
    @NotNull(message = "Role is required")
    private AdminRole role;
}
