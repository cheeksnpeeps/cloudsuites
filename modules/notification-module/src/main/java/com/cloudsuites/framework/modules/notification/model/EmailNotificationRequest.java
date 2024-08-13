package com.cloudsuites.framework.modules.notification.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class EmailNotificationRequest {

    private String to;
    private String subject;
    private String body;
}
