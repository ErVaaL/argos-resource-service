package com.erval.argos.resource.dto;

import com.erval.argos.core.domain.device.DeviceType;

import jakarta.validation.constraints.Size;

/**
 * GraphQL input for filtering devices.
 */
public record DeviceFilterInput(
        @Size(max = 60) String building,
        @Size(max = 60) String room,
        DeviceType type,
        Boolean active) {
}
