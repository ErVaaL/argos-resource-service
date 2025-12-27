package com.erval.argos.mongo.repositories;

import com.erval.argos.mongo.model.DeviceDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data repository for {@link DeviceDocument} collections.
 * <p>
 * Relies on generated CRUD operations; custom queries live in the adapter
 * layer.
 */
@Repository
public interface DeviceMongoRepository extends MongoRepository<DeviceDocument, String> {

    boolean existsByName(String name);

    java.util.Optional<DeviceDocument> findByName(String name);
}
