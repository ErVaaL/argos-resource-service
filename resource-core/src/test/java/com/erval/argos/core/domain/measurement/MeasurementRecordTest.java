package com.erval.argos.core.domain.measurement;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Test;

class MeasurementRecordTest {

    @Test
    void storesMeasurementValues() {
        Instant now = Instant.parse("2024-01-01T00:00:00Z");
        Measurement measurement = new Measurement("m1", "d1", MeasurementType.TEMP, 21.5, 7, now, List.of("lab"));

        assertEquals("m1", measurement.id());
        assertEquals("d1", measurement.deviceId());
        assertEquals(MeasurementType.TEMP, measurement.type());
        assertEquals(21.5, measurement.value());
        assertEquals(7, measurement.sequenceNumber());
        assertEquals(now, measurement.timestamp());
        assertEquals(List.of("lab"), measurement.tags());
    }
}
