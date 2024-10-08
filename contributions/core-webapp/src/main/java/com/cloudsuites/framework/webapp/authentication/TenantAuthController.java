package com.cloudsuites.framework.webapp.authentication;

import com.cloudsuites.framework.modules.jwt.JwtTokenProvider;
import com.cloudsuites.framework.services.common.exception.InvalidOperationException;
import com.cloudsuites.framework.services.common.exception.NotFoundResponseException;
import com.cloudsuites.framework.services.common.exception.UserAlreadyExistsException;
import com.cloudsuites.framework.services.otp.OtpService;
import com.cloudsuites.framework.services.property.features.entities.Building;
import com.cloudsuites.framework.services.property.features.entities.Unit;
import com.cloudsuites.framework.services.property.features.service.BuildingService;
import com.cloudsuites.framework.services.property.features.service.UnitService;
import com.cloudsuites.framework.services.property.personas.entities.Tenant;
import com.cloudsuites.framework.services.property.personas.entities.TenantStatus;
import com.cloudsuites.framework.services.property.personas.service.OwnerService;
import com.cloudsuites.framework.services.property.personas.service.TenantService;
import com.cloudsuites.framework.services.user.UserService;
import com.cloudsuites.framework.services.user.entities.Identity;
import com.cloudsuites.framework.services.user.entities.UserType;
import com.cloudsuites.framework.webapp.authentication.util.JwtTokenHelper;
import com.cloudsuites.framework.webapp.authentication.util.WebAppConstants;
import com.cloudsuites.framework.webapp.rest.property.dto.Views;
import com.cloudsuites.framework.webapp.rest.user.dto.TenantDto;
import com.cloudsuites.framework.webapp.rest.user.mapper.TenantMapper;
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

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth/buildings/{buildingId}/units/{unitId}")
@Tags(value = {@Tag(name = "Tenant Authentication", description = "Operations related to tenant authentication")})
public class TenantAuthController {

    private static final Logger logger = LoggerFactory.getLogger(TenantAuthController.class);
    private final JwtTokenProvider jwtTokenProvider;
    private final OtpService otpService;
    private final TenantService tenantService;
    private final UserService userService;
    private final TenantMapper tenantMapper;
    private final BuildingService buildingService;
    private final UnitService unitService;
    private final OwnerService ownerService;
    private final JwtTokenHelper jwtTokenHelper;

