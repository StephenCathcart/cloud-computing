package uk.co.stephencathcart.smartspeedcamera;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.co.stephencathcart.common.SmartSpeedCamera;
import uk.co.stephencathcart.common.Snapshot;
import uk.co.stephencathcart.common.VehicleType;

/**
 * Spring service which contains the logic for capturing vehicles and
 * registering cameras. We also specify the logic of generating random vehicle
 * types and random speeds. The vehicle registrations are stored as a comma
 * separated string the the application.properties file rather than randomly
 * being generated as we need to have duplicates in the database for tracing
 * purposes.All values are stored in the application.properties file.
 *
 * @author Stephen Cathcart
 */
@Service
public class RecognitionSoftware {

    private static final Logger log = LoggerFactory.getLogger(RecognitionSoftware.class);

    @Autowired
    private TopicPublisher topicPublisher;

    @Value("#{'${app.test.registrations}'.split(',')}")
    private List<String> testRegistrations;

    private Boolean hasRegistered;
    private List<Snapshot> cachedSnapshots;

    public RecognitionSoftware() {
        this.hasRegistered = false;
        this.cachedSnapshots = new ArrayList<>();
    }

    /**
     * Sets the cameras start time to the present and tries to send a
     * registration message. If this fails we log to failure and wait for
     * another register request by the main application.
     */
    public void register(SmartSpeedCamera camera) {
        log.info("Registering: {}", camera);

        camera.setStartTime(new Date());
        if (topicPublisher.sendCameraRegistration(camera)) {
            setHasRegistered((Boolean) true);
        } else {
            log.error("Registration failed");
        }
    }

    /**
     * Creates a Snapshot by setting the relevant information about the vehicle
     * and also captures the details of the camera which captured it. We add
     * this to a cached snapshot list which is used in case the application is
     * offline. This list is then sent to the topic publisher to be sent to the
     * Service Bus.
     */
    public void capture(SmartSpeedCamera camera) {
        Snapshot snapshot = new Snapshot();
        snapshot.setVehicleType(generateRandomVehicleType());
        snapshot.setRegistration(testRegistrations.get((new Random()).nextInt(testRegistrations.size())));
        snapshot.setCurrentSpeed(generateRandomCurrentSpeed(camera));
        snapshot.setCamera(camera);
        snapshot.setSpeeding(snapshot.getCurrentSpeed() > snapshot.getCamera().getMaxSpeedLimit());
        snapshot.setCaptureDate(new Date());
        cachedSnapshots.add(snapshot);

        log.info("Creating: {}", snapshot);
        if (!topicPublisher.sendSnapshot(cachedSnapshots)) {
            log.error("Sending of snapshots failed. Snapshots will be stored in an offline list.");
        }
    }

    /**
     * Get a random vehicle type.
     */
    private VehicleType generateRandomVehicleType() {
        return VehicleType.values()[(int) (Math.random() * VehicleType.values().length)];
    }

    /**
     * Gets a random speed from the car which will be +/- half the speed of the
     * street limit. There is a 5% chance that the car will be speeding.
     */
    private int generateRandomCurrentSpeed(SmartSpeedCamera camera) {
        Random rand = new Random();
        int fluxSpeed = rand.nextInt(camera.getMaxSpeedLimit() / 2);
        int actualSpeed = camera.getMaxSpeedLimit();

        if (Math.random() < 0.05) {
            actualSpeed += fluxSpeed;
        } else {
            actualSpeed -= fluxSpeed;
        }
        return actualSpeed;
    }

    public Boolean getHasRegistered() {
        return hasRegistered;
    }

    public void setHasRegistered(Boolean hasRegistered) {
        this.hasRegistered = hasRegistered;
    }
}
