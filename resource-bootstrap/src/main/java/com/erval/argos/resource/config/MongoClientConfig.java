package com.erval.argos.resource.config;

import java.util.concurrent.TimeUnit;

import org.springframework.boot.mongodb.autoconfigure.MongoClientSettingsBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Customizes MongoDB client settings for pooling, timeouts, and retries.
 * <p>
 * Settings align with {@code application.yaml} values and ensure:
 * <ul>
 *   <li>controlled pool sizing</li>
 *   <li>bounded connect/read/server selection timeouts</li>
 *   <li>retryable writes where supported</li>
 * </ul>
 */
@Configuration
public class MongoClientConfig {

    @Bean
    public MongoClientSettingsBuilderCustomizer mongoClientSettingsBuilderCustomizer() {
        return builder -> builder
                .applyToConnectionPoolSettings(pool -> pool
                        .minSize(5)
                        .maxSize(50)
                        .maxConnectionIdleTime(300, TimeUnit.SECONDS))
                .applyToSocketSettings(socket -> socket
                        .connectTimeout(5, TimeUnit.SECONDS)
                        .readTimeout(10, TimeUnit.SECONDS))
                .applyToClusterSettings(cluster -> cluster
                        .serverSelectionTimeout(5, TimeUnit.SECONDS))
                .retryWrites(true);
    }
}
