package com.cloudsuites.framework.modules.otp;

import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SmsService {

    @Value("${twilio.phoneNumber}")
    private String twilioPhoneNumber;

    public void sendSms(String to, String messageBody) {
        Message.creator(
                        new PhoneNumber(to),
                        new PhoneNumber(twilioPhoneNumber),
                        messageBody)
                .create();
    }
}

