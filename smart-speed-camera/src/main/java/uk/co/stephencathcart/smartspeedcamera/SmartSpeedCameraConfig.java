package uk.co.stephencathcart.smartspeedcamera;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.co.stephencathcart.common.SmartSpeedCamera;

/**
 * Spring configuration class for configuring the smart speed camera. Values are
 * stored in the application.properties file.
 *
 * @author Stephen Cathcart
 */
@Configuration
public class SmartSpeedCameraConfig {

    @Value("${app.uid}")
    private String uid;

    @Value("${app.street}")
    private String street;

    @Value("${app.town}")
    private String town;

    @Value("${app.maxspeedlimit}")
    private Integer maxSpeedLimit;

    @Bean
    public SmartSpeedCamera smartSpeedCamera() {
        SmartSpeedCamera camera = new SmartSpeedCamera();
        camera.setUid(uid);
        camera.setStreet(street);
        camera.setTown(town);
        camera.setMaxSpeedLimit(maxSpeedLimit);
        return camera;
    }
}
