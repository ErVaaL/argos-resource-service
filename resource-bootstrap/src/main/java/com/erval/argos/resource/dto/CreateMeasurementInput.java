package com.erval.argos.resource.dto;

import com.erval.argos.core.domain.measurement.MeasurementType;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * GraphQL input for creating measurements.
 */
public record CreateMeasurementInput(
        @NotBlank String deviceId,
        @NotNull MeasurementType type,
        @Min(-1_000_000) @Max(1_000_000) double value,
        String timestamp) {
}
