package com.project.config;

import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CircuitBreakerPreloadConfig {

    private final CircuitBreakerRegistry circuitBreakerRegistry;

    public CircuitBreakerPreloadConfig(CircuitBreakerRegistry circuitBreakerRegistry) {
        this.circuitBreakerRegistry = circuitBreakerRegistry;
    }

    @PostConstruct
    public void preloadCircuitBreakers() {
        circuitBreakerRegistry.circuitBreaker("filestorageCB");
        circuitBreakerRegistry.circuitBreaker("signatureCB");
        circuitBreakerRegistry.circuitBreaker("userCB");
        circuitBreakerRegistry.circuitBreaker("templateCB");
        circuitBreakerRegistry.circuitBreaker("notificationCB");
    }
}
