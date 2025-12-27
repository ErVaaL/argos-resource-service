package com.erval.argos.application.device;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.erval.argos.core.application.PageRequest;
import com.erval.argos.core.application.PageResult;
import com.erval.argos.core.application.SortDirection;
import com.erval.argos.core.application.port.in.commands.DeviceCommandUseCase.CreateDeviceCommand;
import com.erval.argos.core.application.port.in.commands.DeviceCommandUseCase.UpdateDeviceCommand;
import com.erval.argos.core.application.port.in.queries.DeviceQueryUseCase.DeviceFilter;
import com.erval.argos.core.application.port.out.DeviceRepositoryPort;
import com.erval.argos.core.domain.device.Device;
import com.erval.argos.core.domain.device.DeviceType;

import static org.junit.jupiter.api.Assertions.*;

class DeviceServiceTest {

    private InMemoryDeviceRepo repo;
    private DeviceService service;

    @BeforeEach
    void setUp() {
        repo = new InMemoryDeviceRepo();
        service = new DeviceService(repo);
    }

    @Test
    void createDeviceGeneratesIdAndPersistsActiveDevice() {
        Device created = service.createDevice(new CreateDeviceCommand("Sensor", DeviceType.CO2, "A", "101"));

        assertNotNull(created.id());
        assertEquals("Sensor", created.name());
        assertTrue(created.active());
        assertSame(created, repo.savedDevices.getFirst());
    }

    @Test
    void updateDeviceMergesFieldsAndPersists() {
        Device existing = new Device("id-1", "Old", DeviceType.TEMP, "A", "100", true, null);
        repo.save(existing);

        Device updated = service.updateDevice("id-1", new UpdateDeviceCommand("New", null, null, "200", false));

        assertEquals("id-1", updated.id());
        assertEquals("New", updated.name());
        assertEquals(DeviceType.TEMP, updated.type());
        assertEquals("A", updated.building());
        assertEquals("200", updated.room());
        assertFalse(updated.active());
        assertSame(updated, repo.savedDevices.get(1));
    }

    @Test
    void updateDeviceThrowsWhenMissing() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.updateDevice("missing", new UpdateDeviceCommand(null, null, null, null, null)));

        assertTrue(ex.getMessage().contains("missing"));
    }

    @Test
    void findDevicesDelegatesToRepository() {
        PageResult<Device> page = new PageResult<>(List.of(), 0, 0, 10);
        repo.nextPage.set(page);

        PageRequest request = new PageRequest(0, 10, "name", SortDirection.ASC);
        PageResult<Device> result = service.findDevices(new DeviceFilter("A", null, null, true), request);

        assertSame(page, result);
        assertEquals(request, repo.lastPageRequest);
    }

    private static class InMemoryDeviceRepo implements DeviceRepositoryPort {
        private final List<Device> savedDevices = new ArrayList<>();
        private final AtomicReference<PageResult<Device>> nextPage = new AtomicReference<>();
        private PageRequest lastPageRequest;

        @Override
        public Device save(Device device) {
            savedDevices.add(device);
            return device;
        }

        @Override
        public Optional<Device> findById(String id) {
            return savedDevices.stream().filter(d -> d.id().equals(id)).findFirst();
        }

        @Override
        public PageResult<Device> findAll(PageRequest pageRequest) {
            lastPageRequest = pageRequest;
            return nextPage.get();
        }

        @Override
        public void deleteById(String id) {
            savedDevices.removeIf(d -> d.id().equals(id));
        }

        @Override
        public void deleteAll() {
            savedDevices.clear();
        }

        @Override
        public PageResult<Device> findByFilter(DeviceFilter filter, PageRequest pageRequest) {
            lastPageRequest = pageRequest;
            return nextPage.get();
        }
    }
}
