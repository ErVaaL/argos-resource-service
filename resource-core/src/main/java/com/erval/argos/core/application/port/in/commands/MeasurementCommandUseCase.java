package com.erval.argos.core.application.port.in.commands;

import java.time.Instant;

import com.erval.argos.core.domain.measurement.Measurement;
import com.erval.argos.core.domain.measurement.MeasurementType;

/**
 * Use case for ingesting measurements from devices or an IoT gateway.
 * <p>
 * Steps:
 * <ul>
 * <li>validate payload integrity (device id, type, timestamp)</li>
 * <li>map into domain aggregates</li>
 * <li>persist via the measurement repository port</li>
 * </ul>
 */
public interface MeasurementCommandUseCase {

    /**
     * Stores a new measurement for a device.
     *
     * @param command command describing the measurement
     * @return stored measurement
     */
    Measurement createMeasurement(CreateMeasurementCommand command);

    /**
     * Deletes a measurement by its identifier.
     *
     * @param id measurement identifier
     */
    void deleteMeasurement(String id);

    /**
     * Command used for creating a measurement.
     * <p>
     * Notes:
     * <ul>
     * <li>{@code timestamp} may be null to indicate "now"</li>
     * <li>{@code value} is expected to be already validated at the edge</li>
     * </ul>
     *
     * @param deviceId  ID of the device that produced the measurement
     * @param type      type of the measurement
     * @param value     value of the measurement
     * @param timestamp timestamp when the measurement was taken
     */
    record CreateMeasurementCommand(
            String deviceId,
            MeasurementType type,
            double value,
            Instant timestamp) {
    }

}
