package com.cloudsuites.framework.services.otp;

import org.springframework.stereotype.Service;

@Service
public interface OtpService {

    String sendOtp(String to);

    boolean verifyOtp(String twilioPhoneNumber, String otpCode);
}
