package com.cloudsuites.framework.webapp.authentication;

import com.cloudsuites.framework.services.common.exception.NotFoundResponseException;
import com.cloudsuites.framework.services.property.features.entities.Building;
import com.cloudsuites.framework.services.property.features.entities.Unit;
import com.cloudsuites.framework.services.property.features.service.BuildingService;
import com.cloudsuites.framework.services.property.features.service.UnitService;
import com.cloudsuites.framework.services.property.personas.entities.Owner;
import com.cloudsuites.framework.services.property.personas.service.OwnerService;
import com.cloudsuites.framework.services.user.UserService;
import com.cloudsuites.framework.webapp.authentication.service.OtpService;
import com.cloudsuites.framework.webapp.authentication.util.JwtTokenProvider;
import com.cloudsuites.framework.webapp.authentication.util.OtpVerificationRequest;
import com.cloudsuites.framework.webapp.authentication.util.RefreshTokenRequest;
import com.cloudsuites.framework.webapp.rest.property.dto.Views;
import com.cloudsuites.framework.webapp.rest.user.dto.OwnerDto;
import com.cloudsuites.framework.webapp.rest.user.mapper.OwnerMapper;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/buildings/{buildingId}/units/{unitId}")
public class OwnerAuthController {

    private static final Logger logger = LoggerFactory.getLogger(OwnerAuthController.class);
    private final JwtTokenProvider jwtTokenProvider;
    private final OtpService otpService;
    private final UserService userService;
    private final BuildingService buildingService;
    private final UnitService unitService;
    private final OwnerService ownerService;
    private final OwnerMapper ownerMapper;

    @Autowired
    public OwnerAuthController(JwtTokenProvider jwtTokenProvider, OtpService otpService,
                               UserService userService,
                               OwnerMapper ownerMapper, BuildingService buildingService,
                               UnitService unitService, OwnerService ownerService) {
        this.ownerMapper = ownerMapper;
        this.jwtTokenProvider = jwtTokenProvider;
        this.otpService = otpService;
        this.ownerService = ownerService;
        this.userService = userService;
        this.buildingService = buildingService;
        this.unitService = unitService;
    }


    @Operation(summary = "Register an Owner", description = "Register a new owner with building and unit information")
    @PostMapping("/owner/register")
    @JsonView(Views.OwnerView.class)
    public ResponseEntity<OwnerDto> registerOwner(@PathVariable String buildingId,
                                                  @PathVariable String unitId,
                                                  @Valid @RequestBody @Parameter(description = "Owner registration details") OwnerDto ownerDto) throws NotFoundResponseException {
        // Log the phone number of the owner being registered
        logger.debug("Registering owner with phone number: {}", ownerDto.getIdentity().getPhoneNumber());

        Building building = buildingService.getBuildingById(buildingId);
        if (building == null) throw new NotFoundResponseException("Building not found for ID: " + buildingId);

        Unit unit = unitService.getUnitById(buildingId, unitId);
        if (unit == null) throw new NotFoundResponseException("Unit not found for ID: " + unitId);

        // if the owner of the unit is still
        // Convert OwnerDto to Owner entity
        Owner owner = ownerMapper.convertToEntity(ownerDto);
        owner = ownerService.creatOwner(owner, buildingId, unitId);

        // Handle OTP generation and sending
        String phoneNumber = owner.getIdentity().getPhoneNumber();
        String otp = otpService.generateOtp(phoneNumber);
        logger.debug("Generated OTP for phone number {}: {}", phoneNumber, otp);
        sendOtp(otp, phoneNumber);
        logger.debug("Sent OTP to phone number: {}", phoneNumber);

        // Log the building ID and success message
        logger.info("Owner registered successfully with ID: {} and phone number: {}", owner.getOwnerId(), phoneNumber);

        // Convert entity to DTO and return
        OwnerDto ownerDtoResponse = ownerMapper.convertToDTO(owner);
        logger.debug("Converted Owner entity to OwnerDto with ID: {}", ownerDtoResponse.getOwnerId());

        return ResponseEntity.ok(ownerDtoResponse);
    }


    // Verify OTP
    @PostMapping("/{ownerId}/verify-otp")
    public ResponseEntity<?> verifyOtp(@PathVariable String ownerId, @RequestBody OtpVerificationRequest request) {
        // Implementation here
        return ResponseEntity.ok().body("Verify OTP");
    }

    // Refresh Token
    @PostMapping("/{ownerId}/refresh-token")
    public ResponseEntity<?> refreshToken(@PathVariable String ownerId, @RequestBody RefreshTokenRequest request) {
        // Implementation here
        return ResponseEntity.ok().body("Refresh Token");
    }

    private void sendOtp(String otp, String phoneNumber) {
        // Send OTP to tenant (this would involve integrating with an SMS/email service)
        // Assuming you have an implementation for sending the OTP
    }
}

