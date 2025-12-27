package com.erval.argos.mongo;

import com.erval.argos.application.device.DeviceService;
import com.erval.argos.application.measurement.MeasurementService;
import com.erval.argos.core.application.PageRequest;
import com.erval.argos.core.application.PageResult;
import com.erval.argos.core.application.SortDirection;
import com.erval.argos.core.application.port.in.commands.DeviceCommandUseCase;
import com.erval.argos.core.application.port.in.queries.MeasurementQueryUseCase.MeasurementFilter;
import com.erval.argos.core.application.port.out.DeviceRepositoryPort;
import com.erval.argos.core.application.port.out.MeasurementRepositoryPort;
import com.erval.argos.core.domain.device.Device;
import com.erval.argos.core.domain.device.DeviceType;
import com.erval.argos.core.domain.measurement.Measurement;
import com.erval.argos.core.domain.measurement.MeasurementType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Higher-level business scenario tests combining device and measurement flows.
 */
@SpringBootTest(classes = BusinessScenarioIntegrationTest.Config.class)
@Testcontainers
class BusinessScenarioIntegrationTest {

    @Container
    static final MongoDBContainer mongo = new MongoDBContainer("mongo:6.0.5");

    @DynamicPropertySource
    static void mongoProps(DynamicPropertyRegistry registry) {
        registry.add("spring.mongodb.uri", mongo::getReplicaSetUrl);
    }

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private MeasurementService measurementService;

    @Autowired
    private DeviceRepositoryPort devicePort;

    @Autowired
    private MeasurementRepositoryPort measurementPort;

    @BeforeEach
    void setUp() {
        measurementPort.deleteAll();
        devicePort.deleteAll();
    }

    @AfterEach
    void tearDown() {
        measurementPort.deleteAll();
        devicePort.deleteAll();
    }

    @Test
    void createMeasurementFailsWhenDeviceMissing() {
        var cmd = new MeasurementService.CreateMeasurementCommand("missing", MeasurementType.TEMP, 1.0, null);

        assertThrows(IllegalArgumentException.class, () -> measurementService.createMeasurement(cmd));
    }

    @Test
    void updateDeviceFailsWhenNotFound() {
        var cmd = new DeviceCommandUseCase.UpdateDeviceCommand("name", DeviceType.TEMP, "A", "101", true);

        assertThrows(IllegalArgumentException.class, () -> deviceService.updateDevice("missing", cmd));
    }

    @Test
    void duplicateDeviceNameThrowsDataIntegrityViolation() {
        deviceService.createDevice(new DeviceCommandUseCase.CreateDeviceCommand("dup", DeviceType.TEMP, "A", "101"));

        assertThrows(DataIntegrityViolationException.class, () ->
            deviceService.createDevice(new DeviceCommandUseCase.CreateDeviceCommand("dup", DeviceType.TEMP, "A", "102"))
        );
    }

    @Test
    void deleteByDeviceIdRemovesAllMeasurements() {
        Device d = deviceService.createDevice(new DeviceCommandUseCase.CreateDeviceCommand("dev1", DeviceType.TEMP, "A", "101"));
        measurementPort.save(new Measurement(null, d.id(), MeasurementType.TEMP, 1.0, 1, Instant.now(), List.of()));
        measurementPort.save(new Measurement(null, d.id(), MeasurementType.HUMIDITY, 2.0, 2, Instant.now(), List.of()));

        measurementPort.deleteByDeviceId(d.id());

        assertThat(measurementPort.findAll(new PageRequest(0, 10, "timestamp", SortDirection.ASC)).content()).isEmpty();
    }

    @Test
    void filtersAndSortsMeasurementsByTimestamp() {
        Device d = deviceService.createDevice(new DeviceCommandUseCase.CreateDeviceCommand("dev1", DeviceType.TEMP, "A", "101"));
        Instant later = Instant.now();
        Instant earlier = later.minusSeconds(60);
        measurementPort.save(new Measurement(null, d.id(), MeasurementType.TEMP, 1.0, 1, later, List.of()));
        measurementPort.save(new Measurement(null, d.id(), MeasurementType.TEMP, 2.0, 2, earlier, List.of()));

        MeasurementFilter filter = new MeasurementFilter(d.id(), MeasurementType.TEMP, null, null);
        PageResult<Measurement> page = measurementService.findMeasurements(
            filter,
            new PageRequest(0, 10, "timestamp", SortDirection.ASC)
        );

        assertThat(page.content()).extracting(Measurement::value).containsExactly(2.0, 1.0);
    }

    @TestConfiguration
    @Import({MongoDeviceRepositoryAdapter.class, MongoMeasurementRepositoryAdapter.class})
    static class Config {
        @Bean
        DeviceService deviceService(DeviceRepositoryPort deviceRepo) {
            return new DeviceService(deviceRepo);
        }

        @Bean
        MeasurementService measurementService(MeasurementRepositoryPort measurementRepo, DeviceRepositoryPort deviceRepo) {
            return new MeasurementService(measurementRepo, deviceRepo);
        }
    }
}
