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

    /**
     * Converts a domain DeviceConfig to a DeviceConfigDocument.
     *
     * @param cfg the domain DeviceConfig
     * @return the corresponding DeviceConfigDocument
     */
    public static DeviceConfigDocument fromDomain(DeviceConfig cfg) {
        return new DeviceConfigDocument(
                cfg.minValue(),
                cfg.maxValue(),
                cfg.alertOnThreshold(),
                cfg.tags());
    }

    /**
     * Converts this DeviceConfigDocument to a domain DeviceConfig.
     *
     * @return the corresponding domain DeviceConfig
     */
    public DeviceConfig toDomain() {
        return new DeviceConfig(minValue, maxValue, alertOnThreshold, tags);
    }
}
