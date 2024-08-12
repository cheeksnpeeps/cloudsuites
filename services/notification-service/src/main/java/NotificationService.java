package com.cloudsuites.framework.services.notification;

import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    public void sendPushNotification(PushNotificationRequest request) {
        // Logic to send push notification using Firebase or other service
    }

    public void sendEmailNotification(EmailNotificationRequest request) {
        // Logic to send email notification using Spring Mail
    }

    public void sendSmsNotification(SmsNotificationRequest request) {
        // Logic to send SMS using Twilio or other SMS gateway
    }
}
