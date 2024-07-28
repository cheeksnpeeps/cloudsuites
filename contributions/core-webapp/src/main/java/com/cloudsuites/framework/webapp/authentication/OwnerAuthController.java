package com.cloudsuites.framework.webapp.authentication;

import com.cloudsuites.framework.services.common.exception.InvalidOperationException;
import com.cloudsuites.framework.services.common.exception.NotFoundResponseException;
import com.cloudsuites.framework.services.property.features.entities.Unit;
import com.cloudsuites.framework.services.property.features.service.UnitService;
import com.cloudsuites.framework.services.property.personas.entities.Owner;
import com.cloudsuites.framework.services.property.personas.service.OwnerService;
import com.cloudsuites.framework.services.user.UserService;
import com.cloudsuites.framework.services.user.entities.Identity;
import com.cloudsuites.framework.webapp.authentication.service.OtpService;
import com.cloudsuites.framework.webapp.authentication.util.JwtTokenHelper;
import com.cloudsuites.framework.webapp.authentication.util.JwtTokenProvider;
import com.cloudsuites.framework.webapp.authentication.util.WebAppConstants;
import com.cloudsuites.framework.webapp.rest.property.dto.Views;
import com.cloudsuites.framework.webapp.rest.user.dto.OwnerDto;
import com.cloudsuites.framework.webapp.rest.user.mapper.OwnerMapper;
import com.fasterxml.jackson.annotation.JsonView;
import io.jsonwebtoken.Claims;
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
    private final UnitService unitService;
    private final OwnerService ownerService;
    private final OwnerMapper ownerMapper;
    private final JwtTokenHelper jwtTokenHelper;

    @Autowired
    public OwnerAuthController(JwtTokenProvider jwtTokenProvider, OtpService otpService,
                               UserService userService, OwnerMapper ownerMapper, UnitService unitService,
                               OwnerService ownerService, JwtTokenHelper jwtTokenHelper) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.otpService = otpService;
        this.userService = userService;
        this.ownerMapper = ownerMapper;
        this.unitService = unitService;
        this.ownerService = ownerService;
        this.jwtTokenHelper = jwtTokenHelper;
    }

    @Operation(summary = "Register an Owner", description = "Register a new owner with building and unit information")
    @PostMapping("/owner/register")
    @JsonView(Views.OwnerView.class)
    public ResponseEntity<OwnerDto> registerOwner(@PathVariable String buildingId,
                                                  @PathVariable String unitId,
                                                  @Valid @RequestBody @Parameter(description = "Owner registration details") OwnerDto ownerDto) throws NotFoundResponseException {
        logger.debug(WebAppConstants.Auth.REGISTERING_OWNER_LOG, ownerDto.getIdentity().getPhoneNumber());
        Unit unit = unitService.getUnitById(buildingId, unitId);

        Owner owner = ownerMapper.convertToEntity(ownerDto);
        owner = ownerService.createOwner(owner, unit.getBuilding(), unit);

        sendOtpToOwner(owner);

        logger.info(WebAppConstants.Auth.OWNER_REGISTERED_SUCCESS_LOG, owner.getOwnerId(), owner.getIdentity().getPhoneNumber());
        return ResponseEntity.ok(ownerMapper.convertToDTO(owner));
    }

    @Operation(summary = "Verify OTP", description = "Verify the OTP sent to the owner's phone number")
    @PostMapping("/owners/{ownerId}/verify-otp")
    public ResponseEntity<Map<String, String>> verifyOtp(
            @PathVariable String buildingId,
            @PathVariable String unitId,
            @PathVariable String ownerId,
            @RequestParam @Parameter(description = "OTP to be verified") String otp) throws NotFoundResponseException, InvalidOperationException {

        Unit unit = unitService.getUnitById(buildingId, unitId);
        Owner owner = ownerService.getOwnerById(ownerId);

        validateOwnerOwnership(unit, ownerId);

        Identity identity = owner.getIdentity();
        if (otpService.verifyOtp(identity.getPhoneNumber(), otp)) {
            String token = jwtTokenHelper.generateToken(ownerId, buildingId, unitId, identity.getUserId());
            String refreshToken = jwtTokenHelper.generateRefreshToken(ownerId, buildingId, unitId, identity.getUserId());
            otpService.invalidateOtp(identity.getPhoneNumber());
            logger.debug(WebAppConstants.Otp.OTP_VERIFIED_LOG, identity.getPhoneNumber(), ownerId);
            return ResponseEntity.ok(Map.of("token", token, "refreshToken", refreshToken));
        } else {
            logger.error(WebAppConstants.Otp.INVALID_OTP_ERROR, identity.getPhoneNumber());
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid OTP provided")); // Updated message
        }
    }

    @Operation(summary = "Refresh Token", description = "Refresh the authentication token using a valid refresh token")
    @PostMapping("/owners/{ownerId}/refresh-token")
    public ResponseEntity<Map<String, String>> refreshToken(
            @PathVariable String buildingId,
            @PathVariable String unitId,
            @PathVariable String ownerId,
            @RequestParam @Parameter(description = "Refresh token") String refreshToken) throws NotFoundResponseException, InvalidOperationException {

        Unit unit = unitService.getUnitById(buildingId, unitId);
        Owner owner = ownerService.getOwnerById(ownerId);
        validateOwnerOwnership(unit, ownerId);

        Claims claims = jwtTokenProvider.extractAllClaims(refreshToken);
        if (!jwtTokenHelper.validateTokenClaims(claims, buildingId, unitId, ownerId)) {
            logger.error(WebAppConstants.Token.INVALID_REFRESH_TOKEN_ERROR, ownerId);
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid refresh token")); // Updated message
        }

        Identity identity = userService.getUserById(claims.get(WebAppConstants.Claim.USER_ID, String.class));
        if (owner.getIdentity().getUserId().equals(identity.getUserId())) {
            String token = jwtTokenHelper.generateToken(ownerId, buildingId, unitId, identity.getUserId());
            logger.debug(WebAppConstants.Token.TOKEN_REFRESHED_SUCCESS_LOG, ownerId);
            return ResponseEntity.ok(Map.of("token", token, "refreshToken", refreshToken));
        } else {
            logger.error(WebAppConstants.Token.TOKEN_IDENTITY_MISMATCH_LOG, owner.getIdentity().getUserId(), identity.getUserId());
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid refresh token")); // Updated message
        }
    }

    private void validateOwnerOwnership(Unit unit, String ownerId) throws InvalidOperationException {
        if (!unit.getOwner().getOwnerId().equals(ownerId)) {
            logger.error(WebAppConstants.Auth.OWNER_NOT_UNIT_OWNER_ERROR, ownerId);
            throw new InvalidOperationException(WebAppConstants.Auth.OWNER_NOT_UNIT_OWNER_ERROR);
        }
    }

    private void sendOtpToOwner(Owner owner) {
        String phoneNumber = owner.getIdentity().getPhoneNumber();
        String otp = otpService.generateOtp(phoneNumber);
        logger.debug(WebAppConstants.Otp.OTP_GENERATED_LOG, phoneNumber, otp);
        logger.debug(WebAppConstants.Otp.OTP_SENT_LOG, phoneNumber);
    }
}
