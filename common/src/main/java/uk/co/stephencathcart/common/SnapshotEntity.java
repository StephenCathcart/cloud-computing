package uk.co.stephencathcart.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.azure.storage.table.TableServiceEntity;

/**
 * A Azure table entity which represents a snapshot. The snapshot is serialised
 * to a json string to be stored in a row. The partition key is the vehicle
 * registration and the row key is the captured time of when the snapshot was
 * taken which will also be unique.
 *
 * @author Stephen Cathcart
 */
public class SnapshotEntity extends TableServiceEntity {

    private String snapshot;

    public SnapshotEntity() {
    }

    public SnapshotEntity(Snapshot snapshot) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        this.snapshot = mapper.writeValueAsString(snapshot);
        this.partitionKey = snapshot.getRegistration();
        this.rowKey = DateUtils.format(snapshot.getCaptureDate());
    }

    public String getSnapshot() {
        return snapshot;
    }

    public void setSnapshot(String snapshot) {
        this.snapshot = snapshot;
    }
}
