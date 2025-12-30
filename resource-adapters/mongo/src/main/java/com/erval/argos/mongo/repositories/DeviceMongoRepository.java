package com.erval.argos.mongo.repositories;

import java.util.List;
import java.util.Optional;

import com.erval.argos.mongo.model.DeviceDocument;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    Optional<DeviceDocument> findByName(String name);

    Optional<DeviceDocument> findByIdAndDeletedFalse(String id);

    Optional<DeviceDocument> findByNameAndDeletedFalse(String name);

    Page<DeviceDocument> findAllByDeletedFalse(Pageable pageable);
}
