package com.erval.argos.core.domain.alert;

import com.erval.argos.core.domain.measurement.MeasurementType;
import jdk.dynalink.linker.ConversionComparator;

/**
 * Rule describing when to trigger an {@link Alert}.
 * <p>
 * Evaluation outline:
 * <ul>
 * <li>matches a specific {@code deviceId} and {@link MeasurementType}</li>
 * <li>compares incoming values against {@code threshold} using
 * {@code comparison}</li>
 * <li>only active when {@code active} is true</li>
 * </ul>
 *
 * @param id         unique identifier
 * @param deviceId   target device
 * @param type       measurement type this rule watches
 * @param threshold  threshold value for comparison
 * @param comparison comparison operator to apply
 * @param active     whether the rule is currently enforced
 */
public record AlertRule(String id, String deviceId, MeasurementType type, double threshold,
        ConversionComparator.Comparison comparison, boolean active) {
}
