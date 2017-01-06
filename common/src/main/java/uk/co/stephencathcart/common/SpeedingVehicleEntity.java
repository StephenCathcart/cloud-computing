package uk.co.stephencathcart.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.azure.storage.table.TableServiceEntity;

/**
 * A Azure table entity which represents a speeding vehicle. The snapshot is
 * serialised to a json string to be stored in a row. A boolean is also stored
 * which lets us know if the snapshot is a priority. The partition key is a
 * string representing the priority level and the row key is the captured time
 * of when the snapshot was taken along with the vehicles registration which
 * will also be unique.
 *
 * @author Stephen Cathcart
 */
public class SpeedingVehicleEntity extends TableServiceEntity {

    private boolean isPriority;
    private String snapshot;

    public SpeedingVehicleEntity() {
    }

    public SpeedingVehicleEntity(boolean isPriority, Snapshot snapshot) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        this.isPriority = isPriority;
        this.snapshot = mapper.writeValueAsString(snapshot);
        this.partitionKey = isPriority ? "PRIORITY" : "NORMAL";
        this.rowKey = DateUtils.format(snapshot.getCaptureDate()) + "_" + snapshot.getRegistration();
    }

    public boolean isIsPriority() {
        return isPriority;
    }

    public void setIsPriority(boolean isPriority) {
        this.isPriority = isPriority;
    }

    public String getSnapshot() {
        return snapshot;
    }

    public void setSnapshot(String snapshot) {
        this.snapshot = snapshot;
    }
}
