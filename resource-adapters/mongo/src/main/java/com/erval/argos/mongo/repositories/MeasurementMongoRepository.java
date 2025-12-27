package com.erval.argos.mongo.repositories;

import java.util.List;

import com.erval.argos.mongo.model.MeasurementDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Spring Data repository for storing {@link MeasurementDocument} instances.
 * <p>
 * Uses generated CRUD methods; domain-specific filtering is implemented via the
 * adapter.
 */
@Repository
public interface MeasurementMongoRepository extends MongoRepository<MeasurementDocument, String> {

    /**
     * Returns a projection of measurements for a device, limited to summary fields.
     *
     * @param deviceId device identifier to filter by
     * @return summaries containing deviceId, timestamp, value, and type
     */
    @Query(value = "{ 'deviceId': ?0 }", fields = "{ 'deviceId': 1, 'timestamp': 1, 'value': 1, 'type': 1 }")
    List<MeasurementSummary> findProjectedByDeviceId(String deviceId);
}
