package com.cloudsuites.framework.modules.notification;

import com.cloudsuites.framework.modules.notification.model.EmailNotificationRequest;
import com.cloudsuites.framework.modules.notification.model.PushNotificationRequest;
import com.cloudsuites.framework.modules.notification.model.SmsNotificationRequest;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class NotificationServiceImpl {

    private static final Logger logger = LoggerFactory.getLogger(NotificationServiceImpl.class);
    private static final String SMS_API_URL = "https://api.twilio.com/2010-04-01/Accounts/{accountSid}/Messages.json";
    private static final String PUSH_NOTIFICATION_TITLE_KEY = "title";
    private static final String PUSH_NOTIFICATION_BODY_KEY = "body";

    private final JavaMailSender mailSender;
    private final RestTemplate restTemplate; // For sending SMS

    @Autowired
    public NotificationServiceImpl(JavaMailSender mailSender, RestTemplate restTemplate) {
        this.mailSender = mailSender;
        this.restTemplate = restTemplate;
    }

    public void sendPushNotification(PushNotificationRequest request) {
        Message message = Message.builder()
                .setToken(request.getToken())
                .putData(PUSH_NOTIFICATION_TITLE_KEY, request.getTitle())
                .putData(PUSH_NOTIFICATION_BODY_KEY, request.getMessage())
                .build();

        try {
            String response = FirebaseMessaging.getInstance().send(message);
            logger.info("Push notification sent successfully: {}", response);
        } catch (Exception e) {
            logger.error("Failed to send push notification to {}: {}", request.getToken(), e.getMessage());
            // Handle specific exceptions as needed
        }
    }

    public void sendEmailNotification(EmailNotificationRequest request) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(request.getTo());
        message.setSubject(request.getSubject());
        message.setText(request.getBody());

        try {
            mailSender.send(message);
            logger.info("Email sent successfully to: {}", request.getTo());
        } catch (Exception e) {
            logger.error("Failed to send email to {}: {}", request.getTo(), e.getMessage());
            // Handle specific exceptions as needed
        }
    }

    public void sendSmsNotification(SmsNotificationRequest request) {
        String payload = String.format("To=%s&From=%s&Body=%s", request.getTo(), request.getFrom(), request.getMessage());

        try {
            restTemplate.postForEntity(SMS_API_URL, payload, String.class);
            logger.info("SMS sent successfully to: {}", request.getTo());
        } catch (Exception e) {
            logger.error("Failed to send SMS to {}: {}", request.getTo(), e.getMessage());
            // Handle specific exceptions as needed
        }
    }
}
