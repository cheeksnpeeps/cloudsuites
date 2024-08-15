package com.cloudsuites.framework.webapp.authentication;

import com.cloudsuites.framework.modules.jwt.JwtTokenProvider;
import com.cloudsuites.framework.services.common.exception.InvalidOperationException;
import com.cloudsuites.framework.services.common.exception.NotFoundResponseException;
import com.cloudsuites.framework.services.common.exception.UserAlreadyExistsException;
import com.cloudsuites.framework.services.otp.OtpService;
import com.cloudsuites.framework.services.user.AdminService;
import com.cloudsuites.framework.services.user.UserService;
import com.cloudsuites.framework.services.user.entities.Admin;
import com.cloudsuites.framework.services.user.entities.Identity;
import com.cloudsuites.framework.services.user.entities.UserType;
import com.cloudsuites.framework.webapp.authentication.util.JwtTokenHelper;
import com.cloudsuites.framework.webapp.authentication.util.WebAppConstants;
import com.cloudsuites.framework.webapp.rest.property.dto.Views;
import com.cloudsuites.framework.webapp.rest.user.dto.AdminDto;
import com.cloudsuites.framework.webapp.rest.user.mapper.AdminMapper;
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
@RequestMapping("/api/v1/auth/admins")
@Tags(value = {@Tag(name = "Admin Authentication", description = "Operations related to admin authentication")})
public class AdminAuthController {

    private static final Logger logger = LoggerFactory.getLogger(AdminAuthController.class);
    private final JwtTokenProvider jwtTokenProvider;
    private final OtpService otpService;
    private final UserService userService;
    private final AdminMapper adminMapper;
    private final JwtTokenHelper jwtTokenHelper;
    private final AdminService adminService;

    @Autowired
    public AdminAuthController(JwtTokenProvider jwtTokenProvider, OtpService otpService,
                               UserService userService, AdminMapper adminMapper,
                               JwtTokenHelper jwtTokenHelper, AdminService adminService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.otpService = otpService;
        this.userService = userService;
        this.adminMapper = adminMapper;
        this.jwtTokenHelper = jwtTokenHelper;
        this.adminService = adminService;
    }

    @Operation(summary = "Register an Admin", description = "Register a new admin with building and unit information")
    @PostMapping("/register")
    @JsonView(Views.AdminView.class)
    public ResponseEntity<AdminDto> registerAdmin(@Valid @RequestBody @Parameter(description = "Admin registration details") AdminDto adminDto)
            throws UserAlreadyExistsException, InvalidOperationException {
        logger.debug(WebAppConstants.Auth.REGISTERING_ADMIN_LOG, adminDto.getIdentity().getPhoneNumber());
        // Convert DTO to entity and save
        Admin admin = adminMapper.convertToEntity(adminDto);
        admin = adminService.createAdmin(admin);
        sendOtpToAdmin(admin);
        logger.info(WebAppConstants.Auth.ADMIN_REGISTERED_SUCCESS_LOG, admin.getAdminId(), admin.getIdentity().getPhoneNumber());
        return ResponseEntity.ok(adminMapper.convertToDTO(admin));
    }

    @Operation(summary = "Request OTP", description = "Request a new OTP code to be sent to the admin's phone number")
    @PostMapping("/{adminId}/request-otp")
    public ResponseEntity<Map<String, String>> requestOtp(
            @PathVariable String adminId) throws NotFoundResponseException, InvalidOperationException {
        Admin admin = adminService.getAdminById(adminId);
        sendOtpToAdmin(admin);
        return ResponseEntity.ok(Map.of("message", "OTP sent successfully"));
    }

    @Operation(summary = "Verify OTP", description = "Verify the OTP sent to the admin's phone number")
    @PostMapping("/{adminId}/verify-otp")
    public ResponseEntity<Map<String, String>> verifyOtp(
            @PathVariable String adminId,
            @RequestParam @Parameter(description = "OTP to be verified") String otp) throws NotFoundResponseException, InvalidOperationException {

        Admin admin = adminService.getAdminById(adminId);

        Identity identity = admin.getIdentity();
        if (otpService.verifyOtp(identity.getPhoneNumber(), otp)) {
            String token = jwtTokenHelper.generateToken(adminId, UserType.ADMIN, identity.getUserId());
            String refreshToken = jwtTokenHelper.generateRefreshToken(adminId, UserType.ADMIN, identity.getUserId());
            logger.debug(WebAppConstants.Otp.OTP_VERIFIED_LOG, identity.getPhoneNumber(), adminId);
            return ResponseEntity.ok(Map.of("token", token, "refreshToken", refreshToken));
        } else {
            logger.error(WebAppConstants.Otp.INVALID_OTP_ERROR, identity.getPhoneNumber());
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid OTP provided"));
        }
    }

    @Operation(summary = "Refresh Token", description = "Refresh the authentication token using a valid refresh token")
    @PostMapping("/{adminId}/refresh-token")
    public ResponseEntity<Map<String, String>> refreshToken(
            @PathVariable String adminId,
            @RequestParam @Parameter(description = "Refresh token") String refreshToken) throws NotFoundResponseException, InvalidOperationException {

        logger.debug("Refresh token received: {}", refreshToken);
        Admin admin = adminService.getAdminById(adminId);
        Claims claims = jwtTokenProvider.extractAllClaims(refreshToken);
        if (!jwtTokenHelper.validateTokenClaims(claims, adminId)) {
            logger.error(WebAppConstants.Token.INVALID_REFRESH_TOKEN_ERROR, adminId);
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid refresh token"));
        }

        Identity identity = userService.getUserById(claims.get(WebAppConstants.Claim.USER_ID, String.class));
        if (admin.getIdentity().getUserId().equals(identity.getUserId())) {
            String token = jwtTokenHelper.generateToken(adminId, UserType.ADMIN, identity.getUserId());
            logger.debug(WebAppConstants.Token.TOKEN_REFRESHED_SUCCESS_LOG, adminId);
            return ResponseEntity.ok(Map.of("token", token, "refreshToken", refreshToken));
        } else {
            logger.error(WebAppConstants.Token.TOKEN_IDENTITY_MISMATCH_LOG, admin.getIdentity().getUserId(), identity.getUserId());
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid refresh token"));
        }
    }

    private void sendOtpToAdmin(Admin admin) throws InvalidOperationException {
        String phoneNumber = admin.getIdentity().getPhoneNumber();
        if (phoneNumber == null) {
            logger.error("Phone number is null for admin: {}", admin.getAdminId());
            throw new InvalidOperationException("Phone number is required");
        }
        String otp = otpService.sendOtp(phoneNumber);
        logger.debug(WebAppConstants.Otp.OTP_GENERATED_LOG, phoneNumber, otp);
        logger.debug(WebAppConstants.Otp.OTP_SENT_LOG, phoneNumber);
    }
}
