package com.cloudsuites.framework.modules.otp;

import com.cloudsuites.framework.services.otp.OtpService;
import com.twilio.exception.ApiException;
import com.twilio.http.TwilioRestClient;
import com.twilio.rest.verify.v2.service.Verification;
import com.twilio.rest.verify.v2.service.VerificationCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TwilioOtpService implements OtpService {

    private static final Logger logger = LoggerFactory.getLogger(TwilioOtpService.class);
    private final TwilioRestClient twilioRestClient;
    @Value("${twilio.accountSid}")
    private String accountSid;
    @Value("${twilio.serviceSid}")  // Add this property to your application.properties
    private String serviceSid;
    @Value("${twilio.authToken}")
    private String authToken;

    public TwilioOtpService() {
        this.twilioRestClient = new TwilioRestClient.Builder(accountSid, authToken).build();
    }

    public String sendOtp(String to) {
        try {
            Verification verification = Verification.creator(
                            serviceSid,  // Use service SID here
                            to,
                            "sms")
                    .create();
            logger.info("OTP sent to {}: {}", to, verification.getStatus());
            return verification.getStatus();
        } catch (ApiException e) {
            logger.error("Error sending OTP to {}: {}", to, e.getMessage());
            throw e;  // Rethrow or handle according to your application's needs
        }
    }

    public boolean verifyOtp(String twilioPhoneNumber, String otpCode) {
        try {
            VerificationCheck verificationCheck = VerificationCheck.creator(serviceSid, otpCode)
                    .setTo(twilioPhoneNumber)
                    .create();
            boolean isApproved = verificationCheck.getStatus().equals("approved");
            logger.info("OTP verification for {}: {} {}", twilioPhoneNumber, isApproved ? "approved" : "denied");
            return isApproved;
        } catch (ApiException e) {
            logger.error("Error verifying OTP for {}: {}", twilioPhoneNumber, e.getMessage());
            return false;  // Return false or handle according to your application's needs
        }
    }
}
