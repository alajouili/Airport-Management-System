package com.airport;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point of the backend.
 *
 * @SpringBootApplication tells Spring to scan every class in the package
 * "com.airport" and its sub-packages (controllers, services, repositories,
 * entities...) and to configure the application automatically.
 */
@SpringBootApplication
public class AirportApplication {

    public static void main(String[] args) {
        SpringApplication.run(AirportApplication.class, args);
    }
}
