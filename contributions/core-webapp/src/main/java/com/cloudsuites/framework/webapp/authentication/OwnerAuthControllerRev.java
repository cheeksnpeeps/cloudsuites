package com.cloudsuites.framework.webapp.authentication;

import com.cloudsuites.framework.services.common.exception.NotFoundResponseException;
import com.cloudsuites.framework.services.property.features.service.BuildingService;
import com.cloudsuites.framework.services.property.features.service.UnitService;
import com.cloudsuites.framework.services.property.personas.service.OwnerService;
import com.cloudsuites.framework.services.property.personas.service.TenantService;
import com.cloudsuites.framework.services.user.UserService;
import com.cloudsuites.framework.webapp.authentication.service.OtpService;
import com.cloudsuites.framework.webapp.authentication.util.JwtTokenProvider;
import com.cloudsuites.framework.webapp.authentication.util.OtpVerificationRequest;
import com.cloudsuites.framework.webapp.authentication.util.RefreshTokenRequest;
import com.cloudsuites.framework.webapp.rest.user.dto.OwnerDto;
import com.cloudsuites.framework.webapp.rest.user.dto.TenantDto;
import com.cloudsuites.framework.webapp.rest.user.mapper.TenantMapper;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/owners")
public class OwnerAuthControllerRev {

    private static final Logger logger = LoggerFactory.getLogger(OwnerAuthControllerRev.class);
    private final JwtTokenProvider jwtTokenProvider;
    private final OtpService otpService;
    private final TenantService tenantService;
    private final UserService userService;
    private final BuildingService buildingService;
    private final UnitService unitService;
    private final OwnerService ownerService;

    @Autowired
    public OwnerAuthControllerRev(JwtTokenProvider jwtTokenProvider, OtpService otpService,
                                  TenantService tenantService, UserService userService,
                                  TenantMapper tenantMapper, BuildingService buildingService,
                                  UnitService unitService, OwnerService ownerService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.otpService = otpService;
        this.tenantService = tenantService;
        this.userService = userService;
        this.buildingService = buildingService;
        this.unitService = unitService;
        this.ownerService = ownerService;
    }


    // Register a New Owner
    @PostMapping("/register")
    public ResponseEntity<?> registerOwner(@RequestBody OwnerDto ownerDto,
                                           @PathVariable String buildingId,
                                           @PathVariable String unitId,
                                           @Valid @RequestBody @Parameter(description = "Tenant registration details") TenantDto tenantDto) throws
            NotFoundResponseException {
        return ResponseEntity.ok().body("Register Owner");
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
}

