package com.erval.argos.mongo.model;

import com.erval.argos.core.domain.measurement.Measurement;
import com.erval.argos.core.domain.measurement.MeasurementType;
import java.time.Instant;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

/**
 * MongoDB representation of a Measurement aggregate.
 * <p>
 * Stores measurement metadata and values to mirror domain invariants.
 * <p>
 * Indexed fields:
 * <ul>
 *   <li>{@code deviceId}: frequent lookup and projection target</li>
 *   <li>{@code timestamp}: common sort/filter field</li>
 * </ul>
 * Related documents:
 * <ul>
 *   <li>{@code device}: populated via {@code @DocumentReference} lookup</li>
 * </ul>
 */
@Data
@Document("measurements")
@NoArgsConstructor
@AllArgsConstructor
public class MeasurementDocument {

    @Id
    private String id;

    @Indexed
    private String deviceId;
    private MeasurementType type;
    private double value;
    private int sequenceNumber;
    @Indexed
    private Instant timestamp;

    @DocumentReference(lookup = "{ '_id' : ?#{#self.deviceId} }", lazy = true)
    private DeviceDocument device;
    private List<String> tags;

    /**
     * Creates a {@link MeasurementDocument} from a domain measurement.
     * <p>
     * The Mongo driver generates an {@code id}; therefore the id is left
     * {@code null}.
     *
     * @param m domain measurement
     * @return document ready for persistence
     */
    public static MeasurementDocument fromDomain(Measurement m) {
        return new MeasurementDocument(

            m.id(),
            m.deviceId(),
            m.type(),
            m.value(),
            m.sequenceNumber(),
            m.timestamp(),
            null,
            m.tags());
    }

    /**
     * Converts the document back to the immutable domain aggregate.
     *
     * @return a {@link Measurement} constructed from stored fields
     */
    public Measurement toDomain() {
        return new Measurement(
            id,
            deviceId,
            type,
            value,
            sequenceNumber,
            timestamp,
            tags);
    }
}
