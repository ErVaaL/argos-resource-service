package com.erval.argos.resource.dto;

import com.erval.argos.core.domain.measurement.MeasurementType;

import jakarta.validation.constraints.Size;

/**
 * GraphQL input used to filter measurements; all fields are optional.
 *
 * @param deviceId device identifier to match
 * @param type     measurement type to match
 * @param from     ISO-8601 timestamp lower bound
 * @param to       ISO-8601 timestamp upper bound
 */
public record MeasurementFilterInput(
        @Size(max = 120) String deviceId,
        MeasurementType type,
        String from,
        String to) {
}
