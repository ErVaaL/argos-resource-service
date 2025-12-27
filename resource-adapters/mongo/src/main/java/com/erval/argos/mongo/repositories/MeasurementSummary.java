package com.erval.argos.mongo.repositories;

import java.time.Instant;

import com.erval.argos.core.domain.measurement.MeasurementType;

/**
 * Projection for lightweight measurement reads.
 * <p>
 * Used to fetch a subset of fields for device history without full documents.
 */
public interface MeasurementSummary {
    /**
     * @return device identifier owning the measurement
     */
    String getDeviceId();
    /**
     * @return measurement type
     */
    MeasurementType getType();
    /**
     * @return numeric value recorded
     */
    double getValue();
    /**
     * @return timestamp when the measurement occurred
     */
    Instant getTimestamp();
}
