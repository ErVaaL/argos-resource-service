package com.erval.argos.resource.dto;

import com.erval.argos.core.domain.device.DeviceType;

import jakarta.validation.constraints.Size;

/**
 * GraphQL input for filtering devices; all fields are optional.
 *
 * @param building building identifier to match
 * @param room     room identifier to match
 * @param type     device type to match
 * @param active   active flag to match
 */
public record DeviceFilterInput(
        @Size(max = 60) String building,
        @Size(max = 60) String room,
        DeviceType type,
        Boolean active) {
}
