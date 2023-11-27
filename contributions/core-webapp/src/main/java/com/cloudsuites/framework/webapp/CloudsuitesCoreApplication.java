package com.cloudsuites.framework.webapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class, ManagementWebSecurityAutoConfiguration.class})
@ComponentScan({"com.cloudsuites.framework.modules", "com.cloudsuites.framework.services", "com.cloudsuites.framework.webapp"})

public class CloudsuitesCoreApplication {

	public static void main(String[] args) {
		SpringApplication.run(CloudsuitesCoreApplication.class, args);
	}

}
