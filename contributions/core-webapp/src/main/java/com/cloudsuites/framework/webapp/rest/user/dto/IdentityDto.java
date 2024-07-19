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
@JsonView({Views.StaffView.class, Views.OwnerView.class, Views.TenantView.class, Views.UnitView.class})
public class IdentityDto {

    @Schema(hidden = true)
    private Long userId;

    @Schema(description = "Username of the user", example = "johndoe")
    private String username;

    @Schema(description = "Gender of the user", example = "MALE | FEMALE | OTHER")
    private Gender gender;

    @Schema(description = "First name of the user", example = "John")
    private String firstName;

    @Schema(description = "Last name of the user", example = "Doe")
    private String lastName;

    @Schema(description = "Phone number of the user", example = "+1234567890")
    private String phoneNumber;

    @Schema(description = "Email of the user", example = "johndoe@xyz.com")
    private String email;

}
