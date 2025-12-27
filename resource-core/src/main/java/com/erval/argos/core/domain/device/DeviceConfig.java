package com.erval.argos.core.domain.device;

import java.util.List;

/**
 * Configuration for device thresholds and tagging metadata.
 *
 * @param minValue lower bound for readings
 * @param maxValue upper bound for readings
 * @param alertOnThreshold whether to raise alerts when bounds are crossed
 * @param tags arbitrary labels for grouping or search
 */
public record DeviceConfig(Double minValue, Double maxValue, Boolean alertOnThreshold, List<String> tags) {
}
