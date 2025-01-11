package com.clover.circuitbreaker;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.circuitbreaker.event.CircuitBreakerOnErrorEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CircuitBreakerConfig {

	private static final Logger logger = LoggerFactory.getLogger(CircuitBreakerConfig.class);

	public CircuitBreakerConfig(CircuitBreakerRegistry circuitBreakerRegistry) {
		CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker("vetServiceBreaker");

		circuitBreaker.getEventPublisher().onStateTransition(event -> {
			logger.info("CircuitBreaker '{}' transitioned to {} state", event.getCircuitBreakerName(),
					event.getStateTransition());
		}).onError(event -> {
			logger.error("CircuitBreaker '{}' recorded an error: {}", event.getCircuitBreakerName(),
					((CircuitBreakerOnErrorEvent) event).getThrowable().getMessage());
		});
	}
}
