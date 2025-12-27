package com.erval.argos.core.application.port.in.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Instant;

import org.junit.jupiter.api.Test;

import com.erval.argos.core.domain.measurement.MeasurementType;

class MeasurementCommandRecordTest {

    @Test
    void createMeasurementCommandStoresFields() {
        Instant ts = Instant.parse("2024-01-01T00:00:00Z");
        MeasurementCommandUseCase.CreateMeasurementCommand cmd = new MeasurementCommandUseCase.CreateMeasurementCommand(
                "device-1", MeasurementType.CO2, 12.3, ts);

        assertEquals("device-1", cmd.deviceId());
        assertEquals(MeasurementType.CO2, cmd.type());
        assertEquals(12.3, cmd.value());
        assertEquals(ts, cmd.timestamp());
    }
}
