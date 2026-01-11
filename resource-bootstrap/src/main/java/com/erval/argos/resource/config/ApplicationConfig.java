package com.erval.argos.resource.config;

import com.erval.argos.application.device.DeviceService;
import com.erval.argos.application.measurement.MeasurementService;
import com.erval.argos.core.application.port.out.DeviceRepositoryPort;
import com.erval.argos.core.application.port.out.MeasurementRepositoryPort;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Wires application-layer services with their required ports.
 */
@Configuration
public class ApplicationConfig {

    @Bean
    public DeviceService deviceService(DeviceRepositoryPort deviceRepositoryPort) {
        return new DeviceService(deviceRepositoryPort);
    }

    @Bean
    public MeasurementService measurementService(MeasurementRepositoryPort measurementRepositoryPort,
            DeviceRepositoryPort deviceRepositoryPort) {
        return new MeasurementService(measurementRepositoryPort, deviceRepositoryPort);
    }
}
