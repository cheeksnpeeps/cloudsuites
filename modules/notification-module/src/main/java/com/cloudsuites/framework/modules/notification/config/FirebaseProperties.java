package com.cloudsuites.framework.modules.notification.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;

@Getter
@Setter
@ConfigurationProperties(prefix = "gcp.firebase")
public class FirebaseProperties {
    /**
     * @return the serviceAccount
     * @param serviceAccount the serviceAccount to set
     */
    private Resource serviceAccount;
}