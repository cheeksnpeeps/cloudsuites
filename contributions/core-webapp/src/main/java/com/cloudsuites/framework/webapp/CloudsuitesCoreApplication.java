package com.cloudsuites.framework.webapp;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication(scanBasePackages = "com.cloudsuites.framework")
@ComponentScan({"com.cloudsuites.framework.modules", "com.cloudsuites.framework.services", "com.cloudsuites.framework.webapp"})
@EnableJpaRepositories({"com.cloudsuites.framework.modules"})
@EntityScan(basePackages = {"com.cloudsuites.framework.services"})
@EnableAsync
public class CloudsuitesCoreApplication {

	public static void main(String[] args) {
		SpringApplication.run(CloudsuitesCoreApplication.class, args);
	}

	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}

	@Bean
	public ObjectMapper objectMapper() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());
		mapper.registerModule(new ParameterNamesModule());
		mapper.registerModule(new SimpleModule());
		mapper.configure(DeserializationFeature.READ_ENUMS_USING_TO_STRING, true);

		// Enable support for polymorphic deserialization
		mapper.activateDefaultTyping(
				mapper.getPolymorphicTypeValidator(),
				ObjectMapper.DefaultTyping.JAVA_LANG_OBJECT,
				JsonTypeInfo.As.PROPERTY
		);

		return mapper;
	}

}
