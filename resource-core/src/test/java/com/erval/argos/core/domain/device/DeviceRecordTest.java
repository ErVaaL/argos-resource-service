package com.erval.argos.core.domain.device;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

class DeviceRecordTest {

    @Test
    void storesFieldsAndConfig() {
        DeviceConfig config = new DeviceConfig(1.0, 10.0, true, List.of("lab", "co2"));
        Device device = new Device("id-1", "Sensor", DeviceType.CO2, "A", "101", true, config);

        assertEquals("id-1", device.id());
        assertEquals("Sensor", device.name());
        assertEquals(DeviceType.CO2, device.type());
        assertEquals("A", device.building());
        assertEquals("101", device.room());
        assertTrue(device.active());
        assertEquals(config, device.config());
    }
}
