package com.cloudsuites.framework.webapp.authentication;

import com.cloudsuites.framework.services.common.exception.InvalidOperationException;
import com.cloudsuites.framework.services.common.exception.NotFoundResponseException;
import com.cloudsuites.framework.services.property.features.entities.Building;
import com.cloudsuites.framework.services.property.features.entities.Unit;
import com.cloudsuites.framework.services.property.features.service.BuildingService;
import com.cloudsuites.framework.services.property.features.service.UnitService;
import com.cloudsuites.framework.services.property.personas.entities.Owner;
import com.cloudsuites.framework.services.property.personas.entities.UserType;
import com.cloudsuites.framework.services.property.personas.service.OwnerService;
import com.cloudsuites.framework.services.user.UserService;
import com.cloudsuites.framework.services.user.entities.Identity;
import com.cloudsuites.framework.webapp.authentication.service.OtpService;
import com.cloudsuites.framework.webapp.authentication.util.JwtTokenProvider;
import com.cloudsuites.framework.webapp.authentication.util.RefreshTokenRequest;
import com.cloudsuites.framework.webapp.rest.property.dto.Views;
import com.cloudsuites.framework.webapp.rest.user.dto.OwnerDto;
import com.cloudsuites.framework.webapp.rest.user.mapper.OwnerMapper;
import com.fasterxml.jackson.annotation.JsonView;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/buildings/{buildingId}/units/{unitId}")
@Tags(value = {@Tag(name = "Owner Authentication", description = "Operations related to owner authentication")})
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


    @Operation(summary = "Verify OTP", description = "Verify the OTP sent to the owner's phone number")
    @PostMapping("/owners/{ownerId}/verify-otp")
    public ResponseEntity<Map<String, String>> verifyOtp(
            @PathVariable String buildingId,
            @PathVariable String unitId,
            @PathVariable String ownerId,
            @RequestParam @Parameter(description = "OTP to be verified") String otp) throws NotFoundResponseException, InvalidOperationException {

        Building building = buildingService.getBuildingById(buildingId);
        if (building == null) throw new NotFoundResponseException("Building not found for ID: " + buildingId);

        Unit unit = unitService.getUnitById(buildingId, unitId);
        if (unit == null) throw new NotFoundResponseException("Unit not found for ID: " + unitId);

        Owner owner = ownerService.getOwnerById(ownerId);
        // Check if the owner is the owner of the unit
        if (!unit.getOwner().getOwnerId().equals(ownerId)) {
            logger.error("Owner is not the owner of the unit");
            throw new InvalidOperationException("Owner is not the owner of the unit");
        }
        Identity identity = owner.getIdentity();

        if (otpService.verifyOtp(identity.getPhoneNumber(), otp)) {
            String token = generateToken(ownerId, buildingId, unitId, identity.getUserId());
            String refreshToken = generateRefreshToken(ownerId, buildingId, unitId, identity.getUserId());
            otpService.invalidateOtp(identity.getPhoneNumber());
            logger.debug("OTP verified successfully for ownerId: {}", ownerId);
            return ResponseEntity.ok(Map.of("token", token, "refreshToken", refreshToken));
        } else {
            logger.error("Invalid OTP for phone number: {}", identity.getPhoneNumber());
            return ResponseEntity.status(400).body(Map.of("error", "Invalid OTP"));
        }
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

    private String generateToken(String ownerId, String buildingId, String unitId, String userId) {
        JwtBuilder claims = Jwts.builder()
                .subject(ownerId)
                .audience()
                .add(UserType.TENANT.name())
                .and()
                .claim("personaId", ownerId)
                .claim("buildingId", buildingId)
                .claim("unitId", unitId)
                .claim("userId", userId);
        return jwtTokenProvider.generateToken(claims);
    }

    private String generateRefreshToken(String ownerId, String buildingId, String unitId, String userId) {
        JwtBuilder claims = Jwts.builder()
                .subject(ownerId)
                .audience()
                .add(UserType.TENANT.name())
                .and()
                .claim("personaId", ownerId)
                .claim("buildingId", buildingId)
                .claim("unitId", unitId)
                .claim("userId", userId);
        return jwtTokenProvider.generateRefreshToken(claims);
    }
}

