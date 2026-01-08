package com.erval.argos.mongo;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.erval.argos.core.application.PageRequest;
import com.erval.argos.core.application.PageResult;
import com.erval.argos.core.application.SortDirection;
import com.erval.argos.core.application.port.in.queries.DeviceQueryUseCase.DeviceFilter;
import com.erval.argos.core.application.port.in.queries.MeasurementQueryUseCase.MeasurementFilter;
import com.erval.argos.core.domain.device.Device;
import com.erval.argos.core.domain.device.DeviceType;
import com.erval.argos.core.domain.measurement.Measurement;
import com.erval.argos.core.domain.measurement.MeasurementType;
import com.erval.argos.mongo.model.DeviceDocument;
import com.erval.argos.mongo.model.MeasurementDocument;
import com.erval.argos.mongo.repositories.DeviceMongoRepository;
import com.erval.argos.mongo.repositories.MeasurementMongoRepository;

/**
 * Integration tests for Mongo adapters covering CRUD on device and measurement collections.
 */
@SpringBootTest(classes = MongoRepositoryAdapterIntegrationTest.TestApp.class)
@Testcontainers
class MongoRepositoryAdapterIntegrationTest {

    @Container
    static final MongoDBContainer mongo = new MongoDBContainer("mongo:6.0.5");

    @DynamicPropertySource
    static void mongoProps(DynamicPropertyRegistry registry) {
        registry.add("spring.mongodb.uri", mongo::getReplicaSetUrl);
    }

    @Autowired
    private MongoDeviceRepositoryAdapter deviceAdapter;

    @Autowired
    private MongoMeasurementRepositoryAdapter measurementAdapter;

    @Autowired
    private DeviceMongoRepository deviceRepo;

    @Autowired
    private MeasurementMongoRepository measurementRepo;

    @BeforeEach
    void initData() {
        measurementRepo.deleteAll();
        deviceRepo.deleteAll();
    }

    @AfterEach
    void cleanup() {
        measurementRepo.deleteAll();
        deviceRepo.deleteAll();
    }

    @Test
    void savesAndFindsDeviceById() {
        Device saved = deviceAdapter.save(new Device("d1", "Sensor A", DeviceType.TEMP, "A", "101", true, false, null));

        assertThat(deviceAdapter.findById(saved.id()))
            .isPresent()
            .get()
            .extracting(Device::name)
            .isEqualTo("Sensor A");
    }

    @Test
    void filtersDevicesByBuildingAndActive() {
        deviceRepo.saveAll(List.of(
            deviceDoc("d1", "A", true),
            deviceDoc("d2", "B", true),
            deviceDoc("d3", "A", false)
        ));

        PageResult<Device> page = deviceAdapter.findByFilter(
            new DeviceFilter("A", null, null, true),
            new PageRequest(0, 10, "name", SortDirection.ASC)
        );

        assertThat(page.content()).hasSize(1);
        assertThat(page.content().getFirst().id()).isEqualTo("d1");
    }

    @Test
    void paginatesDevicesWithDefaultSort() {
        deviceRepo.saveAll(List.of(
            deviceDoc("d1", "A", true, "B-name"),
            deviceDoc("d2", "A", true, "A-name")
        ));

        PageResult<Device> page = deviceAdapter.findAll(new PageRequest(0, 1, null, SortDirection.ASC));

        assertThat(page.content()).hasSize(1);
        assertThat(page.totalElements()).isEqualTo(2);
        assertThat(page.content().getFirst().name()).isEqualTo("A-name");
    }

    @Test
    void updatesDeviceFields() {
        deviceAdapter.save(new Device("d1", "Sensor A", DeviceType.TEMP, "A", "101", true, false, null));

        Device updated = deviceAdapter.save(new Device("d1", "Sensor A2", DeviceType.CO2, "A", "101", false, false, null));

        assertThat(deviceAdapter.findById(updated.id()))
            .isPresent()
            .get()
            .extracting(Device::active)
            .isEqualTo(false);
    }

    @Test
    void deletesDeviceById() {
        deviceAdapter.save(new Device("d1", "Sensor A", DeviceType.TEMP, "A", "101", true, false, null));

        deviceAdapter.deleteById("d1");

        assertThat(deviceAdapter.findById("d1")).isEmpty();
    }

