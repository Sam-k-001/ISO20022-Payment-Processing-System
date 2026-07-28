// AppConfig.java
package com.fintech.payment.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * Application Configuration — Defines beans for async processing,
 * thread pools, and Jackson configuration.
 */
@Configuration
public class AppConfig {

    /**
     * Dedicated thread pool executor for async payment processing.
     * Isolates payment processing threads from the HTTP request thread pool.
     */
    @Bean(name = "paymentProcessorExecutor")
    public Executor paymentProcessorExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(20);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("payment-processor-");
        executor.setRejectedExecutionHandler(
            (r, e) -> {
                // Log and handle rejection
                throw new RuntimeException("Payment processor queue at capacity");
            }
        );
        executor.initialize();
        return executor;
    }
}
