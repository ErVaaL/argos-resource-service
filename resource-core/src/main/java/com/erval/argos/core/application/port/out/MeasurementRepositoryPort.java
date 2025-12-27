package com.erval.argos.core.application.port.out;

import com.erval.argos.core.application.PageRequest;
import com.erval.argos.core.application.PageResult;
import com.erval.argos.core.application.port.in.queries.MeasurementQueryUseCase.MeasurementFilter;
import com.erval.argos.core.domain.measurement.Measurement;

import java.util.Optional;

/**
 * Persistence port for measurements.
 */
public interface MeasurementRepositoryPort {

    /**
     * Persists or updates a measurement aggregate.
     *
     * @param measurement measurement to save
     * @return saved measurement
     */
    Measurement save(Measurement measurement);

    /**
     * Finds measurements using filter and paging.
     *
     * @param filter      constraints such as device, type, or time window
     * @param pageRequest paging and sorting information
     * @return paged measurements
     */
    PageResult<Measurement> findByFilter(MeasurementFilter filter, PageRequest pageRequest);

    /**
     * Retrieves a measurement by id.
     *
     * @param id measurement identifier
     * @return optional measurement if found
     */
    Optional<Measurement> findById(String id);

    /**
     * Returns a page of all measurements.
     *
     * @param pageRequest paging and sorting information
     * @return paged measurements
     */
    PageResult<Measurement> findAll(PageRequest pageRequest);

    /**
     * Deletes a measurement by its identifier.
     *
     * @param id measurement id to delete
     */
    void deleteById(String id);

    /**
     * Deletes all measurements for a device.
     *
     * @param deviceId device identifier whose measurements should be removed
     */
    void deleteByDeviceId(String deviceId);

    void deleteAll();
}
