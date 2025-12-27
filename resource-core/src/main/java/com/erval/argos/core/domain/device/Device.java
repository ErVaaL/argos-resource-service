package com.erval.argos.core.domain.device;

/**
 * Domain aggregate representing a physical or virtual sensor.
 * <p>
 * Example payload:
 * <ul>
 * <li>{@code name}: "Room 101 CO2 sensor"</li>
 * <li>{@code building}: "Main", {@code room}: "101"</li>
 * <li>{@code type}: {@link DeviceType#CO2}</li>
 * </ul>
 *
 * @param id       unique identifier
 * @param name     human-readable name, e.g., "Room 101 CO2 sensor"
 * @param type     type of device, e.g. TEMP, HUMIDITY, CO2, MOTION
 * @param building building identifier, e.g. "A" or "Main"
 * @param room     room identifier within the building, e.g. "101"
 * @param active   whether the device is currently active
 */
public record Device(String id, String name, DeviceType type, String building, String room, boolean active, DeviceConfig config) {
}
