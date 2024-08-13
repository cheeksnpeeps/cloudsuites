package com.cloudsuites.framework.modules.notification.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SmsNotificationRequest {

    private String to;
    private String from;
    private String message;
}
