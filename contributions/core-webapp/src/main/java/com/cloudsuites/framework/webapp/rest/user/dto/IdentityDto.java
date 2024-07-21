package com.cloudsuites.framework.webapp.rest.user.dto;

import com.cloudsuites.framework.services.user.entities.Gender;
import com.cloudsuites.framework.webapp.rest.property.dto.Views;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown=true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class IdentityDto {

    @Schema(hidden = true)
    @JsonView({Views.StaffView.class, Views.OwnerView.class, Views.TenantView.class, Views.UnitView.class})
    private Long userId;

    @JsonView({Views.StaffView.class, Views.OwnerView.class, Views.TenantView.class, Views.UnitView.class})
    @Schema(description = "Username of the user", example = "johndoe")
    private String username;

    @JsonView({Views.StaffView.class, Views.OwnerView.class, Views.TenantView.class, Views.UnitView.class})
    @Schema(description = "Gender of the user", example = "MALE | FEMALE | OTHER")
    private Gender gender;

    @JsonView({Views.StaffView.class, Views.OwnerView.class, Views.TenantView.class, Views.UnitView.class})
    @Schema(description = "First name of the user", example = "John")
    private String firstName;

    @JsonView({Views.StaffView.class, Views.OwnerView.class, Views.TenantView.class, Views.UnitView.class})
    @Schema(description = "Last name of the user", example = "Doe")
    private String lastName;

    @JsonView({Views.StaffView.class, Views.OwnerView.class, Views.TenantView.class, Views.UnitView.class})
    @Schema(description = "Phone number of the user", example = "+1234567890")
    private String phoneNumber;

    @JsonView({Views.StaffView.class, Views.OwnerView.class, Views.TenantView.class, Views.UnitView.class})
    @Schema(description = "Email of the user", example = "johndoe@xyz.com")
    private String email;

}
