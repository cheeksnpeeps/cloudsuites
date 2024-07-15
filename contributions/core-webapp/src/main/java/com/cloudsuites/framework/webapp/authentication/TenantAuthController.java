package com.cloudsuites.framework.webapp.authentication;

import com.cloudsuites.framework.services.common.exception.NotFoundResponseException;
import com.cloudsuites.framework.services.property.BuildingService;
import com.cloudsuites.framework.services.property.TenantService;
import com.cloudsuites.framework.services.property.UnitService;
import com.cloudsuites.framework.services.property.entities.Tenant;
import com.cloudsuites.framework.services.user.UserService;
import com.cloudsuites.framework.services.user.entities.Identity;
import com.cloudsuites.framework.webapp.authentication.service.OtpService;
import com.cloudsuites.framework.webapp.authentication.util.JwtTokenProvider;
import com.cloudsuites.framework.webapp.rest.user.dto.TenantDto;
import com.cloudsuites.framework.webapp.rest.user.mapper.IdentityMapper;
import com.cloudsuites.framework.webapp.rest.user.mapper.TenantMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
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
    private final UserDetailsService userDetailsService;

    private final BuildingService buildingService;
    private final UnitService unitService;

    private final IdentityMapper identityMapper;

    @Autowired
    public TenantAuthController(JwtTokenProvider jwtTokenProvider, OtpService otpService,
                                TenantService tenantService, UserService userService,
                                TenantMapper tenantMapper, UserDetailsService userDetailsService, BuildingService buildingService, UnitService unitService, IdentityMapper identityMapper) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.otpService = otpService;
        this.tenantService = tenantService;
        this.userService = userService;
        this.tenantMapper = tenantMapper;
        this.userDetailsService = userDetailsService;
        this.buildingService = buildingService;
        this.unitService = unitService;
        this.identityMapper = identityMapper;
    }

    @Operation(summary = "Register a Tenant", description = "Register a new tenant with building and unit information")
    @PostMapping("/tenants/register")
    public ResponseEntity<TenantDto> registerTenant(
            @PathVariable Long buildingId,
            @PathVariable Long unitId,
            @RequestBody @Parameter(description = "Tenant registration details") TenantDto tenantDto)
            throws NotFoundResponseException {

        logger.info("Registering tenant with phone number: {}", tenantDto.getIdentity().getPhoneNumber());

        try {
            // Create a new user identity
            Identity identity = userService.createUser(identityMapper.convertToEntity(tenantDto.getIdentity()));

            // Map DTO to entity
            Tenant tenant = tenantMapper.convertToEntity(tenantDto);
            tenant.setIdentity(identity);

            // Query building and unit entities
            tenant.setBuilding(buildingService.getBuildingById(buildingId));
            tenant.setUnit(unitService.getUnitById(buildingId,unitId));

            // Create tenant in the database
            tenant = tenantService.createTenant(tenant);

            // Generate OTP
            String otp = otpService.generateOtp(identity.getPhoneNumber());

            // Send OTP to tenant (this would involve integrating with an SMS/email service)
            // Assuming you have an implementation for sending the OTP

            return ResponseEntity.ok(tenantMapper.convertToDTO(tenant));
        }  catch (Exception e) {
            logger.error("Error occurred while registering tenant: {}", e.getMessage());
            return ResponseEntity.status(500).body(null);  // Adjust the response as needed
        }
    }

    @Operation(summary = "Verify OTP", description = "Verify the OTP sent to the tenant's phone number")
    @PostMapping("/tenants/{tenantId}/verify-otp")
    public ResponseEntity<Map<String, String>> verifyOtp(
            @Parameter(description = "Phone number of the tenant") @RequestParam String phoneNumber,
            @Parameter(description = "OTP to be verified") @RequestParam String otp) {

        if (otpService.verifyOtp(phoneNumber, otp)) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(phoneNumber);
                String token = jwtTokenProvider.generateToken(userDetails);
                String refreshToken = jwtTokenProvider.generateRefreshToken(userDetails);
                otpService.invalidateOtp(phoneNumber);
                return ResponseEntity.ok(Map.of("token", token, "refreshToken", refreshToken));
        } else {
            logger.error("Invalid OTP for phone number: {}", phoneNumber);
            return ResponseEntity.status(400).body(Map.of("error", "Invalid OTP"));
        }
    }

    @Operation(summary = "Refresh Token", description = "Refresh the authentication token using a valid refresh token")
    @PostMapping("/tenants/{tenantId}/refresh-token")
    public ResponseEntity<Map<String, String>> refreshToken(
            @Parameter(description = "Refresh token") @RequestParam String refreshToken) {

        if (jwtTokenProvider.validateToken(refreshToken)) {
            String phoneNumber = jwtTokenProvider.extractUsername(refreshToken);
            UserDetails userDetails = userDetailsService.loadUserByUsername(phoneNumber);
            String token = jwtTokenProvider.generateToken(userDetails);
            return ResponseEntity.ok(Map.of("token", token));
        } else {
            logger.error("Invalid refresh token");
            return ResponseEntity.status(400).body(Map.of("error", "Invalid refresh token"));
        }
    }
}
