package com.cloudsuites.framework.webapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "com.cloudsuites.framework", exclude = {SecurityAutoConfiguration.class, ManagementWebSecurityAutoConfiguration.class})
@ComponentScan({"com.cloudsuites.framework.modules.property","com.cloudsuites.framework.modules.user", "com.cloudsuites.framework.services", "com.cloudsuites.framework.webapp"})
@EnableJpaRepositories({"com.cloudsuites.framework.modules"})
@EntityScan(basePackages = {"com.cloudsuites.framework.services"})

public class CloudsuitesCoreApplication {

	public static void main(String[] args) {
		SpringApplication.run(CloudsuitesCoreApplication.class, args);
	}

}
