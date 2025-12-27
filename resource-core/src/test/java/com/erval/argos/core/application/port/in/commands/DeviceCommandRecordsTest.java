package com.erval.argos.core.application.port.in.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import com.erval.argos.core.domain.device.DeviceType;

class DeviceCommandRecordsTest {

    @Test
    void createDeviceCommandStoresFields() {
        DeviceCommandUseCase.CreateDeviceCommand cmd = new DeviceCommandUseCase.CreateDeviceCommand(
                "Sensor", DeviceType.CO2, "A", "101");

        assertEquals("Sensor", cmd.name());
        assertEquals(DeviceType.CO2, cmd.type());
        assertEquals("A", cmd.building());
        assertEquals("101", cmd.room());
    }

    @Test
    void updateDeviceCommandAllowsNulls() {
        DeviceCommandUseCase.UpdateDeviceCommand cmd = new DeviceCommandUseCase.UpdateDeviceCommand(
                null, DeviceType.TEMP, null, "202", null);

        assertNull(cmd.name());
        assertEquals(DeviceType.TEMP, cmd.type());
        assertNull(cmd.building());
        assertEquals("202", cmd.room());
        assertNull(cmd.active());
    }
}
