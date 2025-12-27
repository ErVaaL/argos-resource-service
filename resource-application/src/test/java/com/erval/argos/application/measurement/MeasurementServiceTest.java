package com.erval.argos.application.measurement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.erval.argos.core.application.PageRequest;
import com.erval.argos.core.application.PageResult;
import com.erval.argos.core.application.SortDirection;
import com.erval.argos.core.application.port.in.commands.MeasurementCommandUseCase.CreateMeasurementCommand;
import com.erval.argos.core.application.port.in.queries.MeasurementQueryUseCase.MeasurementFilter;
import com.erval.argos.core.application.port.out.DeviceRepositoryPort;
import com.erval.argos.core.application.port.out.MeasurementRepositoryPort;
import com.erval.argos.core.domain.device.Device;
import com.erval.argos.core.domain.device.DeviceType;
import com.erval.argos.core.domain.measurement.Measurement;
import com.erval.argos.core.domain.measurement.MeasurementType;

class MeasurementServiceTest {

    private FakeMeasurementRepo measurementRepo;
    private FakeDeviceRepo deviceRepo;
    private MeasurementService service;

    @BeforeEach
    void setUp() {
        measurementRepo = new FakeMeasurementRepo();
        deviceRepo = new FakeDeviceRepo();
        service = new MeasurementService(measurementRepo, deviceRepo);
    }

    @Test
    void createMeasurementDefaultsTimestampWhenNull() {
        deviceRepo.save(new Device("d1", "Sensor", DeviceType.CO2, "A", "101", true, null));

        Measurement created = service.createMeasurement(
                new CreateMeasurementCommand("d1", MeasurementType.CO2, 12.3, null));

        assertNotNull(created.timestamp());
        assertSame(created, measurementRepo.lastSaved);
    }

    @Test
    void createMeasurementUsesProvidedTimestamp() {
        deviceRepo.save(new Device("d1", "Sensor", DeviceType.CO2, "A", "101", true, null));
        Instant fixed = Instant.parse("2024-01-01T00:00:00Z");

        Measurement created = service.createMeasurement(
                new CreateMeasurementCommand("d1", MeasurementType.CO2, 12.3, fixed));

        assertEquals(fixed, created.timestamp());
        assertSame(created, measurementRepo.lastSaved);
    }

    @Test
    void createMeasurementThrowsWhenDeviceMissing() {
        assertThrows(IllegalArgumentException.class, () -> service
                .createMeasurement(new CreateMeasurementCommand("missing", MeasurementType.CO2, 1.0, null)));
    }

    @Test
    void findMeasurementsDelegatesToRepository() {
        PageResult<Measurement> page = new PageResult<>(List.of(), 0, 0, 10);
        measurementRepo.nextPage = page;

        MeasurementFilter filter = new MeasurementFilter("d1", MeasurementType.CO2, null, null);
        PageRequest request = new PageRequest(0, 10, "timestamp", SortDirection.DESC);

        PageResult<Measurement> result = service.findMeasurements(filter, request);

        assertSame(page, result);
        assertEquals(filter, measurementRepo.lastFilter);
        assertEquals(request, measurementRepo.lastRequest);
    }

    @Test
    void deleteMeasurementDelegatesToRepository() {
        service.deleteMeasurement("m1");
        assertEquals("m1", measurementRepo.lastDeletedId);
    }

    private static class FakeMeasurementRepo implements MeasurementRepositoryPort {
        private Measurement lastSaved;
        private PageResult<Measurement> nextPage;
        private MeasurementFilter lastFilter;
        private PageRequest lastRequest;
        private String lastDeletedId;

        @Override
        public Measurement save(Measurement measurement) {
            lastSaved = measurement;
            return measurement;
        }

        @Override
        public PageResult<Measurement> findByFilter(MeasurementFilter filter, PageRequest pageRequest) {
            lastFilter = filter;
            lastRequest = pageRequest;
            return nextPage;
        }

        @Override
        public Optional<Measurement> findById(String id) {
            return Optional.empty();
        }

        @Override
        public PageResult<Measurement> findAll(PageRequest pageRequest) {
            lastRequest = pageRequest;
            return nextPage;
        }

        @Override
        public void deleteById(String id) {
            lastDeletedId = id;
        }

        @Override
        public void deleteByDeviceId(String deviceId) {
            // no-op for test stub
        }

        @Override
        public void deleteAll() {
            // no-op for test stub
        }
    }

    private static class FakeDeviceRepo implements DeviceRepositoryPort {
        private Device saved;

        @Override
        public Device save(Device device) {
            saved = device;
            return device;
        }

        @Override
        public Optional<Device> findById(String id) {
            return Optional.ofNullable(saved).filter(d -> d.id().equals(id));
        }

        @Override
        public PageResult<Device> findAll(PageRequest pageRequest) {
            return new PageResult<>(List.of(), 0, 0, 0);
        }

        @Override
        public void deleteById(String id) {
            saved = null;
        }

        @Override
        public void deleteAll() {
            saved = null;
        }

        @Override
        public PageResult<Device> findByFilter(
                com.erval.argos.core.application.port.in.queries.DeviceQueryUseCase.DeviceFilter filter,
                PageRequest pageRequest) {
            return new PageResult<>(List.of(), 0, 0, 0);
        }
    }
}
