package com.barisdalyanemre.userregistrationemailverification.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

@Configuration
@EnableAsync(proxyTargetClass = true)
public class AsyncConfig {
    // Default configuration to enable async processing
}
