package com.cloudsuites.framework.webapp.authentication.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@TestConfiguration
@Profile("test")
public class TestAsyncConfig {

    Logger logger = LoggerFactory.getLogger(TestAsyncConfig.class);

    @Bean
    @Primary
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // Setting corePoolSize to 1 ensures single-threaded execution for tests
        executor.setCorePoolSize(1);
        executor.setMaxPoolSize(1);
        executor.setQueueCapacity(0);
        executor.setThreadNamePrefix("TestAsyncExecutor-");
        executor.initialize();
        logger.debug("Initialized TestAsyncExecutor with corePoolSize: {}, maxPoolSize: {}, queueCapacity: {}",
                executor.getCorePoolSize(), executor.getMaxPoolSize(), executor.getQueueCapacity());
        return executor;
    }
}
