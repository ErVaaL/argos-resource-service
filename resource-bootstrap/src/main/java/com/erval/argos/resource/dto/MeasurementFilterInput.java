package com.erval.argos.resource.dto;

import com.erval.argos.core.domain.measurement.MeasurementType;

import jakarta.validation.constraints.Size;

/**
 * GraphQL input used to filter measurements.
 */
public record MeasurementFilterInput(
        @Size(max = 120) String deviceId,
        MeasurementType type,
        String from,
        String to) {
}
