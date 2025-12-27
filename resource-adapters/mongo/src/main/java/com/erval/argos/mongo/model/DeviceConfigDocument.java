package com.erval.argos.mongo.model;

import java.util.List;

import com.erval.argos.core.domain.device.DeviceConfig;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Embedded document holding device configuration thresholds and tags.
 * <p>
 * Mirrors {@link com.erval.argos.core.domain.device.DeviceConfig}.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeviceConfigDocument {
    private Double minValue;
    private Double maxValue;
    private Boolean alertOnThreshold;
    private List<String> tags;

    public static DeviceConfigDocument fromDomain(DeviceConfig cfg) {
        return new DeviceConfigDocument(
                cfg.minValue(),
                cfg.maxValue(),
                cfg.alertOnThreshold(),
                cfg.tags());
    }

    public DeviceConfig toDomain() {
        return new DeviceConfig(minValue, maxValue, alertOnThreshold, tags);
    }
}
