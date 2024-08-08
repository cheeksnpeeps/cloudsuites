package com.cloudsuites.framework.webapp.authentication.utils;

import com.cloudsuites.framework.services.otp.OtpService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("test")
public class TestTwilioConfig {

    @Bean
    public OtpService otpService() {
        return new OtpService() {
            @Override
            public String sendOtp(String to) {
                // Simulate sending OTP
                return "mock-sent"; // Mock response
            }

            @Override
            public boolean verifyOtp(String twilioPhoneNumber, String otpCode) {
                // Simulate verifying OTP
                return "123456".equals(otpCode); // Always approve this mock OTP
            }
        };
    }
}

