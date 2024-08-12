package com.cloudsuites.framework.services.notification;

import org.springframework.stereotype.Component;

@Component
public class TemplateService {

    public String getEmailTemplate(String name) {
        // Load and return email template with placeholders
        return "<h1>Welcome, " + name + "!</h1><p>Thank you for joining us.</p>";
    }

    public String getPushNotificationTemplate(String title, String body) {
        // Load and return push notification template
        return "{ \"title\": \"" + title + "\", \"body\": \"" + body + "\" }";
    }
}
