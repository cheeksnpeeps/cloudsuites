package com.cloudsuites.framework.webapp.authentication;

import com.cloudsuites.framework.modules.jwt.JwtTokenProvider;
import com.cloudsuites.framework.services.common.exception.InvalidOperationException;
import com.cloudsuites.framework.services.common.exception.NotFoundResponseException;
import com.cloudsuites.framework.services.common.exception.UsernameAlreadyExistsException;
import com.cloudsuites.framework.services.otp.OtpService;
import com.cloudsuites.framework.services.property.personas.entities.Staff;
import com.cloudsuites.framework.services.property.personas.service.StaffService;
import com.cloudsuites.framework.services.user.UserService;
import com.cloudsuites.framework.services.user.entities.Identity;
import com.cloudsuites.framework.services.user.entities.UserType;
import com.cloudsuites.framework.webapp.authentication.util.JwtTokenHelper;
import com.cloudsuites.framework.webapp.authentication.util.WebAppConstants;
import com.cloudsuites.framework.webapp.rest.property.dto.Views;
import com.cloudsuites.framework.webapp.rest.user.dto.StaffDto;
import com.cloudsuites.framework.webapp.rest.user.mapper.StaffMapper;
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
@RequestMapping("/api/v1/auth/staff")
@Tags(value = {@Tag(name = "Staff Authentication", description = "Operations related to staff authentication")})
public class StaffAuthController {

    private static final Logger logger = LoggerFactory.getLogger(StaffAuthController.class);
    private final JwtTokenProvider jwtTokenProvider;
    private final OtpService otpService;
    private final UserService userService;
    private final StaffMapper staffMapper;
    private final JwtTokenHelper jwtTokenHelper;
    private final StaffService staffService;

    @Autowired
    public StaffAuthController(JwtTokenProvider jwtTokenProvider, OtpService otpService,
                               UserService userService, StaffMapper staffMapper,
                               JwtTokenHelper jwtTokenHelper, StaffService staffService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.otpService = otpService;
        this.userService = userService;
        this.staffMapper = staffMapper;
        this.jwtTokenHelper = jwtTokenHelper;
        this.staffService = staffService;
    }

    @Operation(summary = "Register a Staff Member", description = "Register a new staff member with identity details")
    @PostMapping("/companies/{companyId}/buildings/{buildingId}/register")
    @JsonView(Views.StaffView.class)
    public ResponseEntity<StaffDto> registerStaff(@Valid @RequestBody @Parameter(description = "Staff registration details") StaffDto staffDto,
                                                  @PathVariable String buildingId,
                                                  @PathVariable String companyId)
            throws UsernameAlreadyExistsException, InvalidOperationException, NotFoundResponseException {
        logger.debug(WebAppConstants.Auth.REGISTERING_STAFF_LOG, staffDto.getIdentity().getPhoneNumber());
        Staff staff = staffMapper.convertToEntity(staffDto);
        staff = staffService.createStaff(staff, companyId, buildingId);
        sendOtpToStaff(staff);
        logger.info(WebAppConstants.Auth.STAFF_REGISTERED_SUCCESS_LOG, staff.getStaffId(), staff.getIdentity().getPhoneNumber());
        return ResponseEntity.ok(staffMapper.convertToDTO(staff));
    }

    @Operation(summary = "Request OTP", description = "Request a new OTP code to be sent to the staff's phone number")
    @PostMapping("/{staffId}/request-otp")
    public ResponseEntity<Map<String, String>> requestOtp(
            @PathVariable String staffId) throws NotFoundResponseException, InvalidOperationException {
        Staff staff = staffService.getStaffById(staffId);
        sendOtpToStaff(staff);
        return ResponseEntity.ok(Map.of("message", "OTP sent successfully"));
    }

    @Operation(summary = "Verify OTP", description = "Verify the OTP sent to the staff's phone number")
    @PostMapping("/{staffId}/verify-otp")
    public ResponseEntity<Map<String, String>> verifyOtp(
            @PathVariable String staffId,
            @RequestParam @Parameter(description = "OTP to be verified") String otp) throws NotFoundResponseException, InvalidOperationException {

        Staff staff = staffService.getStaffById(staffId);

        Identity identity = staff.getIdentity();
        if (otpService.verifyOtp(identity.getPhoneNumber(), otp)) {
            String token = jwtTokenHelper.generateToken(staffId, UserType.STAFF, identity.getUserId());
            String refreshToken = jwtTokenHelper.generateRefreshToken(staffId, UserType.STAFF, identity.getUserId());
            logger.debug(WebAppConstants.Otp.OTP_VERIFIED_LOG, identity.getPhoneNumber(), staffId);
            return ResponseEntity.ok(Map.of("token", token, "refreshToken", refreshToken));
        } else {
            logger.error(WebAppConstants.Otp.INVALID_OTP_ERROR, identity.getPhoneNumber());
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid OTP provided"));
        }
    }

    @Operation(summary = "Refresh Token", description = "Refresh the authentication token using a valid refresh token")
    @PostMapping("/{staffId}/refresh-token")
    public ResponseEntity<Map<String, String>> refreshToken(
            @PathVariable String staffId,
            @RequestParam @Parameter(description = "Refresh token") String refreshToken) throws NotFoundResponseException, InvalidOperationException {

        logger.debug("Refresh token received: {}", refreshToken);
        Staff staff = staffService.getStaffById(staffId);
        Claims claims = jwtTokenProvider.extractAllClaims(refreshToken);
        if (!jwtTokenHelper.validateTokenClaims(claims, staffId)) {
            logger.error(WebAppConstants.Token.INVALID_REFRESH_TOKEN_ERROR, staffId);
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid refresh token"));
        }

        Identity identity = userService.getUserById(claims.get(WebAppConstants.Claim.USER_ID, String.class));
        if (staff.getIdentity().getUserId().equals(identity.getUserId())) {
            String token = jwtTokenHelper.generateToken(staffId, UserType.STAFF, identity.getUserId());
            logger.debug(WebAppConstants.Token.TOKEN_REFRESHED_SUCCESS_LOG, staffId);
            return ResponseEntity.ok(Map.of("token", token, "refreshToken", refreshToken));
        } else {
            logger.error(WebAppConstants.Token.TOKEN_IDENTITY_MISMATCH_LOG, staff.getIdentity().getUserId(), identity.getUserId());
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid refresh token"));
        }
    }

    private void sendOtpToStaff(Staff staff) throws InvalidOperationException {
        String phoneNumber = staff.getIdentity().getPhoneNumber();
        if (phoneNumber == null) {
            logger.error("Phone number is null for staff: {}", staff.getStaffId());
            throw new InvalidOperationException("Phone number is required");
        }
        String otp = otpService.sendOtp(phoneNumber);
        logger.debug(WebAppConstants.Otp.OTP_GENERATED_LOG, phoneNumber, otp);
        logger.debug(WebAppConstants.Otp.OTP_SENT_LOG, phoneNumber);
    }
}
