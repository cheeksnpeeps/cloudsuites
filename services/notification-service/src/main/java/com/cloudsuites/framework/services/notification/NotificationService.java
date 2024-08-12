package com.cloudsuites.framework.services.notification;

import org.springframework.stereotype.Service;

@Service
public interface NotificationService {

    void sendPushNotification(PushNotificationRequest request);

    void sendEmailNotification(EmailNotificationRequest request);

    void sendSmsNotification(SmsNotificationRequest request);
}
