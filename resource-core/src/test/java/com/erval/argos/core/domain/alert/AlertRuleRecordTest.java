package com.erval.argos.core.domain.alert;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.erval.argos.core.domain.measurement.MeasurementType;

import jdk.dynalink.linker.ConversionComparator;

class AlertRuleRecordTest {

    @Test
    void storesRuleFields() {
        ConversionComparator.Comparison comparison = ConversionComparator.Comparison.values()[0];
        AlertRule rule = new AlertRule("r1", "device1", MeasurementType.CO2, 100.5, comparison, true);

        assertEquals("r1", rule.id());
        assertEquals("device1", rule.deviceId());
        assertEquals(MeasurementType.CO2, rule.type());
        assertEquals(100.5, rule.threshold());
        assertEquals(comparison, rule.comparison());
        assertTrue(rule.active());
    }
}
