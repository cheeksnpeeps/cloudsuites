package com.cloudsuites.framework.webapp.rest.user.dto;

import com.cloudsuites.framework.services.user.entities.Gender;
import com.cloudsuites.framework.webapp.rest.property.dto.Views;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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
    @JsonView({Views.RoleView.class, Views.StaffView.class, Views.AdminView.class, Views.OwnerView.class, Views.OwnerView.class, Views.TenantView.class, Views.UnitView.class})
    private String userId;

    @JsonView({Views.StaffView.class, Views.AdminView.class, Views.OwnerView.class, Views.TenantView.class, Views.UnitView.class})
    @Schema(description = "Gender of the user", example = "FEMALE")
    private Gender gender;

    @JsonView({Views.RoleView.class, Views.StaffView.class, Views.AdminView.class, Views.OwnerView.class, Views.TenantView.class, Views.UnitView.class})
    @Schema(description = "First name of the user", example = "John")
    @Size(min = 2, max = 20, message = "First name must be between 3 and 20 characters long")
    private String firstName;

    @JsonView({Views.RoleView.class, Views.StaffView.class, Views.AdminView.class, Views.OwnerView.class, Views.TenantView.class, Views.UnitView.class})
    @Schema(description = "Last name of the user", example = "Doe")
    @Size(min = 2, max = 20, message = "Last name must be between 3 and 20 characters long")
    private String lastName;

    @JsonView({Views.StaffView.class, Views.AdminView.class, Views.OwnerView.class, Views.TenantView.class, Views.UnitView.class})
    @Schema(description = "Phone number of the user", example = "+1234567890")
    @Size(min = 10, max = 15, message = "Phone number must be between 10 and 15 characters long")
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Phone number must be in a valid format")
    private String phoneNumber;

    @JsonView({Views.StaffView.class, Views.AdminView.class, Views.OwnerView.class, Views.TenantView.class, Views.UnitView.class})
    @Schema(description = "Email of the user", example = "johndoe@xyz.com")
    @Size(min = 5, max = 50, message = "Email must be between 5 and 50 characters long")
    @Email(message = "Email should be valid")
    @NotNull(message = "Email is mandatory")
    private String email;
}
