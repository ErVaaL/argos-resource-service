package com.erval.argos.application.measurement;

import com.erval.argos.core.application.PageRequest;
import com.erval.argos.core.application.PageResult;
import com.erval.argos.core.application.port.in.commands.MeasurementCommandUseCase;
import com.erval.argos.core.application.port.in.queries.MeasurementQueryUseCase;
import com.erval.argos.core.application.port.out.DeviceRepositoryPort;
import com.erval.argos.core.application.port.out.MeasurementRepositoryPort;
import com.erval.argos.core.domain.measurement.Measurement;

import java.time.Instant;

/**
 * Application service coordinating measurement commands and queries.
 * <p>
 * Responsibilities:
 * <ul>
 * <li>verifying referenced devices exist</li>
 * <li>defaulting timestamps when the caller omits them</li>
 * <li>delegating filtering and paging to the repository port</li>
 * </ul>
 */
public record MeasurementService(MeasurementRepositoryPort measurementRepo, DeviceRepositoryPort deviceRepo)
        implements MeasurementCommandUseCase, MeasurementQueryUseCase {

    /**
     * Creates a measurement for a device, defaulting timestamp to now when absent.
     * <ul>
     * <li>Ensures the device exists.</li>
     * <li>If the timestamp is {@code null}, the current time is used.</li>
     * </ul>
     *
     * @param cmd incoming measurement data
     * @return created measurement in domain form
     * @throws IllegalArgumentException if the device referenced by the command
     *                                  doesn't exist
     */
    @Override
    public Measurement createMeasurement(CreateMeasurementCommand cmd) {
        deviceRepo().findById(cmd.deviceId())
                .orElseThrow(() -> new IllegalArgumentException("Device not found: " + cmd.deviceId()));

        Instant timestamp = cmd.timestamp() != null
                ? cmd.timestamp()
                : Instant.now();

        Measurement measurement = new Measurement(
                null,
                cmd.deviceId(),
                cmd.type(),
                cmd.value(),
                0,
                timestamp,
                null);

        return measurementRepo().save(measurement);
    }

    /**
     * Removes a measurement by id. No-op if missing.
     *
     * @param id identifier of the measurement to delete
     */
    @Override
    public void deleteMeasurement(String id) {
        measurementRepo().deleteById(id);
    }

    /**
     * Finds measurements matching the given filter and pagination settings.
     * <p>
     * Defaults:
     * <ul>
     * <li>sorts by {@code timestamp} when no sort field is provided</li>
     * <li>returns empty content when nothing matches the filter</li>
     * </ul>
     *
     * @param filter      filter criteria (device, type, time range); may be
     *                    {@code null}
     * @param pageRequest pagination and sorting parameters
     * @return measurements matching the filter wrapped in a paginated result
     */
    @Override
    public PageResult<Measurement> findMeasurements(MeasurementFilter filter, PageRequest pageRequest) {
        return measurementRepo().findByFilter(filter, pageRequest);
    }

}
