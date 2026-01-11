package com.erval.argos.core.application.port.out;

import java.util.Optional;

import com.erval.argos.core.application.PageRequest;
import com.erval.argos.core.application.PageResult;
import com.erval.argos.core.application.port.in.queries.DeviceQueryUseCase;
import com.erval.argos.core.domain.device.Device;

/**
 * Persistence port for devices.
 */
public interface DeviceRepositoryPort {

    /**
     * Persists or updates a device aggregate.
     *
     * @param device device to save
     * @return saved device with generated fields (if any)
     */
    Device save(Device device);

    /**
     * Retrieves a device by id.
     *
     * @param id device identifier
     * @return optional containing the device when found
     */
    Optional<Device> findById(String id);

    /**
     * Returns a page of devices without filtering.
     *
     * @param pageRequest paging and sorting information
     * @return paged result
     */
    PageResult<Device> findAll(PageRequest pageRequest);

    /**
     * Deletes a device by id, ignoring missing entries.
     *
     * @param id identifier of the device to delete
     */
    void deleteById(String id);

    /**
     * Deletes all devices.
     */
    void deleteAll();

    /**
     * Finds devices matching the provided filter with paging.
     *
     * @param filter      filter constraints such as building or active status
     * @param pageRequest paging and sorting information
     * @return paged devices that satisfy the filter
     */
    PageResult<Device> findByFilter(DeviceQueryUseCase.DeviceFilter filter, PageRequest pageRequest);
}
