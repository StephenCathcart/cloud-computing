package uk.co.stephencathcart.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.azure.storage.table.TableServiceEntity;

/**
 * A Azure table entity which represents a smart speed camera. The smart speed
 * camera is serialised to a json string to be stored in a row. The partition
 * key is the camera UID and the row key is the cameras start time which will
 * also be unique.
 *
 * @author Stephen Cathcart
 */
public class SmartSpeedCameraEntity extends TableServiceEntity {

    private String smartSpeedCamera;

    public SmartSpeedCameraEntity() {
    }

    public SmartSpeedCameraEntity(SmartSpeedCamera camera) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        this.smartSpeedCamera = mapper.writeValueAsString(camera);
        this.partitionKey = camera.getUid();
        this.rowKey = DateUtils.format(camera.getStartTime());
    }

    public String getSmartSpeedCamera() {
        return smartSpeedCamera;
    }

    public void setSmartSpeedCamera(String camera) {
        this.smartSpeedCamera = camera;
    }
}
