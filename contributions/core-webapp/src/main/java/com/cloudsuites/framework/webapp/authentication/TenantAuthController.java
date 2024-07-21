package com.cloudsuites.framework.webapp.authentication;

import com.cloudsuites.framework.services.common.exception.NotFoundResponseException;
import com.cloudsuites.framework.services.property.BuildingService;
import com.cloudsuites.framework.services.property.TenantService;
import com.cloudsuites.framework.services.property.UnitService;
import com.cloudsuites.framework.services.property.entities.Building;
import com.cloudsuites.framework.services.property.entities.Tenant;
import com.cloudsuites.framework.services.property.entities.Unit;
import com.cloudsuites.framework.services.property.entities.UserType;
import com.cloudsuites.framework.services.user.UserService;
import com.cloudsuites.framework.services.user.entities.Identity;
import com.cloudsuites.framework.webapp.authentication.service.OtpService;
import com.cloudsuites.framework.webapp.authentication.util.JwtTokenProvider;
import com.cloudsuites.framework.webapp.rest.property.dto.Views;
import com.cloudsuites.framework.webapp.rest.user.dto.TenantDto;
import com.cloudsuites.framework.webapp.rest.user.mapper.TenantMapper;
import com.fasterxml.jackson.annotation.JsonView;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/buildings/{buildingId}/units/{unitId}")
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

    @Autowired
    public TenantAuthController(JwtTokenProvider jwtTokenProvider, OtpService otpService,
                                TenantService tenantService, UserService userService,
                                TenantMapper tenantMapper, BuildingService buildingService,
                                UnitService unitService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.otpService = otpService;
        this.tenantService = tenantService;
        this.userService = userService;
        this.tenantMapper = tenantMapper;
        this.buildingService = buildingService;
        this.unitService = unitService;
    }

    @Operation(summary = "Register a Tenant", description = "Register a new tenant with building and unit information")
    @PostMapping("/tenants/register")
    @JsonView(Views.TenantView.class)
    public ResponseEntity<TenantDto> registerTenant(
            @PathVariable String buildingId,
            @PathVariable Long unitId,
            @RequestBody @Parameter(description = "Tenant registration details") TenantDto tenantDto) throws NotFoundResponseException {

        logger.debug("Registering tenant with phone number: {}", tenantDto.getIdentity().getPhoneNumber());
        Tenant tenant = tenantMapper.convertToEntity(tenantDto);
        tenant.setBuilding(buildingService.getBuildingById(buildingId));
        tenantService.createTenant(tenant, unitId);
        String phoneNumber = tenant.getIdentity().getPhoneNumber();
        String otp = otpService.generateOtp(phoneNumber);
        sendOtp(otp, phoneNumber);
        logger.info("Tenant building ID: {}", tenant.getBuilding().getBuildingId());
        logger.debug("Tenant registered successfully: {} {}", tenant.getTenantId(), phoneNumber);
            return ResponseEntity.ok(tenantMapper.convertToDTO(tenant));
    }

    @Operation(summary = "Verify OTP", description = "Verify the OTP sent to the tenant's phone number")
    @PostMapping("/tenants/{tenantId}/verify-otp")
    public ResponseEntity<Map<String, String>> verifyOtp(
            @PathVariable String buildingId,
            @PathVariable Long unitId,
            @PathVariable Long tenantId,
            @RequestParam @Parameter(description = "OTP to be verified") String otp) throws NotFoundResponseException {

        validateBuildingAndUnit(buildingId, unitId);
        Tenant tenant = tenantService.getTenantByBuildingIdAndUnitIdAndTenantId(buildingId, unitId, tenantId);
        Identity identity = tenant.getIdentity();

        if (otpService.verifyOtp(identity.getPhoneNumber(), otp)) {
            String token = generateToken(tenantId, buildingId, unitId, identity.getUserId());
            String refreshToken = generateRefreshToken(tenantId, buildingId, unitId, identity.getUserId());
            otpService.invalidateOtp(identity.getPhoneNumber());

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
            @PathVariable Long unitId,
            @PathVariable Long tenantId,
            @RequestParam @Parameter(description = "Refresh token") String refreshToken) throws NotFoundResponseException {

        if (!jwtTokenProvider.validateToken(refreshToken)) {
            logger.error("Invalid refresh token");
            return ResponseEntity.status(400).body(Map.of("error", "Invalid refresh token"));
        }

        Claims claims = jwtTokenProvider.extractAllClaims(refreshToken);
        if (!validateTokenClaims(claims, buildingId, unitId, tenantId)) {
            return ResponseEntity.status(400).body(Map.of("error", "Invalid refresh token"));
        }

        Identity identity = userService.getUserById(claims.get("userId", Long.class));
        Tenant tenant = tenantService.getTenantByBuildingIdAndUnitIdAndTenantId(buildingId, unitId, tenantId);

        if (tenant.getIdentity().getUserId().equals(identity.getUserId())) {
            String token = generateToken(tenantId, buildingId, unitId, identity.getUserId());

            logger.debug("Token refreshed successfully for tenant: {}", tenantId);
            return ResponseEntity.ok(Map.of("token", token, "refreshToken", refreshToken));
        } else {
            logger.error("User identity does not match with the token claims");
            return ResponseEntity.status(400).body(Map.of("error", "Invalid refresh token"));
        }
    }



    private void sendOtp(String otp, String phoneNumber) {
        // Send OTP to tenant (this would involve integrating with an SMS/email service)
        // Assuming you have an implementation for sending the OTP
    }

    private String generateToken(Long tenantId, String buildingId, Long unitId, Long userId) {
        JwtBuilder claims = Jwts.builder()
                .subject(tenantId.toString())
                .audience()
                .add(UserType.TENANT.name())
                .and()
                .claim("personaId", tenantId)
                .claim("buildingId", buildingId)
                .claim("unitId", unitId)
                .claim("userId", userId);
        return jwtTokenProvider.generateToken(claims);
    }

    private String generateRefreshToken(Long tenantId, String buildingId, Long unitId, Long userId) {
        JwtBuilder claims = Jwts.builder()
                .subject(tenantId.toString())
                .audience()
                .add(UserType.TENANT.name())
                .and()
                .claim("personaId", tenantId)
                .claim("buildingId", buildingId)
                .claim("unitId", unitId)
                .claim("userId", userId);
        return jwtTokenProvider.generateRefreshToken(claims);
    }

    private boolean validateTokenClaims(Claims claims, String buildingId, Long unitId, Long tenantId) {
        Long tenantIdClaim = claims.get("personaId", Long.class);
        String buildingIdClaim = claims.get("buildingId", String.class);
        Long unitIdClaim = claims.get("unitId", Long.class);

        if (!(tenantIdClaim.equals(tenantId) && buildingIdClaim.equals(buildingId) && unitIdClaim.equals(unitId))) {
            logger.error("Token claims do not match with the request parameters");
            return false;
        }
        return true;
    }

    private void validateBuildingAndUnit(String buildingId, Long unitId) throws NotFoundResponseException {
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
    }
}
