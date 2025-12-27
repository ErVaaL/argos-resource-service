package com.erval.argos.resource.controllers;

import java.util.Set;

import com.erval.argos.core.application.port.in.queries.DeviceQueryUseCase;
import com.erval.argos.core.domain.device.Device;
import com.erval.argos.resource.dto.*;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import com.erval.argos.core.application.PageRequest;
import com.erval.argos.core.application.PageResult;
import com.erval.argos.core.application.port.in.commands.DeviceCommandUseCase;
import com.erval.argos.core.application.port.in.commands.MeasurementCommandUseCase;
import com.erval.argos.core.application.port.in.queries.MeasurementQueryUseCase;
import com.erval.argos.core.domain.measurement.Measurement;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;

@Controller
@Validated
@RequiredArgsConstructor
public class DeviceGraphqlController {

    private static final String DEFAULT_DEVICE_SORT = "name";
    private static final String DEFAULT_MEASUREMENT_SORT = "timestamp";

    private final DeviceCommandUseCase deviceCommandUseCase;
    private final DeviceQueryUseCase deviceQueryUseCase;
    private final MeasurementCommandUseCase measurementCommandUseCase;
    private final MeasurementQueryUseCase measurementQueryUseCase;

    @QueryMapping
    public PageResult<Device> devices(
            @Argument("filter") @Valid DeviceFilterInput filter,
            @Argument("page") @Valid PageRequestInput pageInput) {
        PageRequest pageRequest = GraphqlMapper.toPageRequest(pageInput, DEFAULT_DEVICE_SORT);
        return deviceQueryUseCase.findDevices(GraphqlMapper.toDeviceFilter(filter), pageRequest);
    }

    @QueryMapping
    public PageResult<Measurement> measurements(
            @Argument("filter") @Valid MeasurementFilterInput filter,
            @Argument("page") @Valid PageRequestInput pageInput) {
        PageRequest pageRequest = normalizeMeasurementSort(GraphqlMapper.toPageRequest(pageInput, DEFAULT_MEASUREMENT_SORT));
        return measurementQueryUseCase.findMeasurements(GraphqlMapper.toMeasurementFilter(filter), pageRequest);
    }

    @MutationMapping
//    @PreAuthorize("isAuthenticated()")
    public Device createDevice(@Argument("input") @Valid CreateDeviceInput input) {
        return deviceCommandUseCase.createDevice(GraphqlMapper.toCreateDeviceCommand(input));
    }

    @MutationMapping
//    @PreAuthorize("isAuthenticated()")
    public Device updateDevice(@Argument("id") String id, @Argument("input") @Valid UpdateDeviceInput input) {
        return deviceCommandUseCase.updateDevice(id, GraphqlMapper.toUpdateDeviceCommand(input));
    }

    @MutationMapping
//    @PreAuthorize("isAuthenticated()")
    public Boolean deleteDevice(@Argument("id") String id) {
        deviceCommandUseCase.deleteDevice(id);
        return true;
    }

    @MutationMapping
//    @PreAuthorize("isAuthenticated()")
    public Measurement createMeasurement(@Argument("input") @Valid CreateMeasurementInput input) {
        return measurementCommandUseCase.createMeasurement(GraphqlMapper.toCreateMeasurementCommand(input));
    }

    @MutationMapping
//    @PreAuthorize("isAuthenticated()")
    public Boolean deleteMeasurement(@Argument("id") @Valid String id) {
        measurementCommandUseCase.deleteMeasurement(id);
        return true;
    }

    private PageRequest normalizeMeasurementSort(PageRequest pageRequest) {
        Set<String> allowed = Set.of("timestamp", "value", "type", "deviceId");
        if (pageRequest.sortBy() != null && allowed.contains(pageRequest.sortBy())) {
            return pageRequest;
        }
        return new PageRequest(pageRequest.page(), pageRequest.size(), DEFAULT_MEASUREMENT_SORT, pageRequest.direction());
    }
}
