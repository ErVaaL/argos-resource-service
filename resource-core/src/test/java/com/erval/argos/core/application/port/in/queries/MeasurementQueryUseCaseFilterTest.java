package com.erval.argos.core.application.port.in.queries;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.Instant;

import org.junit.jupiter.api.Test;

import com.erval.argos.core.domain.measurement.MeasurementType;

class MeasurementQueryUseCaseFilterTest {

    @Test
    void storesFilterFields() {
        Instant from = Instant.parse("2024-01-01T00:00:00Z");
        Instant to = Instant.parse("2024-01-02T00:00:00Z");

        MeasurementQueryUseCase.MeasurementFilter filter = new MeasurementQueryUseCase.MeasurementFilter(
                "device-1", MeasurementType.CO2, from, to);

        assertEquals("device-1", filter.deviceId());
        assertEquals(MeasurementType.CO2, filter.type());
        assertEquals(from, filter.from());
        assertEquals(to, filter.to());
    }

    @Test
    void allowsNulls() {
        MeasurementQueryUseCase.MeasurementFilter filter = new MeasurementQueryUseCase.MeasurementFilter(
                null, null, null, null);

        assertNull(filter.deviceId());
        assertNull(filter.type());
        assertNull(filter.from());
        assertNull(filter.to());
    }
}
