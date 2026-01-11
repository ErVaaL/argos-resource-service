package com.erval.argos.resource.dto;

import com.erval.argos.core.domain.device.DeviceType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * GraphQL input for creating devices with required identity and location data.
 *
 * @param name     device name
 * @param type     device type
 * @param building building identifier
 * @param room     room identifier
 */
public record CreateDeviceInput(
        @NotBlank @Size(min = 3, max = 120) String name,
        @NotNull DeviceType type,
        @NotBlank @Size(min = 1, max = 60) String building,
        @NotBlank @Size(min = 1, max = 60) String room) {
}
