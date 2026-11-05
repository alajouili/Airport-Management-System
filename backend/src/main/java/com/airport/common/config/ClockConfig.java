package com.airport.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

/**
 * Services ask this Clock for "now" instead of calling LocalDateTime.now() directly.
 *
 * Why? Rules like "check-in opens 48 hours before departure" depend on the
 * current time. In unit tests we can give the service a fixed Clock
 * (Clock.fixed(...)) and test the rule at any moment we want.
 */
@Configuration
public class ClockConfig {

    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }
}
