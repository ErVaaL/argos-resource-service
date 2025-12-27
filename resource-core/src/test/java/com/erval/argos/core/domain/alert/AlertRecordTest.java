package com.erval.argos.core.domain.alert;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Instant;

import org.junit.jupiter.api.Test;

class AlertRecordTest {

    @Test
    void storesAlertFields() {
        Instant ts = Instant.parse("2024-01-02T03:04:05Z");
        Alert alert = new Alert("a1", "rule1", "device1", "threshold exceeded", ts);

        assertEquals("a1", alert.id());
        assertEquals("rule1", alert.ruleId());
        assertEquals("device1", alert.deviceId());
        assertEquals("threshold exceeded", alert.description());
        assertEquals(ts, alert.timestamp());
    }
}
