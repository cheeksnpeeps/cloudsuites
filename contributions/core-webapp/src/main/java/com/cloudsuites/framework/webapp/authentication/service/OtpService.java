package com.cloudsuites.framework.webapp.authentication.service;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class OtpService {

    private final Map<String, String> otpStorage = new HashMap<>();
    private final Map<String, String> userIdentifierStorage = new HashMap<>(); // Storage for user identifiers

    private final Random random = new Random();

    public String generateOtp(String userIdentifier) {
        String otp = String.format("%04d", random.nextInt(10000));
        otpStorage.put(userIdentifier, otp);
        // Send the OTP via SMS using a third-party service like Twilio
        return otp;
    }

    public boolean verifyOtp(String userIdentifier, String otp) {
        String storedOtp = otpStorage.get(userIdentifier);
        return storedOtp != null && storedOtp.equals(otp);
    }

    public void invalidateOtp(String userIdentifier) {
        otpStorage.remove(userIdentifier);
    }
}
