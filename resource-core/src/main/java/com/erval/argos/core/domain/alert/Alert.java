package com.erval.argos.core.domain.alert;

import java.time.Instant;

/**
 * Alert raised when a rule triggers for a device.
 * <p>
 * Components:
 * <ul>
 * <li>{@code ruleId}: source rule that was matched</li>
 * <li>{@code deviceId}: device whose data triggered the alert</li>
 * <li>{@code description}: human-readable explanation</li>
 * </ul>
 *
 * @param id          unique identifier
 * @param ruleId      originating rule id
 * @param deviceId    device involved in the alert
 * @param description human-readable detail
 * @param timestamp   when the alert was produced
 */
public record Alert(String id, String ruleId, String deviceId, String description, Instant timestamp) {
}
