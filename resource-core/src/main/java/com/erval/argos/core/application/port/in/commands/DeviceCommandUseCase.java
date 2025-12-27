package com.erval.argos.core.application.port.in.commands;

import com.erval.argos.core.domain.device.Device;
import com.erval.argos.core.domain.device.DeviceType;

/**
 * Use cases related to creating, updating and deleting devices.
 * <p>
 * Typical flow:
 * <ul>
 * <li>validate incoming command payloads</li>
 * <li>persist device aggregates via output ports</li>
 * <li>signal errors when invariants are broken</li>
 * </ul>
 */
public interface DeviceCommandUseCase {

    /**
     * Creates a new device.
     *
     * @param cmd command payload
     * @return persisted device
     */
    Device createDevice(CreateDeviceCommand cmd);

    /**
     * Updates an existing device.
     *
     * @param id  target device id
     * @param cmd partial update payload
     * @return updated device
     */
    Device updateDevice(String id, UpdateDeviceCommand cmd);

    /**
     * Deletes a device by id.
     *
     * @param id device identifier
     */
    void deleteDevice(String id);

    /**
     * Command used for device creation.
     * <p>
     * Expected data:
     * <ul>
     * <li>{@code name}: human-friendly label</li>
     * <li>{@code building}/{@code room}: physical location</li>
     * </ul>
     *
     * @param name     device name
     * @param type     device type
     * @param building building where the device is located
     * @param room     room where the device is located
     */
    record CreateDeviceCommand(
            String name,
            DeviceType type,
            String building,
            String room) {
    }

    /**
     * Command used for device update.
     * Fields may be null if they should not be changed.
     * <p>
     * Field behavior:
     * <ul>
     * <li>non-null values overwrite stored values</li>
     * <li>null values keep the current data</li>
     * </ul>
     *
     * @param name     device name
     * @param type     device type
     * @param building building where the device is located
     * @param room     room where the device is located
     * @param active   whether the device is active
     */
    record UpdateDeviceCommand(
            String name,
            DeviceType type,
            String building,
            String room,
            Boolean active) {
    }

}
