package com.erval.argos.core.application.port.in.queries;

import com.erval.argos.core.application.PageRequest;
import com.erval.argos.core.application.PageResult;
import com.erval.argos.core.domain.device.Device;
import com.erval.argos.core.domain.device.DeviceType;

import java.util.Optional;

/**
 * Use case for reading devices, used by REST / GraphQL layers.
 * <p>
 * Supports:
 * <ul>
 * <li>filtering by building, room, type, and active flag</li>
 * <li>pagination and sorting through {@link PageRequest}</li>
 * </ul>
 */
public interface DeviceQueryUseCase {

    /**
     * Finds devices matching optional criteria.
     *
     * @param filter      filter constraints; may be {@code null}
     * @param pageRequest pagination and sorting parameters
     * @return paginated result of devices
     */
    PageResult<Device> findDevices(DeviceFilter filter, PageRequest pageRequest);

    /**
     * Finds a device by its identifier.
     *
     * @param id the device identifier
     * @return an optional device; empty if not found
     */
    Optional<Device> findById(String id);

    /**
     * Filter criteria for querying devices.
     * All fields are optional. Null means "no filter" for that field.
     * <p>
     * Examples:
     * <ul>
     * <li>Set {@code building} only to search a campus wing</li>
     * <li>Combine {@code type} and {@code active} to find live devices of a
     * kind</li>
     * </ul>
     *
     * @param building the building to filter by
     * @param room     the room to filter by
     * @param type     the device type to filter by
     * @param active   the active status to filter by
     */
    record DeviceFilter(
            String building,
            String room,
            DeviceType type,
            Boolean active) {
    }
}
