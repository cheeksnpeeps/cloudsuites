package com.cloudsuites.framework.webapp.authentication;

import com.cloudsuites.framework.webapp.authentication.util.OtpVerificationRequest;
import com.cloudsuites.framework.webapp.authentication.util.RefreshTokenRequest;
import com.cloudsuites.framework.webapp.rest.user.dto.OwnerDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/rev/owners")
public class OwnerAuthControllerRev {

    // Register a New Owner
    @PostMapping("/register")
    public ResponseEntity<?> registerOwner(@RequestBody OwnerDto ownerDto) {
        // Implementation here
        return ResponseEntity.status(201).body("Register Owner");
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

