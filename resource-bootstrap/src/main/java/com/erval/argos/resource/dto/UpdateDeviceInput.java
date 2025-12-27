package com.erval.argos.resource.dto;

import com.erval.argos.core.domain.device.DeviceType;

import jakarta.validation.constraints.Size;

/**
 * GraphQL input for updating devices.
 */
public record UpdateDeviceInput(
        @Size(min = 3, max = 120) String name,
        DeviceType type,
        @Size(min = 1, max = 60) String building,
        @Size(min = 1, max = 60) String room,
        Boolean active) {
}
