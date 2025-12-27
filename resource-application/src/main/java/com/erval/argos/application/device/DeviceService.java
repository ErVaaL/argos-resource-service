package com.erval.argos.application.device;

import java.util.Optional;
import java.util.UUID;

import com.erval.argos.core.application.PageRequest;
import com.erval.argos.core.application.PageResult;
import com.erval.argos.core.application.port.in.commands.DeviceCommandUseCase;
import com.erval.argos.core.application.port.in.queries.DeviceQueryUseCase;
import com.erval.argos.core.application.port.out.DeviceRepositoryPort;
import com.erval.argos.core.domain.device.Device;
import com.erval.argos.core.domain.device.DeviceType;

/**
 * Application service orchestrating device commands and queries.
 * <p>
 * Responsibilities:
 * <ul>
 * <li>validating and persisting device aggregates</li>
 * <li>translating query filters into repository calls</li>
 * <li>managing default values such as generated identifiers</li>
 * </ul>
 */
public record DeviceService(DeviceRepositoryPort repo) implements DeviceCommandUseCase, DeviceQueryUseCase {

    @Override
    public Optional<Device> findById(String id) {
        return repo.findById(id);
    }

    /**
     * Creates a new device using the provided command data.
     * <p>
     * Behavior notes:
     * <ul>
     * <li>generates a random UUID for the device id</li>
     * <li>marks the device as active by default</li>
     * </ul>
     *
     * @param cmd incoming device data
     * @return the persisted device
     */
    @Override
    public Device createDevice(CreateDeviceCommand cmd) {
        String id = UUID.randomUUID().toString();

        Device device = new Device(
                id,
                cmd.name(),
                cmd.type(),
                cmd.building(),
                cmd.room(),
                true,
                null);

        return repo.save(device);
    }

    /**
     * Removes a device by id.
     * <p>
     * <ul>
     * <li>silently succeeds if the id does not exist</li>
     * </ul>
     *
     * @param id identifier of the device to delete
     */
    @Override
    public void deleteDevice(String id) {
        repo.deleteById(id);
    }

    /**
     * Updates a device with new values, keeping existing fields when a command
     * value is null.
     * <p>
     * Field handling rules:
     * <ul>
     * <li>non-null command values overwrite stored values</li>
     * <li>null command values leave the existing field intact</li>
     * </ul>
     *
     * @param id  identifier of the device to update
     * @param cmd incoming updates
     * @return the updated device
     * @throws IllegalArgumentException if the device does not exist
     */
    @Override
    public Device updateDevice(String id, UpdateDeviceCommand cmd) {
        Device existing = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Device not found: " + id));

        String name = cmd.name() != null ? cmd.name() : existing.name();
        DeviceType type = cmd.type() != null ? cmd.type() : existing.type();
        String building = cmd.building() != null ? cmd.building() : existing.building();
        String room = cmd.room() != null ? cmd.room() : existing.room();
        boolean active = cmd.active() != null ? cmd.active() : existing.active();

        Device updated = new Device(
                existing.id(),
                name,
                type,
                building,
                room,
                active,
                existing.config());

        return repo.save(updated);
    }

    /**
     * Finds devices that match the given filter and pagination settings.
     * <p>
     * Defaults:
     * <ul>
     * <li>sorts by {@code name} when the caller omits a field</li>
     * <li>returns empty content when no devices match the filter</li>
     * </ul>
     *
     * @param filter      filter criteria (building, room, type, active flag); may
     *                    be {@code null}
     * @param pageRequest pagination and sorting parameters
     * @return matching devices wrapped in a paginated result
     */
    @Override
    public PageResult<Device> findDevices(DeviceFilter filter, PageRequest pageRequest) {
        return repo.findByFilter(filter, pageRequest);
    }

}
