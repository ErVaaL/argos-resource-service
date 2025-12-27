package com.erval.argos.resource;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Spring Boot entrypoint scanning all Argos packages (API, application, adapters).
 */
@SpringBootApplication
@ComponentScan(basePackages = "com.erval.argos")
public class ResourceServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ResourceServiceApplication.class, args);
    }
}
