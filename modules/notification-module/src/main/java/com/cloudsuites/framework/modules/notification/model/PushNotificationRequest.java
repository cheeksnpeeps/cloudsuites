package com.cloudsuites.framework.modules.notification.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PushNotificationRequest {

    private String title;                   // Title of the notification
    private String message;                 // Message body of the notification
    private String token;                   // Target device token for FCM
    private String topic;                   // Topic to send notification to multiple subscribers
    private String priority;                // Priority of the notification (high or normal)
    private String sound;                   // Sound to play when the notification is received
    private String badge;                   // Badge count for iOS applications
    private String clickAction;             // Action to perform when notification is clicked
    private String imageUrl;                // URL of an image to display in the notification
    private String androidChannelId;        // Channel ID for Android notifications
    private String collapseKey;             // Collapse key for grouping notifications
    private String timeToLive;              // Time to live for the notification in seconds
}
