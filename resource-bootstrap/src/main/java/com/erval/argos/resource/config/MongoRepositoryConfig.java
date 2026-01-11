package com.erval.argos.resource.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * Enables Spring Data Mongo repositories for the adapter layer.
 */
@Configuration
@EnableMongoRepositories(basePackages = "com.erval.argos.mongo.repositories")
public class MongoRepositoryConfig {
}
