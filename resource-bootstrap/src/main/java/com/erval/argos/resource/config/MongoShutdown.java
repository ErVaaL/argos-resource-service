package com.erval.argos.resource.config;

import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

import com.mongodb.client.MongoClient;

/**
 * Ensures Mongo connections are closed gracefully on shutdown.
 * <p>
 * Invoked via {@link jakarta.annotation.PreDestroy} when the Spring context stops.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class MongoShutdown {

    private final MongoClient mongoClient;

    @PreDestroy
    public void close() {
        try {
            log.info("Closing MongoClient gracefully");
            mongoClient.close();
        } catch (Exception e) {
            log.warn("Error while closing MongoClient", e);
        }
    }
}