    @Test
    void savesAndFindsMeasurementById() {
        Measurement saved = measurementAdapter.save(measurement(Instant.now()));

        assertThat(measurementAdapter.findById(saved.id())).isPresent();
    }

    @Test
    void filtersMeasurementsByDeviceAndType() {
        Instant now = Instant.now();
        measurementRepo.saveAll(List.of(
            measurementDoc("m1", "d1", MeasurementType.TEMP, now),
            measurementDoc("m2", "d1", MeasurementType.HUMIDITY, now),
            measurementDoc("m3", "d2", MeasurementType.TEMP, now)
        ));

        MeasurementFilter filter = new MeasurementFilter("d1", MeasurementType.TEMP, null, null);
        PageResult<Measurement> page = measurementAdapter.findByFilter(filter, new PageRequest(0, 10, "timestamp", SortDirection.ASC));

        assertThat(page.content()).hasSize(1);
        assertThat(page.content().getFirst().id()).isEqualTo("m1");
    }

    @Test
    void filtersMeasurementsByTimeWindow() {
        Instant now = Instant.now();
        Instant earlier = now.minusSeconds(3600);
        measurementRepo.saveAll(List.of(
            measurementDoc("m1", "d1", MeasurementType.TEMP, earlier),
            measurementDoc("m2", "d1", MeasurementType.TEMP, now)
        ));

        MeasurementFilter filter = new MeasurementFilter("d1", MeasurementType.TEMP, now.minusSeconds(10), now.plusSeconds(10));
        PageResult<Measurement> page = measurementAdapter.findByFilter(filter, new PageRequest(0, 10, "timestamp", SortDirection.ASC));

        assertThat(page.content()).extracting(Measurement::id).containsExactly("m2");
    }

    @Test
    void deletesMeasurementsByDeviceId() {
        measurementRepo.saveAll(List.of(
            measurementDoc("m1", "d1", MeasurementType.TEMP, Instant.now()),
            measurementDoc("m2", "d1", MeasurementType.HUMIDITY, Instant.now())
        ));

        measurementAdapter.deleteByDeviceId("d1");

        assertThat(measurementAdapter.findAll(new PageRequest(0, 10, "timestamp", SortDirection.ASC)).content()).isEmpty();
    }

    @Test
    void paginatesMeasurementsWithDefaultSort() {
        measurementRepo.saveAll(List.of(
            measurementDoc("m1", "d1", MeasurementType.TEMP, Instant.now()),
            measurementDoc("m2", "d1", MeasurementType.TEMP, Instant.now().minusSeconds(60))
        ));

        PageResult<Measurement> page = measurementAdapter.findAll(new PageRequest(0, 1, null, SortDirection.ASC));

        assertThat(page.content()).hasSize(1);
        assertThat(page.totalElements()).isEqualTo(2);
        assertThat(page.content().getFirst().id()).isEqualTo("m2");
    }

    @Test
    void deletesMeasurementById() {
        measurementAdapter.save(measurement(Instant.now()));

        measurementAdapter.deleteById("m1");

        assertThat(measurementAdapter.findById("m1")).isEmpty();
    }

    private DeviceDocument deviceDoc(String id, String building, boolean active) {
        return deviceDoc(id, building, active, "Device-" + id);
    }

    private DeviceDocument deviceDoc(String id, String building, boolean active, String name) {
        return new DeviceDocument(
            id,
            name,
            DeviceType.TEMP,
            building,
            "101",
            active,
            false,
            null
        );
    }

    private MeasurementDocument measurementDoc(String id, String deviceId, MeasurementType type, Instant ts) {
        return new MeasurementDocument(
            id,
            deviceId,
            type,
            1.0,
            1,
            ts,
            null,
            List.of()
        );
    }

    private Measurement measurement(Instant ts) {
        return new Measurement("m1", "d1", MeasurementType.TEMP, 1.0, 1, ts, List.of());
    }

    @SpringBootApplication(scanBasePackages = "com.erval.argos.mongo")
    static class TestApp {
    }
}
