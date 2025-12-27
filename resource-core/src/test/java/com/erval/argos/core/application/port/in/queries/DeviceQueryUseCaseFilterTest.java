package com.erval.argos.core.application.port.in.queries;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import com.erval.argos.core.domain.device.DeviceType;

class DeviceQueryUseCaseFilterTest {

    @Test
    void storesFilterFields() {
        DeviceQueryUseCase.DeviceFilter filter = new DeviceQueryUseCase.DeviceFilter("A", "101", DeviceType.CO2, true);

        assertEquals("A", filter.building());
        assertEquals("101", filter.room());
        assertEquals(DeviceType.CO2, filter.type());
        assertEquals(true, filter.active());
    }

    @Test
    void allowsNulls() {
        DeviceQueryUseCase.DeviceFilter filter = new DeviceQueryUseCase.DeviceFilter(null, null, null, null);

        assertNull(filter.building());
        assertNull(filter.room());
        assertNull(filter.type());
        assertNull(filter.active());
    }
}