    @Autowired
    public TenantAuthController(JwtTokenProvider jwtTokenProvider, OtpService otpService,
                                TenantService tenantService, UserService userService,
                                TenantMapper tenantMapper, BuildingService buildingService,
                                UnitService unitService, OwnerService ownerService, JwtTokenHelper jwtTokenHelper) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.otpService = otpService;
        this.tenantService = tenantService;
        this.userService = userService;
        this.tenantMapper = tenantMapper;
        this.buildingService = buildingService;
        this.unitService = unitService;
        this.ownerService = ownerService;
        this.jwtTokenHelper = jwtTokenHelper;
    }

    // Request a new OTP for tenant registration
    @Operation(summary = "Request OTP", description = "Request an OTP for tenant registration")
    @PostMapping("/tenants/{tenantId}/request-otp")
    public ResponseEntity<Map<String, String>> requestOtp(
            @PathVariable String buildingId,
            @PathVariable String unitId,
            @PathVariable String tenantId) throws NotFoundResponseException, InvalidOperationException {
        // Generate OTP and send to the tenant
        logger.debug("Generating OTP for tenant: {}", tenantId);
        Tenant tenant = tenantService.getTenantByBuildingIdAndUnitIdAndTenantId(buildingId, unitId, tenantId);
        sendOtpToTenant(tenant);
        return ResponseEntity.ok(Map.of("message", "OTP sent successfully"));
    }

    @Operation(summary = "Register a Tenant", description = "Register a new tenant with building and unit information")
    @PostMapping("/tenants/register")
    @JsonView(Views.TenantView.class)
    public ResponseEntity<TenantDto> registerTenant(
            @PathVariable String buildingId,
            @PathVariable String unitId,
            @Valid @RequestBody @Parameter(description = "Tenant registration details") TenantDto tenantDto) throws NotFoundResponseException, UserAlreadyExistsException, InvalidOperationException {
        Unit unit = validateBuildingAndUnit(buildingId, unitId);
        // Log the phone number of the tenant being registered
        logger.debug("Registering tenant with phone number: {}", tenantDto.getIdentity().getPhoneNumber());

        // Convert TenantDto to Tenant entity
        Tenant tenant = tenantMapper.convertToEntity(tenantDto);
        // Set the building information for the tenant
        tenant.setBuilding(buildingService.getBuildingById(buildingId));
        logger.debug("Assigned building ID to tenant: {}", tenant.getBuilding() != null ? tenant.getBuilding().getBuildingId() : "null");

        // Check if the tenant is marked as a primary tenant
        if (tenantDto.getIsPrimaryTenant() != null && tenantDto.getIsPrimaryTenant()) {
            logger.debug("Tenant is marked as primary. Fetching existing tenants for unit: {}", unitId);

            // Fetch existing tenants for the specified building and unit
            List<Tenant> existingTenants = tenantService.getAllTenantsByBuildingAndUnit(buildingId, unitId, TenantStatus.ACTIVE);

            // Log basic information about existing tenants
            logger.debug("Existing tenants for unit {}: {}", unitId, existingTenants.size());

            // Deactivate existing tenants if the new tenant is primary
            for (Tenant existingTenant : existingTenants) {
                existingTenant.setStatus(TenantStatus.INACTIVE);
                tenantService.updateTenant(existingTenant.getTenantId(), existingTenant);
                logger.debug("Deactivated existing tenant ID: {}", existingTenant.getTenantId());
            }
        } else {
            logger.debug("Tenant is not marked as primary. Skipping deactivation of existing tenants.");
        }

        // Create or update the tenant
        Tenant createdTenant = tenantService.createTenant(tenant, unit);
        logger.debug("Created or updated tenant with ID: {}", tenant.getTenantId());

        // Handle owner registration if applicable
        if (tenantDto.getIsOwner() != null && tenantDto.getIsOwner()) {
            logger.debug("Tenant is also an owner. Creating or updating owner record.");
            ownerService.createOrUpdateOwner(createdTenant);
            logger.debug("Owner record created or updated for tenant ID: {}", tenant.getTenantId());
        } else {
            logger.debug("Tenant is not an owner. Skipping owner registration.");
        }

        // Handle OTP generation and sending
        String phoneNumber = tenant.getIdentity().getPhoneNumber();
        String otp = otpService.sendOtp(phoneNumber);
        logger.debug("Generated OTP for phone number {}: {}", phoneNumber, otp);
        sendOtpToTenant(tenant);
        logger.debug("Sent OTP to phone number: {}", phoneNumber);

        // Log the building ID and success message
        logger.info("Tenant building ID: {}", tenant.getBuilding() != null ? tenant.getBuilding().getBuildingId() : "null");
        logger.debug("Tenant registered successfully with ID: {} and phone number: {}", tenant.getTenantId(), phoneNumber);

        // Convert entity to DTO and return
        TenantDto tenantDtoResponse = tenantMapper.convertToDTO(tenant);
        logger.debug("Converted Tenant entity to TenantDto with ID: {}", tenantDtoResponse.getTenantId());

        return ResponseEntity.ok(tenantDtoResponse);
    }


    @Operation(summary = "Verify OTP", description = "Verify the OTP sent to the tenant's phone number")
    @PostMapping("/tenants/{tenantId}/verify-otp")
    public ResponseEntity<Map<String, String>> verifyOtp(
            @PathVariable String buildingId,
            @PathVariable String unitId,
            @PathVariable String tenantId,
            @RequestParam @Parameter(description = "OTP to be verified") String otp) throws NotFoundResponseException {

        validateBuildingAndUnit(buildingId, unitId);
        Tenant tenant = tenantService.getTenantByBuildingIdAndUnitIdAndTenantId(buildingId, unitId, tenantId);
        if (tenant.getStatus() == TenantStatus.ACTIVE || tenant.getStatus() == TenantStatus.PENDING) {
            logger.error("Tenant is inactive");
            return ResponseEntity.status(400).body(Map.of("error", "Tenant is inactive"));
        }
        Identity identity = tenant.getIdentity();

        if (otpService.verifyOtp(identity.getPhoneNumber(), otp)) {
            String token = jwtTokenHelper.generateToken(tenantId, UserType.TENANT, buildingId, unitId, identity.getUserId());
            String refreshToken = jwtTokenHelper.generateRefreshToken(tenantId, UserType.TENANT, buildingId, unitId, identity.getUserId());
            logger.debug("OTP verified successfully for tenant: {}", tenantId);
            return ResponseEntity.ok(Map.of("token", token, "refreshToken", refreshToken));
        } else {
            logger.error("Invalid OTP for phone number: {}", identity.getPhoneNumber());
            return ResponseEntity.status(400).body(Map.of("error", "Invalid OTP"));
        }
    }

    @Operation(summary = "Refresh Token", description = "Refresh the authentication token using a valid refresh token")
    @PostMapping("/tenants/{tenantId}/refresh-token")
    public ResponseEntity<Map<String, String>> refreshToken(
            @PathVariable String buildingId,
            @PathVariable String unitId,
            @PathVariable String tenantId,
            @RequestParam @Parameter(description = "Refresh token") String refreshToken) throws NotFoundResponseException {
        Tenant tenant = tenantService.getTenantByBuildingIdAndUnitIdAndTenantId(buildingId, unitId, tenantId);
        if (tenant.getStatus() == TenantStatus.ACTIVE || tenant.getStatus() == TenantStatus.PENDING) {
            logger.error("Tenant is inactive");
            return ResponseEntity.status(400).body(Map.of("error", "Tenant is inactive"));
        }
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            logger.error("Invalid refresh token");
            return ResponseEntity.status(400).body(Map.of("error", "Invalid refresh token"));
        }

        Claims claims = jwtTokenProvider.extractAllClaims(refreshToken);
        if (!validateTokenClaims(claims, buildingId, unitId, tenantId)) {
            return ResponseEntity.status(400).body(Map.of("error", "Invalid refresh token"));
        }

        Identity identity = userService.getUserById(claims.get("userId", String.class));
        if (tenant.getIdentity().getUserId().equals(identity.getUserId())) {
            String token = jwtTokenHelper.generateToken(tenantId, UserType.TENANT, buildingId, unitId, identity.getUserId());

            logger.debug("Token refreshed successfully for tenant: {}", tenantId);
            return ResponseEntity.ok(Map.of("token", token, "refreshToken", refreshToken));
        } else {
            logger.error("User identity does not match with the token claims");
            return ResponseEntity.status(400).body(Map.of("error", "Invalid refresh token"));
        }
    }


    private void sendOtpToTenant(Tenant tenant) throws InvalidOperationException {
        String phoneNumber = tenant.getIdentity().getPhoneNumber();
        if (phoneNumber == null) {
            logger.error("Phone number is null for owner: {}", tenant.getTenantId());
            throw new InvalidOperationException("Phone number is required");
        }
        String otp = otpService.sendOtp(phoneNumber);
        logger.debug(WebAppConstants.Otp.OTP_GENERATED_LOG, phoneNumber, otp);
        logger.debug(WebAppConstants.Otp.OTP_SENT_LOG, phoneNumber);
    }

    private boolean validateTokenClaims(Claims claims, String buildingId, String unitId, String tenantId) {
        String tenantIdClaim = claims.getSubject();
        String buildingIdClaim = claims.get("buildingId", String.class);
        String unitIdClaim = claims.get("unitId", String.class);
        logger.debug("Token claims: tenantId: {}, buildingId: {}, unitId: {}", tenantIdClaim, buildingIdClaim, unitIdClaim);
        logger.debug("Request parameters: tenantId: {}, buildingId: {}, unitId: {}", tenantId, buildingId, unitId);
        if (!(tenantIdClaim.equals(tenantId) && buildingIdClaim.equals(buildingId) && unitIdClaim.equals(unitId))) {
            logger.error("Token claims do not match with the request parameters");
            return false;
        }
        return true;
    }

    private Unit validateBuildingAndUnit(String buildingId, String unitId) throws NotFoundResponseException {
        Building building = buildingService.getBuildingById(buildingId);
        Unit unit = unitService.getUnitById(buildingId, unitId);

        if (building == null) {
            logger.error("Building not found for ID: {}", buildingId);
            throw new NotFoundResponseException("Building not found for ID: " + buildingId);
        }
        if (unit == null) {
            logger.error("Unit not found for ID: {}", unitId);
            throw new NotFoundResponseException("Unit not found for ID: " + unitId);
        }
        unit.setBuilding(building);
        return unit;
    }
}