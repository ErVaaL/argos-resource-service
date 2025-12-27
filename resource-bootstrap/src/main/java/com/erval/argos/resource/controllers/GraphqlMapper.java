package com.erval.argos.resource.controllers;

import java.time.Instant;
import java.time.format.DateTimeParseException;

import com.erval.argos.core.application.PageRequest;
import com.erval.argos.core.application.SortDirection;
import com.erval.argos.core.application.port.in.commands.DeviceCommandUseCase.CreateDeviceCommand;
import com.erval.argos.core.application.port.in.commands.DeviceCommandUseCase.UpdateDeviceCommand;
import com.erval.argos.core.application.port.in.commands.MeasurementCommandUseCase.CreateMeasurementCommand;
import com.erval.argos.core.application.port.in.queries.DeviceQueryUseCase.DeviceFilter;
import com.erval.argos.core.application.port.in.queries.MeasurementQueryUseCase.MeasurementFilter;
import com.erval.argos.resource.dto.*;

final class GraphqlMapper {

    private GraphqlMapper() {
    }

    static PageRequest toPageRequest(PageRequestInput input, String defaultSortBy) {
        int page = input != null && input.page() != null ? input.page() : 0;
        int size = input != null && input.size() != null ? input.size() : 20;
        String sortBy = input != null && input.sortBy() != null && !input.sortBy().isBlank()
                ? input.sortBy()
                : defaultSortBy;
        SortDirection direction = input != null && input.sortDirection() != null
                ? input.sortDirection()
                : SortDirection.ASC;
        return new PageRequest(page, size, sortBy, direction);
    }

    static DeviceFilter toDeviceFilter(DeviceFilterInput input) {
        if (input == null) {
            return null;
        }
        return new DeviceFilter(input.building(), input.room(), input.type(), input.active());
    }

    static MeasurementFilter toMeasurementFilter(MeasurementFilterInput input) {
        if (input == null) {
            return null;
        }
        Instant from = parseInstant(input.from());
        Instant to = parseInstant(input.to());
        return new MeasurementFilter(input.deviceId(), input.type(), from, to);
    }

    static CreateDeviceCommand toCreateDeviceCommand(CreateDeviceInput input) {
        return new CreateDeviceCommand(input.name(), input.type(), input.building(), input.room());
    }

    static UpdateDeviceCommand toUpdateDeviceCommand(UpdateDeviceInput input) {
        return new UpdateDeviceCommand(
                input.name(),
                input.type(),
                input.building(),
                input.room(),
                input.active());
    }

    static CreateMeasurementCommand toCreateMeasurementCommand(CreateMeasurementInput input) {
        Instant timestamp = parseInstant(input.timestamp());
        return new CreateMeasurementCommand(
                input.deviceId(),
                input.type(),
                input.value(),
                timestamp);
    }

    private static Instant parseInstant(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return Instant.parse(value);
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException("Invalid timestamp format, expected ISO-8601: " + value, ex);
        }
    }
}
