package com.erval.argos.mongo.model;

import com.erval.argos.core.domain.device.Device;
import com.erval.argos.core.domain.device.DeviceConfig;
import com.erval.argos.core.domain.device.DeviceType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * MongoDB representation of a Device aggregate.
 * <p>
 * The document mirrors the domain object fields so that mapping is a direct
 * copy.
 * <p>
 * Indexed fields:
 * <ul>
 *   <li>{@code name}: unique business key</li>
 *   <li>{@code building}: common filter field</li>
 * </ul>
 */
@Document("devices")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class DeviceDocument {

    @Id
    private String id;

    @Indexed(unique = true)
    private String name;
    private DeviceType type;
    @Indexed
    private String building;
    private String room;
    private boolean active;
    private DeviceConfigDocument config;

    /**
     * Creates a {@link DeviceDocument} from a domain device.
     * <p>
     * Field mapping rules:
     * <ul>
     * <li>ids are kept as provided</li>
     * <li>boolean flags are copied without transformation</li>
     * <li>enum {@code DeviceType} is stored verbatim</li>
     * </ul>
     *
     * @param device domain aggregate
     * @return mongo document ready for persistence
     */
    public static DeviceDocument fromDomain(Device device) {
        DeviceConfig cfg = device.config();
        return new DeviceDocument(
                device.id(),
                device.name(),
                device.type(),
                device.building(),
                device.room(),
                device.active(),
                cfg != null ? DeviceConfigDocument.fromDomain(cfg) : null);
    }

    /**
     * Maps the stored document back to the domain model.
     *
     * @return a new {@link Device} populated from document fields
     */
    public Device toDomain() {
        DeviceConfig cfg = this.config != null ? this.config.toDomain() : null;
        return new Device(
                this.id,
                this.name,
                this.type,
                this.building,
                this.room,
                this.active,
                cfg);
    }
}
