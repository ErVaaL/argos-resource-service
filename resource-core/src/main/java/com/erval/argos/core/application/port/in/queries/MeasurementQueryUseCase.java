package com.erval.argos.core.application.port.in.queries;

import java.time.Instant;

import com.erval.argos.core.application.PageRequest;
import com.erval.argos.core.application.PageResult;
import com.erval.argos.core.domain.measurement.Measurement;
import com.erval.argos.core.domain.measurement.MeasurementType;

/**
 * Use case for querying stored measurements, e.g. for dashboards.
 * <p>
 * Capabilities:
 * <ul>
 *   <li>filtering by device, type, or time ranges</li>
 *   <li>pagination and sorting through {@link PageRequest}</li>
 * </ul>
 */
public interface MeasurementQueryUseCase {

    /**
     * Finds measurements matching optional criteria.
     *
     * @param filter      filter constraints; may be {@code null}
     * @param pageRequest pagination and sorting parameters
     * @return paginated measurements
     */
    PageResult<Measurement> findMeasurements(MeasurementFilter filter, PageRequest pageRequest);

    /**
     * Filter criteria for querying measurements.
     * All fields are optional. Null means "no filter" for that field.
     * <p>
     * Examples:
     * <ul>
     *   <li>Use {@code from}/{@code to} together to fetch a window</li>
     *   <li>Combine {@code deviceId} with {@code type} for a single-sensor series</li>
     * </ul>
     *
     * @param deviceId the device ID to filter by
     * @param type     the measurement type to filter by
     * @param from     the start time to filter by
     * @param to       the end time to filter by
     */
    record MeasurementFilter(
            String deviceId,
            MeasurementType type,
            Instant from,
            Instant to) {
    }
}
